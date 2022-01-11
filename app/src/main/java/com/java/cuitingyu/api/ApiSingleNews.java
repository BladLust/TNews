package com.java.cuitingyu.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

class DateUtils {// DateUtils Class from https://www.cnblogs.com/shihaiming/p/7048511.html

    /**
     * 返回unix时间戳 (1970年至今的秒数) * @return
     */
    public static long getUnixStamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 得到昨天的日期 * @return
     */
    public static String getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String yestoday = sdf.format(calendar.getTime());
        return yestoday;
    }

    /**
     * 得到今天的日期 * @return
     */
    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        return date;
    }

    /**
     * 时间戳转化为时间格式 * @param timeStamp * @return
     */
    public static String timeStampToStr(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(timeStamp * 1000);
        return date;
    }

    /**
     * 得到日期   yyyy-MM-dd * @param timeStamp  时间戳 * @return
     */
    public static String formatDate(long timeStamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(timeStamp * 1000);
        return date;
    }

    /**
     * 得到时间  HH:mm:ss * @param timeStamp   时间戳 * @return
     */
    public static String getTime(long timeStamp) {
        String time = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(timeStamp * 1000);
        String[] split = date.split("\\s");
        if (split.length > 1) {
            time = split[1];
        }
        return time;
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前 * * @param timeStamp * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;
        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "未来？！";
        }
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，(多少分钟) * * @param timeStamp * @return
     */
    public static String timeStampToFormat(long timeStamp) {
        long curTime = System.currentTimeMillis() / (long) 1000;
        long time = curTime - timeStamp;
        return time / 60 + "";
    }
}

@Entity(tableName = "api_single_news")
public class ApiSingleNews {

    public static final int mDisplayImageless = 0;
    public static final int mDisplayOneImage = 1;
    public static final int mDisplayThreeOrMoreImage = 2;

    @SerializedName("image")
    @Expose
    @ColumnInfo(name="image")
    public String mImageUrlUncompiled;
    @Ignore
    public int mDisplayType = 0;
    @Ignore
    public int mImageUrlCount = -1;// Image url not yet compiled if this is -1;
    @Ignore
    public ArrayList<String> mImageUrlCompiled = new ArrayList<String>();
    @SerializedName("publishTime")
    @Expose
    @ColumnInfo(name="publishTime")
    public String mPublishTime;
    @SerializedName("publisher")
    @Expose
    @ColumnInfo(name="publisher")
    public String mPublisher;
    @SerializedName("keywords")
    @Expose
    @Ignore
    public ArrayList<ApiKeyword> mKeywords;
    @SerializedName("language")
    @Expose
    @Ignore
    public String mLanguage;
    @SerializedName("video")
    @Expose
    @ColumnInfo(name="video")
    public String mVideoUrl;

    @SerializedName("url")
    @Expose
    @Ignore
    public String mNewsUrl;
    @SerializedName("title")
    @Expose
    @ColumnInfo(name="title")
    public String mTitle;
    //    @SerializedName("when")
//    @Expose
//    public ArrayList<ApiRelatedTime> mRelatedTimes ;
    @SerializedName("content")
    @Expose
    @ColumnInfo(name="content")
    public String mContent;
    //    @SerializedName("persons")
//    @Expose
//    public ArrayList<ApiPerson> mPersons;
    @SerializedName("newsID")
    @Expose
    @PrimaryKey
    @NonNull
    public String mNewsID;
    //    @SerializedName("crawTime")
//    @Expose
//    public String mCrawTime;
//    @SerializedName("organizations")
//    @Expose
//    public ArrayList<ApiOrganization> mOrganizations;
//    @SerializedName("locations")
//    @Expose
//    public ArrayList<ApiLocation> mLocations;
//    @SerializedName("where")
//    @Expose
//    public ArrayList<ApiWhere> mWhereList;
    @SerializedName("category")
    @Expose
    @Ignore
    public String mCategory;
    @ColumnInfo(name="readTime",index = true)
    public long mReadTime=Long.MAX_VALUE;
    @ColumnInfo(name="favoriteTime",index = true)
    public long mFavoriteTime=Long.MAX_VALUE;

    //    @SerializedName("who")
//    @Expose
//    public ArrayList<ApiWho> mWho;
    public String getNewsUrl() {
        return mNewsUrl;
    }

    private void imageUrlCompile() {
        mImageUrlCount = 0;
        String parsingString = mImageUrlUncompiled.replace(" ", "").replace("[", "").replace("]", "");
        if (parsingString.length() < 10) {
            mImageUrlCompiled.clear();
            mDisplayType = mDisplayImageless;
        } else {
            String[] splitUrl = parsingString.split(",");
            for (String url :
                    splitUrl) {
                if (url.length() >= 10) {
                    mImageUrlCompiled.add(url);
                    mImageUrlCount++;
                }
            }
            if (mImageUrlCount > 2)
                mDisplayType = mDisplayThreeOrMoreImage;
            else if (mImageUrlCount > 0)
                mDisplayType = mDisplayOneImage;
        }
        return;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public int getDisplayType() {
        if (mImageUrlCount == -1)
            imageUrlCompile();
        return mDisplayType;
    }

    public ArrayList<String> getImageUrl() {
        if (mImageUrlCount == -1)
            imageUrlCompile();
        return mImageUrlCompiled;
    }

    public int getImageUrlCount() {
        if (mImageUrlCount == -1)
            imageUrlCompile();
        return mImageUrlCount;
    }

    public String getImageUrlUncompiled() {
        return mImageUrlUncompiled;
    }


    public String getPublishTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date publishTime = sdf.parse(mPublishTime);
            return DateUtils.convertTimeToFormat(publishTime.getTime() / 1000);
        } catch (ParseException e) {
        }

        return mPublishTime;
    }

    @Override
    public String toString() {
        return "ApiSingleNews{" +
                "mTitle='" + mTitle + '\'' +
                '}';
    }
    public void markRead(){
        mReadTime=System.currentTimeMillis();
        return;
    }
    public void markFavorited(){
        mFavoriteTime=System.currentTimeMillis();
        return;
    }
}
