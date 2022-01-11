package com.java.cuitingyu.newsList;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.cuitingyu.api.ApiResponse;
import com.java.cuitingyu.api.ApiSingleNews;
import com.java.cuitingyu.api.ApiSingleNewsDao;
import com.java.cuitingyu.api.ApiSingleNewsDatabase;
import com.java.cuitingyu.MainActivity;
import com.java.cuitingyu.NewsDetailActivity;
import com.example.tnews.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


class OneOrLessImageViewHolder extends RecyclerView.ViewHolder {
    public final CardView mCardView;
    public final ImageView mImageView;
    public final TextView mTitleTextView;
    public final TextView mSourceTextView;
    public final TextView mDateTextView;
    public ApiSingleNews mApiSingleNews = null;

    public OneOrLessImageViewHolder(@NonNull View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.news_item_card);
        mImageView = itemView.findViewById(R.id.news_item_image);
        mTitleTextView = itemView.findViewById(R.id.news_item_title);
        mSourceTextView = itemView.findViewById(R.id.news_item_source);
        mDateTextView = itemView.findViewById(R.id.news_item_date);
    }
}

class MultiImageViewHolder extends RecyclerView.ViewHolder {
    public final CardView mCardView;
    public final ImageView mImageView1;
    public final ImageView mImageView2;
    public final ImageView mImageView3;
    public final TextView mTitleTextView;
    public final TextView mSourceTextView;
    public final TextView mDateTextView;
    public ApiSingleNews mApiSingleNews = null;

    public MultiImageViewHolder(@NonNull View itemView) {
        super(itemView);
        mCardView = itemView.findViewById(R.id.news_item_multiimage_card);
        mImageView1 = itemView.findViewById(R.id.news_item_image1);
        mImageView2 = itemView.findViewById(R.id.news_item_image2);
        mImageView3 = itemView.findViewById(R.id.news_item_image3);
        mTitleTextView = itemView.findViewById(R.id.news_item_title);
        mSourceTextView = itemView.findViewById(R.id.news_item_source);
        mDateTextView = itemView.findViewById(R.id.news_item_date);
    }
}

public class NewsListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int READ_COLOR=0xFFDC515D;
    private static final String TAG = "NewsListRecyclerAdapter";
    ApiResponse mApiResponse;
    RecyclerView mRecyclerView;
    private WeakReference<ApiSingleNewsDatabase> mDb = null;
    ApiSingleNewsDao mApiSingleNewsDao = null;

    public NewsListRecyclerAdapter(ApiResponse inputResponse) {
        mApiResponse = inputResponse;
    }

    public void setData(ApiResponse newResponse) {
        if (newResponse != null) {
            mApiResponse = newResponse;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mDb = new WeakReference<ApiSingleNewsDatabase>(MainActivity.getInstance().getDb());
        mApiSingleNewsDao = mDb.get().mApiSingleNewsDao();
        RecyclerView.ViewHolder returnedViewHolder = null;
        switch (viewType) {
            case ApiSingleNews.mDisplayImageless:
            case ApiSingleNews.mDisplayOneImage:
                returnedViewHolder = new OneOrLessImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_single_image, parent, false));
                break;
            case ApiSingleNews.mDisplayThreeOrMoreImage:
                returnedViewHolder = new MultiImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item_multiimage, parent, false));
                break;
        }
        return returnedViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ApiSingleNews thisNews = mApiResponse.getNewsList().get(position);
        boolean newsRead =mApiSingleNewsDao.checkNewsRead(thisNews.mNewsID);
        switch (holder.getItemViewType()) {
            case ApiSingleNews.mDisplayImageless:
                OneOrLessImageViewHolder holder0 = (OneOrLessImageViewHolder) holder;
                LinearLayoutCompat.LayoutParams param = new LinearLayoutCompat.LayoutParams(
                        0,
                        0,
                        0.0f
                );
                holder0.mImageView.setLayoutParams(param);
                holder0.mTitleTextView.setText(thisNews.mTitle);
                holder0.mSourceTextView.setText(thisNews.getPublisher());
                holder0.mDateTextView.setText(thisNews.getPublishTime());
                holder0.mApiSingleNews = thisNews;
                if (newsRead)
                    holder0.mTitleTextView.setTextColor(READ_COLOR);
                holder0.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder0.mApiSingleNews == null)
                            return;
                        Intent gotoDetail = new Intent(holder0.mCardView.getContext(), NewsDetailActivity.class);
//                MarkRead
                        gotoDetail.putExtra("newsId", holder0.mApiSingleNews.mNewsID);
                        thisNews.markRead();
                        mApiSingleNewsDao.insertAll(thisNews);
                        holder0.mTitleTextView.setTextColor(READ_COLOR);
                        holder0.mCardView.getContext().startActivity(gotoDetail);
                    }
                });
                break;
            case ApiSingleNews.mDisplayOneImage:
                OneOrLessImageViewHolder holder1 = (OneOrLessImageViewHolder) holder;
                Picasso.get().load(thisNews.getImageUrl().get(0)).fit().centerCrop().into(holder1.mImageView);
                holder1.mTitleTextView.setText(thisNews.mTitle);
                holder1.mSourceTextView.setText(thisNews.getPublisher());
                holder1.mDateTextView.setText(thisNews.getPublishTime());
                holder1.mApiSingleNews = thisNews;
                if (newsRead)
                    holder1.mTitleTextView.setTextColor(READ_COLOR);
                holder1.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder1.mApiSingleNews == null)
                            return;
                        Intent gotoDetail = new Intent(holder1.mCardView.getContext(), NewsDetailActivity.class);
//                MarkRead
                        gotoDetail.putExtra("newsId", holder1.mApiSingleNews.mNewsID);
                        thisNews.markRead();
                        mApiSingleNewsDao.insertAll(thisNews);
                        holder1.mTitleTextView.setTextColor(READ_COLOR);
                        holder1.mCardView.getContext().startActivity(gotoDetail);
                    }
                });
                break;
            case ApiSingleNews.mDisplayThreeOrMoreImage:
                MultiImageViewHolder holder3 = (MultiImageViewHolder) holder;
                Picasso.get().load(thisNews.getImageUrl().get(0)).fit().centerCrop().into(holder3.mImageView1);
                Picasso.get().load(thisNews.getImageUrl().get(1)).fit().centerCrop().into(holder3.mImageView2);
                Picasso.get().load(thisNews.getImageUrl().get(2)).fit().centerCrop().into(holder3.mImageView3);
                holder3.mTitleTextView.setText(thisNews.mTitle);
                holder3.mSourceTextView.setText(thisNews.getPublisher());
                holder3.mDateTextView.setText(thisNews.getPublishTime());
                holder3.mApiSingleNews = thisNews;
                if (newsRead)
                    holder3.mTitleTextView.setTextColor(READ_COLOR);
                holder3.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder3.mApiSingleNews == null)
                            return;
                        Intent gotoDetail = new Intent(holder3.mCardView.getContext(), NewsDetailActivity.class);
                        gotoDetail.putExtra("newsId", holder3.mApiSingleNews.mNewsID);
                        thisNews.markRead();
                        mApiSingleNewsDao.insertAll(thisNews);
                        holder3.mTitleTextView.setTextColor(READ_COLOR);
                        holder3.mCardView.getContext().startActivity(gotoDetail);
                    }
                });
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return mApiResponse.getNewsList().get(position).getDisplayType();
    }

    @Override
    public int getItemCount() {
        return mApiResponse.getNewsCount();
    }
}
