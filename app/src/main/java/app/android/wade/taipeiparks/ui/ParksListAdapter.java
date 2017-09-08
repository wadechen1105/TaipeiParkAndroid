package app.android.wade.taipeiparks.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public void setItems(ArrayList<ParksInfo> data) {
        mData = data;
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
        vh.mTextView.setText(info.getParkName());
    }

    @Override
    public int getItemCount() {
        return (mData == null) ? 0 : mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout mExpandableLayout;

        ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.park_name);
            mExpandableLayout = v.findViewById(R.id.expandable_layout);
        }
    }
}
