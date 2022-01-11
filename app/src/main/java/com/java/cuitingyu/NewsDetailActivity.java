package com.java.cuitingyu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.tnews.R;
import com.java.cuitingyu.api.ApiSingleNews;
import com.java.cuitingyu.api.ApiSingleNewsDao;
import com.java.cuitingyu.api.ApiSingleNewsDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class DetailNewsImageHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;

    public DetailNewsImageHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.detail_image_item);
    }
}

class DetailNewsSectionHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;

    public DetailNewsSectionHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.detail_text_section);
    }
}

class DetailNewsRecyclerAdapter extends RecyclerView.Adapter {
    public static final int IMAGE_VIEWTYPE = 0;
    public static final int CONTENT_VIEWTYPE = 1;
    ArrayList<String> mDisplayList;

    public DetailNewsRecyclerAdapter(ArrayList<String> displayList) {
        mDisplayList = displayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == IMAGE_VIEWTYPE)
            return new DetailNewsImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_news_image, parent, false));
        return new DetailNewsSectionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_news_section, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == IMAGE_VIEWTYPE) {
            Picasso.get().load(mDisplayList.get(position)).transform(new Transformation() {//Transformation method by @lenamuit at https://gist.github.com/lenamuit/9ae1458b43fc2cf28db2
                @Override
                public Bitmap transform(Bitmap source) {
                    int targetWidth = ((DetailNewsImageHolder) holder).mImageView.getWidth();
                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int targetHeight = (int) (targetWidth * aspectRatio);
                    if (targetHeight <= 0 || targetWidth <= 0) return source;
                    Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                    if (result != source) {
                        // Same bitmap is returned if sizes are the same
                        source.recycle();
                    }
                    return result;
                }

                @Override
                public String key() {
                    return "key";
                }
            }).into(((DetailNewsImageHolder) holder).mImageView);

            return;
        }
        ((DetailNewsSectionHolder) holder).mTextView.setText(mDisplayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDisplayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mDisplayList.get(position).startsWith("http") || mDisplayList.get(position).startsWith("ftp")) ? IMAGE_VIEWTYPE : CONTENT_VIEWTYPE;
    }
}

public class NewsDetailActivity extends Activity {
    private static final String TAG = "MainActivity";
    private String mNewsContent;
    private TextView mTitleView;
    private TextView mPublishTimeView;
    private TextView mSourceView;
    private RecyclerView mRecyclerView;
    private Intent mIntent;
    private WeakReference<ApiSingleNewsDatabase> mDb = null;
    private ApiSingleNewsDao mApiSingleNewsDao;
    private ApiSingleNews mApiSingleNews;
    private ArrayList<String> mDisplayList = new ArrayList<String>();
    private DetailNewsRecyclerAdapter mDetailNewsRecyclerAdapter;
    private FloatingActionButton mFavoriteView;
    private VideoView mVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_news);
        mTitleView = findViewById(R.id.detail_title);
        mPublishTimeView = findViewById(R.id.detail_publish_time);
        mSourceView = findViewById(R.id.detail_source);
        mRecyclerView = findViewById(R.id.detail_recycler_view);
        mFavoriteView = findViewById(R.id.floating_like_button);
        mVideoView = findViewById(R.id.detail_video_view);
        mIntent = getIntent();
        mDb = new WeakReference<ApiSingleNewsDatabase>(MainActivity.getInstance().getDb());
        mApiSingleNewsDao = mDb.get().mApiSingleNewsDao();
        mApiSingleNews = mApiSingleNewsDao.getById(mIntent.getStringExtra("newsId"));
        if (mApiSingleNews.mVideoUrl == null || mApiSingleNews.mVideoUrl.equals("")) {
            LinearLayoutCompat.LayoutParams param = new LinearLayoutCompat.LayoutParams(
                    0,
                    0,
                    0.0f
            );
            mVideoView.setLayoutParams(param);
        }
        mPublishTimeView.setText(mApiSingleNews.getPublishTime());
        mSourceView.setText(mApiSingleNews.getPublisher());
        mTitleView.setText(mApiSingleNews.mTitle);
        boolean newsFavorite = mApiSingleNewsDao.checkNewsFavorite(mApiSingleNews.mNewsID);
        mFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApiSingleNews.mFavoriteTime < 10000000000000L) {
                    mApiSingleNews.mFavoriteTime = Long.MAX_VALUE;
                    mApiSingleNewsDao.insertAll(mApiSingleNews);
                    ((FloatingActionButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    return;
                }
                mApiSingleNews.markFavorited();
                mApiSingleNewsDao.insertAll(mApiSingleNews);
                ((FloatingActionButton) v).setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
            }
        });
        if (newsFavorite)
            mFavoriteView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24));
        String[] splitString = mApiSingleNews.mContent.split("\n\n");
        ArrayList<String> imageUrls = mApiSingleNews.getImageUrl();
        int i = 0;
        while (i < splitString.length || i < imageUrls.size()) {
            if (i < splitString.length)
                mDisplayList.add(splitString[i]);
            if (i < imageUrls.size())
                mDisplayList.add(imageUrls.get(i));
            ++i;
        }
        mDetailNewsRecyclerAdapter = new DetailNewsRecyclerAdapter(mDisplayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mDetailNewsRecyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mApiSingleNews.mVideoUrl != null && !mApiSingleNews.mVideoUrl.equals(""))
            initializePlayer();
    }

    private void initializePlayer() {//Reference : https://www.c-sharpcorner.com/article/play-video-from-internet-using-videoview/
        Uri videoUri = Uri.parse(mApiSingleNews.mVideoUrl);
        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                /*
                                 * add media controller
                                 */
                                MediaController mc = new MediaController(NewsDetailActivity.this);
                                mVideoView.setMediaController(mc);
                                mc.setMediaPlayer(mVideoView);
                                /*
                                 * and set its position on screen
                                 */
                                mc.setAnchorView(mVideoView);
                            }});

                        mVideoView.seekTo(1);
                        mVideoView.start();
                    }
                });
    }
}
