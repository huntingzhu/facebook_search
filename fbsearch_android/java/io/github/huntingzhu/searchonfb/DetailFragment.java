package io.github.huntingzhu.searchonfb;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// In this case, the fragment displays simple text based on the page
public class DetailFragment extends Fragment {
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final String ARG_JSON = "ARG_JSON";

    private int mPage;
    private String detailJSON;
    private JSONObject detailJSONObj;
    private JSONObject albumJSONObj;
    private JSONObject postJSONObj;
    private String detailID;
    private String detailName;
    private String detailProfile;


    public static DetailFragment newInstance(int page, String detailJSON) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_JSON, detailJSON);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPage = getArguments().getInt(ARG_PAGE);
        this.detailJSON = getArguments().getString(ARG_JSON);

        try {
            this.detailJSONObj = new JSONObject(detailJSON);

            // Parse album JSON
            if(!detailJSONObj.isNull("albums")) {
                this.albumJSONObj = detailJSONObj.getJSONObject("albums");
            } else {
                this.albumJSONObj = null;
            }
            // Parse post JSON
            if(!detailJSONObj.isNull("posts")) {
                this.postJSONObj = detailJSONObj.getJSONObject("posts");
            } else {
                this.postJSONObj = null;
            }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;

        switch (mPage) {
            case 0: // Fragment album
                view = inflater.inflate(R.layout.fragment_album, container, false);

                // Render album fragment
                renderAlbums(view);
                break;
            case 1: // Fragment post
                view = inflater.inflate(R.layout.fragment_post, container, false);

                // Render post fragment
                renderPosts(view);
                break;
            default:
                view = null;
                break;
        }

        return view;
    }

    // Render album fragment
    private void renderAlbums(View albumLayout) {
        TextView tv = (TextView) albumLayout.findViewById(R.id.no_album_text);
        // If albumJSON is not null
        if (albumJSONObj != null) {
            // Remove textview
            ((LinearLayout)albumLayout).removeView(tv);

            final ExpandableListView expandableListView;
            ExpandableListAdapter expandableListAdapter;
            List<String> expandableListTitle;
            HashMap<String, List<String>> expandableListDetail;
            AlbumExpandableListData albumData = new AlbumExpandableListData(albumJSONObj);

            expandableListView = (ExpandableListView) albumLayout.findViewById(R.id.expandableListView);
            expandableListDetail = albumData.getData();
            expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
            expandableListAdapter = new AlbumExpandableListAdapter(getContext(), expandableListTitle, expandableListDetail);
            expandableListView.setAdapter(expandableListAdapter);

            // Setup auto collapse
            expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;
                @Override
                public void onGroupExpand(int groupPosition) {
                    if(groupPosition != previousGroup)
                        expandableListView.collapseGroup(previousGroup);
                    previousGroup = groupPosition;

                }
            });
        } else {
            // albumJSON is null
        }
    }

    // Render post fragment
    private void renderPosts(View postLayout) {
        TextView tv = (TextView) postLayout.findViewById(R.id.no_post_text);
        ListView postListView;

        // If postJSON is not null
        if (postJSONObj != null) {
            // Remove textview
            ((LinearLayout)postLayout).removeView(tv);

            String postName = detailName;
            String postProfile = detailProfile;
            ArrayList<String> postTime = new ArrayList<>();
            ArrayList<String> postContent = new ArrayList<>();

            try {
                JSONArray postArr = postJSONObj.getJSONArray("data");
                for(int i = 0; i < postArr.length(); i++) {
                    JSONObject postItem = postArr.getJSONObject(i);
                    if(!postItem.isNull("created_time")) {
                        postTime.add(postItem.getString("created_time"));
                    }
                    if(!postItem.isNull("message")) {
                        postContent.add(postItem.getString("message"));
                    } else {
                        postContent.add("No message");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i("Post Content", postContent.toString());

            PostListAdapter postListAdapter = new PostListAdapter(getActivity(), postName, postProfile, postTime, postContent);
            postListView = (ListView) postLayout.findViewById(R.id.listview_post);
            postListView.setAdapter(postListAdapter);

        } else {
            // postJSON is null
        }
    }

}
