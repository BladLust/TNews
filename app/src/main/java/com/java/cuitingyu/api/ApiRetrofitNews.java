package com.java.cuitingyu.api;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRetrofitNews {

    @GET("svc/news/queryNewsList")
    Call<ApiResponse> getResponse(@Query("size")int size,@Query("startDate")String startDate,@Query("endDate")String endDate,@Query("words")String words,@Query("categories")String categories);

}
