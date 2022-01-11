package com.java.cuitingyu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tnews.R;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

class PopupItemViewHolder extends RecyclerView.ViewHolder{
    public final ImageView mImageView;
    public final TextView mTextView;
    String displayString=null;

    public PopupItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.popup_item_image);
        mTextView = itemView.findViewById(R.id.popup_item_text);
    }
}

public class PopupRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<String> mDisplayArray;
    public PopupRecyclerAdapter() {
        mDisplayArray =MainActivity.mCustomCategories;
        mDisplayArray.add("全部频道");
        for (String cat:MainActivity.categories
             ) {
            if(!mDisplayArray.contains(cat)){
                mDisplayArray.add(cat);
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popup_recycler_item, parent, false);
        return new PopupItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PopupItemViewHolder viewHolder=(PopupItemViewHolder)holder;
        String categoryName=mDisplayArray.get(position);
        viewHolder.mTextView.setText(categoryName);
        if(categoryName.endsWith("频道")){
            LinearLayoutCompat.LayoutParams param = new LinearLayoutCompat.LayoutParams(
                    0,
                    0,
                    0.0f
            );
            viewHolder.mImageView.setLayoutParams(param);
            viewHolder.mTextView.setTextColor(0xFFDC515D);
        }
    }

    public int getItemCount() {
        return MainActivity.categories.length+1;
    }

    public void onMoveDone() {
        MainActivity.mCustomCategories=new ArrayList<>();
        for (String cat:mDisplayArray
             ) {
            if(cat.endsWith("频道")){
                break;
            }
            MainActivity.mCustomCategories.add(cat);
        }
        MainActivity.getInstance().resetTabData();
    }

    public void onItemMove(int from, int to) {
        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(mDisplayArray, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(mDisplayArray, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }
    public void onItemDismiss(int position) {

    }

    public static class ItemTouchHelperCallbackClass extends ItemTouchHelper.Callback{
        private final PopupRecyclerAdapter mAdapter;

        public ItemTouchHelperCallbackClass(
                PopupRecyclerAdapter adapter) {
            mAdapter = adapter;
        }
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            String category = ((PopupItemViewHolder)viewHolder).mTextView.getText().toString();
            return makeMovementFlags((category.endsWith("频道")?0:(ItemTouchHelper.UP|ItemTouchHelper.DOWN)),0);

        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            mAdapter.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }
        @Override
        public void clearView( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            // Action finished
            mAdapter.onMoveDone();
        }
    }
}
