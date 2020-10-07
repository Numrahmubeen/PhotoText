package com.jobs.phototext.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jobs.phototext.R;
import com.jobs.phototext.listener.FilterListener;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class FilterViewAdapter extends RecyclerView.Adapter<FilterViewAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private FilterListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_Thumbnail;
        TextView tv_FilterName;

        public MyViewHolder(View view) {
            super(view);
         iv_Thumbnail  = view.findViewById(R.id.iv_Thumbnail);
         tv_FilterName  = view.findViewById(R.id.tv_FilterName);
        }
    }


    public FilterViewAdapter(Context context, List<ThumbnailItem> thumbnailItemList, FilterListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filter_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

        holder.iv_Thumbnail.setImageBitmap(thumbnailItem.image);

        holder.iv_Thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        holder.tv_FilterName.setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            holder.tv_FilterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_selected));
        } else {
            holder.tv_FilterName.setTextColor(ContextCompat.getColor(mContext, R.color.filter_label_normal));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }
}
