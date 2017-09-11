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
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

import app.android.wade.taipeiparks.ParksInfo;
import app.android.wade.taipeiparks.R;

public class ParksListAdapter extends RecyclerView.Adapter {
    private final View.OnClickListener mOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LinearLayout l = view.findViewById(R.id.expandable_layout);
            boolean isShow = (boolean) view.getTag();
            translateYAnimator(l, isShow).start();
            l.setVisibility(isShow ? View.VISIBLE : View.GONE);
            view.setTag(!isShow);
        }
    };

    private boolean preLoading = false;
    private ArrayList<ParksInfo> mData;
    private HashMap<String,Bitmap> mThumbnailCache = new HashMap<>();

    public ParksListAdapter(Context context) {
        initImageLoader(context);
    }

    public void setItems(ArrayList<ParksInfo> data) {
        mData = data;
    }

    private ObjectAnimator translateYAnimator(final View target, boolean isExpand) {
        ObjectAnimator animator;
        if (isExpand) {
            animator = ObjectAnimator.ofFloat(target, "translationY", 0, 150, -30, 0);
        } else {
            animator = ObjectAnimator.ofFloat(target, "translationY", 0, -100);
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

    /**
     * async download image
     * @param VH
     */
    private void loadImage(final ViewHolder VH) {
        final ImageLoader loader = ImageLoader.getInstance();
        loader.loadImage(VH.mThumbnailURL, new ImageSize(60, 60), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.d("onLoadingStarted", "p = "+VH.mPosition);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if(mThumbnailCache.containsKey(VH.mThumbnailURL)) {
                    return;
                }
                mThumbnailCache.put(VH.mThumbnailURL, bitmap);

                if (preLoading) {
                    return;
                }
                if (mThumbnailCache.size() > 3) {
                    notifyDataSetChanged();
                    preLoading = true;
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.park_info_item, parent, false);
        v.setOnClickListener(mOnItemClickListener);
        v.setTag(true);
        ViewHolder VH = new ViewHolder(v);
        return VH;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder VH = (ViewHolder) holder;
        ParksInfo info = mData.get(position);
        VH.mParkName.setText(info.getParkName());
        VH.mSpotName.setText(info.getViewSpot());
        VH.mDiscription.setText(info.getIntroduction());
        VH.mThumbnailURL = info.getImageUrl();
        VH.mPosition = position;

        Bitmap bitmap = mThumbnailCache.get(VH.mThumbnailURL);
        if (bitmap == null) {
            VH.mThumbnailView.setImageBitmap(null);
            loadImage(VH);
        } else {
            VH.mThumbnailView.setImageBitmap(bitmap);
        }

    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView mParkName, mDiscription, mSpotName;
        ImageView mThumbnailView;
        LinearLayout mExpandableLayout;
        String mThumbnailURL;
        int mPosition;

        ViewHolder(View v) {
            super(v);
            mParkName = v.findViewById(R.id.park_name);
            mExpandableLayout = v.findViewById(R.id.expandable_layout);
            mThumbnailView = v.findViewById(R.id.view_spot_thumb);
            mSpotName = v.findViewById(R.id.view_spot_name);
            mDiscription = v.findViewById(R.id.view_spot_description);
        }
    }

}
