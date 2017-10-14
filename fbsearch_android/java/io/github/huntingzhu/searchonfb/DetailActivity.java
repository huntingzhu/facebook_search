package io.github.huntingzhu.searchonfb;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_DETAILID = "io.github.huntingzhu.searchonfb.DETAILID";
    public static final String EXTRA_TYPE = "io.github.huntingzhu.searchonfb.TYPE";
    private String detailID;
    private String detailJSON;
    private JSONObject detailJSONObj;
    private String detailName;
    private String detailProfile;
    private String detailType;

    private TabLayout detailTabLayout;
    private ViewPager detailViewPager;
    private int[] tabIcons = {
            R.drawable.albums,
            R.drawable.posts
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get detailID
        Intent intent = getIntent();
        detailID = intent.getStringExtra(EXTRA_DETAILID);
        detailType = intent.getStringExtra(EXTRA_TYPE);


        // Send request to get detail JSON
        String detailUrl = constructDetailURL(detailID);
        Log.i("Detail-Request-URL", detailUrl);
        detailJSON = JSONRequestTask.getJSONStr(detailUrl);
        fetchDetail();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        detailViewPager = (ViewPager) findViewById(R.id.viewpager_detail);
        detailViewPager.setAdapter(new DetailPagerAdapter(getSupportFragmentManager(), DetailActivity.this));

        // Give the TabLayout the ViewPager
        detailTabLayout = (TabLayout) findViewById(R.id.sliding_tabs_detail);
        detailTabLayout.setupWithViewPager(detailViewPager);

        // Set up the icons of tabs;
        setupTabIcons();

//        Toast.makeText(this, detailID, Toast.LENGTH_SHORT).show();

        //

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_option_button, menu);
        MenuItem addRemove = menu.findItem(R.id.action_add_remove);

        if(LocalStorage.contains(DetailActivity.this, detailID, detailType)) {
            addRemove.setTitle(R.string.action_remove_favor);
        } else {
            addRemove.setTitle(R.string.action_add_favor);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_remove) {
            if(LocalStorage.contains(DetailActivity.this, detailID, detailType)) {
                LocalStorage.remove(DetailActivity.this, detailID, detailType);
                item.setTitle(R.string.action_add_favor);
                Toast.makeText(DetailActivity.this, R.string.toast_remove_text,Toast.LENGTH_SHORT).show();
                return true;
            } else {
                JSONObject jsonToSave = new JSONObject();
                try {
                    jsonToSave.put("name", detailName);
                    jsonToSave.put("id", detailID);
                    jsonToSave.put("profile", detailProfile);
                    jsonToSave.put("type", detailType);

                    LocalStorage.put(DetailActivity.this, detailID,jsonToSave.toString(),detailType);
                    item.setTitle(R.string.action_remove_favor);
                    Toast.makeText(DetailActivity.this, R.string.toast_add_text,Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else if (id == R.id.action_share) {
            CallbackManager callbackManager = CallbackManager.Factory.create();
            ShareDialog shareDialog = new ShareDialog(this);

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(DetailActivity.this, "You shared this post.", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancel() {}

                @Override
                public void onError(FacebookException error) {}
            });

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent shareContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(detailProfile))
                        .setImageUrl(Uri.parse(detailProfile))
                        .setContentTitle(detailName)
                        .setContentDescription("FB SEARCH FROM USC CSCI571")
                        .build();

                shareDialog.show(shareContent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Construct url for detail request
    private String constructDetailURL(String detailID) {
        String requestURL = "http://sample-env.samqhdps4g.us-west-2.elasticbeanstalk.com/fbsearch/fetchJSONAndroid.php?";

        requestURL += "detailID=" + detailID;

        return requestURL;
    }

    // Add icons to Tab Layout
    private void setupTabIcons() {
        detailTabLayout.getTabAt(0).setIcon(tabIcons[0]);
        detailTabLayout.getTabAt(1).setIcon(tabIcons[1]);

    }

    // Define my DetailPagerAdapter
    private class DetailPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private Context context;
        private String tabTitles[] = new String[] {
                "Albums",
                "Posts"
        };

        public DetailPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return DetailFragment.newInstance(position, detailJSON);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    // Parse detailJSON to get info
    private void fetchDetail() {
        try {
            this.detailJSONObj = new JSONObject(detailJSON);

            // Parse detail information
            if(!detailJSONObj.isNull("id")) {
                this.detailID= detailJSONObj.getString("id");
            }
            if(!detailJSONObj.isNull("name")) {
                this.detailName= detailJSONObj.getString("name");
            }
            if(!detailJSONObj.isNull("picture")) {
                this.detailProfile= detailJSONObj.getJSONObject("picture").getJSONObject("data").getString("url");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Set option menu
    private void setOptionMenu() {
        MenuItem addRemove = (MenuItem) findViewById(R.id.action_add_remove);

        if(LocalStorage.contains(DetailActivity.this, detailID, detailType)) {
            addRemove.setTitle(R.string.action_remove_favor);
        } else {
            addRemove.setTitle(R.string.action_add_favor);
        }
    }
}
