package io.github.huntingzhu.searchonfb;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


public class ResultActivity extends AppCompatActivity {

    private String keywords;
    private ArrayList<String> location;
    private String longitude;
    private String latitude;
    private String resultJSON;

    private TabLayout resultTabLayout;
    private ViewPager resultViewPager;
    private int[] tabIcons = {
            R.drawable.users,
            R.drawable.pages,
            R.drawable.events,
            R.drawable.places,
            R.drawable.groups
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get keywords and location from MainActivity
        Intent intent = getIntent();
        keywords = intent.getStringExtra(MainActivity.EXTRA_KEYWORDS);
        location = intent.getStringArrayListExtra(MainActivity.EXTRA_LOCATION);
        longitude = location.get(0);
        latitude = location.get(1);

        // Send request to get JSON Object
        String resultUrl = constructResultURL();
        Log.i("RequestURL", resultUrl);
        resultJSON = JSONRequestTask.getJSONStr(resultUrl);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        resultViewPager = (ViewPager) findViewById(R.id.viewpager);
        resultViewPager.setAdapter(new ResultPagerAdapter(getSupportFragmentManager(), ResultActivity.this));

        // Give the TabLayout the ViewPager
        resultTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        resultTabLayout.setupWithViewPager(resultViewPager);

        // Set up the icons of tabs;
        setupTabIcons();

    }


    // Construct URL to request JSON
    private String constructResultURL() {
        String requestURL = "http://sample-env.samqhdps4g.us-west-2.elasticbeanstalk.com/fbsearch/fetchJSONAndroid.php?";

        requestURL += "keyword=" + keywords;

        if(latitude != null && !latitude.isEmpty() && longitude != null && !longitude.isEmpty()) {
            requestURL += "&lat=" + latitude;
            requestURL += "&lng=" + longitude;
        }

        return requestURL;
    }

    // Add icons to Tab Layout
    private void setupTabIcons() {
        resultTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        resultTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        resultTabLayout.getTabAt(2).setIcon(tabIcons[2]);
        resultTabLayout.getTabAt(3).setIcon(tabIcons[3]);
        resultTabLayout.getTabAt(4).setIcon(tabIcons[4]);

    }

    // Define my ResultPagerAdapter
    private class ResultPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 5;
        private Context context;
        private String tabTitles[] = new String[] {
                "Users",
                "Pages",
                "Events",
                "Places",
                "Groups"
        };

        public ResultPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return PageFragment.newInstance(position, resultJSON);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}

