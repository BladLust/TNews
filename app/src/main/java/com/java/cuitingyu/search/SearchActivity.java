package com.java.cuitingyu.search;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.tnews.R;
import com.java.cuitingyu.newsList.NewsListItemFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

public class SearchActivity extends AppCompatActivity {

    private Button mLateTimeView;
    private Button mEarlyTimeView;
    private TextInputLayout mDropdownView;
    public static final int START_DATE = 1;
    public static final int END_DATE = -1;
    public static final int UNSELECTED = 0;
    private int mIsSettingStartingDate = 0;
    private String mStartDateTime = "";
    private String mEndDateTime = "";
    private AutoCompleteTextView mAutotext;
    private SearchView mSearchView;
    private ConstraintLayout mRootView;

    public static class DatePickerFragment extends DialogFragment// From official Android library example
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public static String formDateTimeString(int year, int month, int day) {
            String returnString = ((Integer) year).toString() + "-";
            if (month < 10)
                returnString += "0";
            returnString += ((Integer) month).toString() + "-";
            if (day < 10)
                returnString += "0";
            return returnString + ((Integer) day).toString();
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;
            SearchActivity instance = (SearchActivity) getContext();
            if (instance.mIsSettingStartingDate == instance.START_DATE) {
                String DateString = formDateTimeString(year, month, day);
                instance.mEarlyTimeView.setText(DateString);
                instance.mStartDateTime = DateString;
            }
            if (instance.mIsSettingStartingDate == instance.END_DATE) {
                String DateString = formDateTimeString(year, month, day);
                instance.mLateTimeView.setText(DateString);
                instance.mEndDateTime = DateString;
            }
            return;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list_activity);
        mDropdownView = findViewById(R.id.news_search_dropdown);
        mEarlyTimeView = findViewById(R.id.news_search_earliest_time);
        mLateTimeView = findViewById(R.id.news_search_latest_time);
        mAutotext = findViewById(R.id.news_search_AutocompleteText);
        mSearchView = findViewById(R.id.news_search_bar);
        mRootView = findViewById(R.id.search_view_root);
        mSearchView.setIconified(false);
        mEarlyTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSettingStartingDate = START_DATE;
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "earlyDatePicker");
            }
        });
        mLateTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSettingStartingDate = END_DATE;
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "lateDatePicker");
            }
        });
        String[] categories= {"任意","娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "科技", "社会"};
        ArrayAdapter<String> dropDownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categories);
        mAutotext.setAdapter(dropDownAdapter);
        int searchBarId = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        View searchBar = mSearchView.findViewById(searchBarId);
        ((EditText)searchBar).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch(v.getText().toString());
                    return true;
                }
                return false;
            }
        });

    }
    public void startSearch(String keyword){
        Bundle args = new Bundle();
        args.putString("category", mDropdownView.getEditText().getText().toString());
        args.putString("startTime",mStartDateTime);
        args.putString("endTime",mEndDateTime);
        args.putString("keyword",keyword);
        args.putInt("baseSize",50);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.news_search_frameholder, NewsListItemFragment.class, args)
                .commit();
        InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRootView.requestFocus();
    }
}
