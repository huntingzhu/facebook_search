package io.github.huntingzhu.searchonfb;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class FavoriteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TabLayout favorTabLayout;
    private ViewPager favorViewPager;
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
        setContentView(R.layout.activity_favorite);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_favorite);
        setSupportActionBar(toolbar);

        // Setup drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_favor);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Setup navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_favorite);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_favorites);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        favorViewPager = (ViewPager) findViewById(R.id.viewpager_favorite);
        favorViewPager.setAdapter(new FavorPagerAdapter(getSupportFragmentManager(), FavoriteActivity.this));

        // Give the TabLayout the ViewPager
        favorTabLayout = (TabLayout) findViewById(R.id.sliding_tabs_favorite);
        favorTabLayout.setupWithViewPager(favorViewPager);

        // Set up the icons of tabs;
        setupTabIcons();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_favor);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the home action
            Intent intentMain = new Intent(this, MainActivity.class);
            startActivity(intentMain);
        } else if (id == R.id.nav_favorites) {
            // Handle the favorites action
            onBackPressed();
        } else if (id == R.id.nav_about_me) {
            // Handle the favorites action
            Intent intentAboutMe = new Intent(this, AboutMeActivity.class);
            startActivity(intentAboutMe);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_favor);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Add icons to Tab Layout
    private void setupTabIcons() {
        favorTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        favorTabLayout.getTabAt(1).setIcon(tabIcons[1]);
        favorTabLayout.getTabAt(2).setIcon(tabIcons[2]);
        favorTabLayout.getTabAt(3).setIcon(tabIcons[3]);
        favorTabLayout.getTabAt(4).setIcon(tabIcons[4]);

    }

    // Define my FavorPagerAdapter
    private class FavorPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 5;
        private Context context;
        private String tabTitles[] = new String[] {
                "Users",
                "Pages",
                "Events",
                "Places",
                "Groups"
        };

        public FavorPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return FavoriteFragment.newInstance(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
