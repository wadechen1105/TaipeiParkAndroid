package app.android.wade.taipeiparks.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import app.android.wade.taipeiparks.ParksInfo;
import app.android.wade.taipeiparks.R;

public class ParksListAdapter extends RecyclerView.Adapter {
    private static int NOTIFY_DATACHANGED_THRESHOLD = 20;
    final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout l = view.findViewById(R.id.expandable_layout);
            RelativeLayout header = view.findViewById(R.id.header_layout);
            int headerHeight = header.getHeight();
            boolean isShow = (boolean) view.getTag();
            translateYAnimator(l, isShow).start();
            l.setVisibility(isShow ? View.VISIBLE : View.GONE);
            view.setTag(!isShow);
        }
    };

    private ArrayList<ParksInfo> mData;
    private int mCurrentDataChangedCount = 0;
    private int mLoopCount = 1;

    public ParksListAdapter(Context context) {
        initImageLoader(context);
    }

    public void setItems(ArrayList<ParksInfo> data) {
        mData = data;
    }

    private ObjectAnimator translateYAnimator(final View target, boolean isExpand) {
        ObjectAnimator animator;
        if (isExpand) {
            animator = ObjectAnimator.ofFloat(target, "translationY",0, 150, -30,0);
        } else {
            animator = ObjectAnimator.ofFloat(target, "translationY",0, -100);
            Log.d("Back","ddd");
        }

        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    private void initImageLoader(Context context) {
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

    private void loadImage(final ParksInfo info) {
        final ImageLoader loader = ImageLoader.getInstance();

        loader.loadImage(info.getImageUrl(), new ImageSize(60, 60), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String uri, View view) {

            }

            @Override
            public void onLoadingFailed(String uri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String uri, View view, Bitmap bitmap) {
                mCurrentDataChangedCount += 1;
                info.setThumbnail(bitmap);
                if(mCurrentDataChangedCount > NOTIFY_DATACHANGED_THRESHOLD * mLoopCount) {
                    ParksListAdapter.this.notifyDataSetChanged();
                    mLoopCount += 1;
                } else if (mCurrentDataChangedCount >= mData.size()) {
                    ParksListAdapter.this.notifyDataSetChanged();
                    mCurrentDataChangedCount = 0;
                    mLoopCount = 1;
                }

                Log.d("LoadImage",
                        "onLoadingComplete : " +
                                "currentCount :" + mCurrentDataChangedCount +
                                ", LoopCount = " + mLoopCount +
                                ", total size = " + mData.size());
            }

            @Override
            public void onLoadingCancelled(String uri, View view) {

            }
        });

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

        if(info.getThumbnail() != null) {
            vh.mThumbnail.setImageBitmap(info.getThumbnail());
        }

    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }

    public void loadDataAndDisplayView() {
        notifyDataSetChanged();
        for (ParksInfo info : mData) {
            loadImage(info);
        }
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
