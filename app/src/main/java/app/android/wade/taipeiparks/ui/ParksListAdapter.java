package app.android.wade.taipeiparks.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;

import app.android.wade.taipeiparks.ParksInfo;
import app.android.wade.taipeiparks.R;

public class ParksListAdapter extends RecyclerView.Adapter {
    final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout l = view.findViewById(R.id.expandable_layout);
            boolean isShow = (boolean) view.getTag();
            l.setVisibility(isShow ? View.VISIBLE : View.GONE);
            view.setTag(!isShow);
        }
    };
    private ArrayList<ParksInfo> mData;

    public ParksListAdapter(Context context) {
        initImageLoader(context);
    }

    public void setItems(ArrayList<ParksInfo> data) {
        mData = data;
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//            config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    private void loadImage(String fileUrl, ImageView imageView) {
        ImageLoader loader = ImageLoader.getInstance();
        loader.displayImage(fileUrl, imageView, new ImageSize(60,60));
    }

    public void stopLoadImage() {
        ImageLoader loader = ImageLoader.getInstance();
        loader.stop();
    }

    public void resumeLoadImage() {
        ImageLoader loader = ImageLoader.getInstance();
        loader.resume();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_info_item, parent, false);
        v.setOnClickListener(mOnItemClickListener);
        v.setTag(true);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder vh = (ViewHolder) holder;
        ParksInfo info = mData.get(position);
        vh.mParkName.setText(info.getParkName());
        vh.mSpotName.setText(info.getViewSpot());
        vh.mDiscription.setText(info.getIntroduction());
        loadImage(info.getImageUrl(), vh.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mParkName, mDiscription, mSpotName;
        ImageView mThumbnail;
        LinearLayout mExpandableLayout;

        ViewHolder(View v) {
            super(v);
            mParkName = v.findViewById(R.id.park_name);
            mExpandableLayout = v.findViewById(R.id.expandable_layout);
            mThumbnail = v.findViewById(R.id.view_spot_thumb);
            mSpotName = v.findViewById(R.id.view_spot_name);
            mDiscription = v.findViewById(R.id.view_spot_description);
        }

    }
}
