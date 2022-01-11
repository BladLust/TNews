package com.java.cuitingyu.api;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ApiSingleNews.class},version = 2)
public abstract class ApiSingleNewsDatabase extends RoomDatabase {
    public abstract ApiSingleNewsDao mApiSingleNewsDao();
}
