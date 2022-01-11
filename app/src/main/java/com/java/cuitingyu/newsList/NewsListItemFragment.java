package com.java.cuitingyu.newsList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.java.cuitingyu.api.ApiResponse;
import com.java.cuitingyu.api.ApiRetrofitNews;
import com.example.tnews.R;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsListItemFragment extends Fragment {
    private static final String TAG = "NewsListItemFragment";
    private static final String[] categories = {"娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
    private Retrofit mRetrofit;
    private ApiRetrofitNews mApiRetrofitNews;
    private XRecyclerView mXRecyclerView;
    private NewsListRecyclerAdapter mNewsListRecyclerAdapter;
    private LinearLayout.LayoutParams mDisplayParam;
    private LinearLayout.LayoutParams mInvisibleParam;
    boolean mRefreshing = false;
    boolean mLoadingMore = false;
    public Callback<ApiResponse> mLoadingCallBack;
    private int mBaseSize;
    private String mCategory;
    private String mStartTime;
    private String mEndTime;
    private String mKeyword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLoadingCallBack = new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (mRefreshing) {
                    mXRecyclerView.refreshComplete();
                    mRefreshing = false;
                }
                if (mLoadingMore) {
                    mXRecyclerView.loadMoreComplete();
                    mLoadingMore = false;
                }
                ApiResponse apiResponse = response.body();
                Log.d(TAG, apiResponse != null ? apiResponse.toString() : "null");
                mNewsListRecyclerAdapter.setData(apiResponse);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                t.printStackTrace();
            }
        };
        mRetrofit = new Retrofit.Builder().baseUrl("https://api2.newsminer.net/").addConverterFactory(GsonConverterFactory.create()).build();
        mApiRetrofitNews = mRetrofit.create(ApiRetrofitNews.class);
        mDisplayParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.0f
        );
        mInvisibleParam = new LinearLayout.LayoutParams(0, 0, 0f);

        return inflater.inflate(R.layout.news_list_fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBaseSize = getArguments().getInt("baseSize");
        mCategory = getArguments().getString("category");
        mStartTime = getArguments().getString("startTime");
        mEndTime = getArguments().getString("endTime");
        mKeyword = getArguments().getString("keyword");
        mXRecyclerView = view.findViewById(R.id.news_list_xrecycler_view);
        mNewsListRecyclerAdapter = new NewsListRecyclerAdapter(new ApiResponse());
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);
        mXRecyclerView.setAdapter(mNewsListRecyclerAdapter);
        mXRecyclerView.setLoadingMoreEnabled(true);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mXRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        if (mCategory.equals("主页") || mCategory.equals("暂无") || mCategory.equals("任意"))
            mCategory = "";
        if (mCategory.equals("教育") && mBaseSize < 100) {
            mBaseSize = 100;
        }
        if (mCategory.equals("汽车") && mBaseSize < 200) {
            mBaseSize = 200;
        }
        Call<ApiResponse> call = mApiRetrofitNews.getResponse(mBaseSize, mStartTime, mEndTime, mKeyword, mCategory);
        call.enqueue(mLoadingCallBack);
        mXRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                int prevNewsCount = mNewsListRecyclerAdapter.mApiResponse.getPageSize();
                Call<ApiResponse> call = mApiRetrofitNews.getResponse(prevNewsCount == 0 ? mBaseSize : prevNewsCount, mStartTime, mEndTime, mKeyword, mCategory);
                call.enqueue(mLoadingCallBack);
                mRefreshing = true;
            }

            @Override
            public void onLoadMore() {
                int prevNewsCount = mNewsListRecyclerAdapter.mApiResponse.getPageSize();
                Call<ApiResponse> call = mApiRetrofitNews.getResponse(prevNewsCount == 0 ? mBaseSize : prevNewsCount + 15, mStartTime, mEndTime, mKeyword, mCategory);
                call.enqueue(mLoadingCallBack);
                mLoadingMore = true;
            }
        });
    }
}
