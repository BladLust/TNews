package com.java.cuitingyu.api;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ApiSingleNewsDao {
    @Query("SELECT * FROM api_single_news")
    List<ApiSingleNews> getAll();

    @Query("SELECT * FROM api_single_news WHERE mNewsId IN (:newsPieceIds)")
    List<ApiSingleNews> loadAllByIds(int[] newsPieceIds);

    @Query("SELECT * FROM api_single_news WHERE mNewsId=:newsId")
    ApiSingleNews getById(String newsId);

    @Query("SELECT EXISTS(SELECT * FROM api_single_news WHERE mNewsId =:newsId)")
    boolean checkNewsRead(String newsId);
    @Query("SELECT EXISTS(SELECT * FROM api_single_news WHERE favoriteTime<10000000000000 AND mNewsId =:newsId)")
    boolean checkNewsFavorite(String newsId);

    @Query("SELECT * FROM api_single_news WHERE readTime<10000000000000 ORDER BY readTime DESC")
    List<ApiSingleNews> loadByRead();
    @Query("SELECT * FROM api_single_news WHERE favoriteTime<10000000000000 ORDER BY favoriteTime DESC")
    List<ApiSingleNews> loadByFavorite();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ApiSingleNews... newsPieces);

    @Delete
    void delete(ApiSingleNews newsPiece);
}
