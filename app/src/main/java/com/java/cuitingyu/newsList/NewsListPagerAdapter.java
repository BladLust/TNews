package com.java.cuitingyu.newsList;
import android.os.Bundle;

import com.java.cuitingyu.MainActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NewsListPagerAdapter extends FragmentStateAdapter {
    ArrayList<String> mCategories;

    public void setData(ArrayList<String> data){
        mCategories=data;
        notifyDataSetChanged();
    }

    public NewsListPagerAdapter(@NonNull FragmentManager fm, Lifecycle lc, ArrayList<String> categories) {
        super(fm,lc);
        mCategories=categories;
    }

    @Override
    public long getItemId(int position) {
        int i=0;
        for (String cat: MainActivity.categories
             ) {
           if(mCategories.get(position).equals(cat)){
               return i;
           }
           i++;
        }
        return 0;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new NewsListItemFragment();
        Bundle args = new Bundle();
        args.putString("category", mCategories.get(position));
        args.putString("startTime","");
        args.putString("endTime","");
        args.putString("keyword","");
        args.putInt("baseSize",50);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }
}
