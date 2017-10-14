package io.github.huntingzhu.searchonfb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String ARG_JSON = "ARG_JSON";

    private int mPage;
    private String resultJSON;
    private JSONObject resultJSONObj;
    private JSONObject userJSON;
    private JSONObject pageJSON;
    private JSONObject eventJSON;
    private JSONObject placeJSON;
    private JSONObject groupJSON;

    private ArrayList<String> itemName = new ArrayList<>();
    private ArrayList<String> profileURL = new ArrayList<>();
    private ArrayList<String> detailID = new ArrayList<>();

    private ResultListAdapter listViewAdapter;


    public static PageFragment newInstance(int page, String resultJSON) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        args.putString(ARG_JSON, resultJSON);

        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mPage = getArguments().getInt(ARG_PAGE);
        this.resultJSON = getArguments().getString(ARG_JSON);

        try {
            this.resultJSONObj = new JSONObject(resultJSON);
            parseJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        int listviewID;
        int btnPevID;
        int btnNextID;

        switch (mPage) {
            case 0: // Fragment user
                view = inflater.inflate(R.layout.fragment_user, container, false);
                listviewID = R.id.list_user;
                btnPevID = R.id.btn_user_prev;
                btnNextID = R.id.btn_user_next;

                // Render pagination buttons
                renderBtns(view, btnPevID, btnNextID, userJSON);

                // Render each row
                renderRows(view, listviewID, userJSON, "user");
                break;
            case 1: // Fragment page
                view = inflater.inflate(R.layout.fragment_page, container, false);
                listviewID = R.id.list_page;
                btnPevID = R.id.btn_page_prev;
                btnNextID = R.id.btn_page_next;

                // Render pagination buttons
                renderBtns(view, btnPevID, btnNextID, pageJSON);

                renderRows(view, listviewID, pageJSON, "page");
                break;
            case 2: // Fragment event
                view = inflater.inflate(R.layout.fragment_event, container, false);
                listviewID = R.id.list_event;
                btnPevID = R.id.btn_event_prev;
                btnNextID = R.id.btn_event_next;

                // Render pagination buttons
                renderBtns(view, btnPevID, btnNextID, eventJSON);

                // Render each row
                renderRows(view, listviewID, eventJSON,"event");
                break;
            case 3: // Fragment place
                view = inflater.inflate(R.layout.fragment_place, container, false);
                listviewID = R.id.list_place;
                btnPevID = R.id.btn_place_prev;
                btnNextID = R.id.btn_place_next;

                // Render pagination buttons
                renderBtns(view, btnPevID, btnNextID, placeJSON);

                // Render each row
                renderRows(view, listviewID, placeJSON, "place");
                break;
            case 4: // Fragment group
                view = inflater.inflate(R.layout.fragment_group, container, false);
                listviewID = R.id.list_group;
                btnPevID = R.id.btn_group_prev;
                btnNextID = R.id.btn_group_next;

                // Render pagination buttons
                renderBtns(view, btnPevID, btnNextID, groupJSON);

                // Render each row
                renderRows(view, listviewID, groupJSON, "group");
                break;
            default:
                view = null;
                break;
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        listViewAdapter.notifyDataSetChanged();
    }

    // Parse JSON object
    private void parseJSON() {
        try {
            userJSON = resultJSONObj.getJSONObject("user");
            pageJSON = resultJSONObj.getJSONObject("page");
            eventJSON = resultJSONObj.getJSONObject("event");
            placeJSON = resultJSONObj.getJSONObject("place");
            groupJSON = resultJSONObj.getJSONObject("group");
        }
        catch (JSONException e ) {
            e.printStackTrace();
        }
    }

    // Render each row of ListView
    private void renderRows(View view, int listviewID, JSONObject jsonObj, String type) {
        ListView list;

        // Clear previous result
        itemName.clear();
        profileURL.clear();
        detailID.clear();

        if(jsonObj != null && jsonObj.length() != 0) {
            try {
                // Get data JSONArray
                JSONArray dataJSONArr = null;
                if(!jsonObj.isNull("data")) {
                    dataJSONArr = jsonObj.getJSONArray("data");
                }
                if(dataJSONArr != null && dataJSONArr.length() != 0){
                    for(int i = 0; i < dataJSONArr.length(); i++) {
                        String dataName = dataJSONArr.getJSONObject(i).getString("name");
                        String dataProfile = dataJSONArr.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");
                        String dataID = dataJSONArr.getJSONObject(i).getString("id");
                        itemName.add(dataName);
                        profileURL.add(dataProfile);
                        detailID.add(dataID);
                    }
//                    Log.i("itemName", itemName.toString());
//                    Log.i("profileURL", profileURL.toString());
//                    Log.i("detailID", detailID.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Pg Fragment JSON Object", "JSON Object is null!!!!");
        }

        listViewAdapter = new ResultListAdapter(getActivity(), itemName, profileURL, detailID, type);
        list = (ListView) view.findViewById(listviewID);
        list.setAdapter(listViewAdapter);
    }

    // Refresh each row of ListView
    private void refreshRows(JSONObject jsonObj) {

        // Clear previous result
        itemName.clear();
        profileURL.clear();
        detailID.clear();

        if(jsonObj != null && jsonObj.length() != 0) {
            try {
                // Get data JSONArray
                JSONArray dataJSONArr = null;
                if(!jsonObj.isNull("data")) {
                    dataJSONArr = jsonObj.getJSONArray("data");
                }
                if(dataJSONArr != null && dataJSONArr.length() != 0){
                    for(int i = 0; i < dataJSONArr.length(); i++) {
                        String dataName = dataJSONArr.getJSONObject(i).getString("name");
                        String dataProfile = dataJSONArr.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");
                        String dataID = dataJSONArr.getJSONObject(i).getString("id");
                        itemName.add(dataName);
                        profileURL.add(dataProfile);
                        detailID.add(dataID);
                    }
//                    Log.i("itemName", itemName.toString());
//                    Log.i("profileURL", profileURL.toString());
//                    Log.i("detailID", detailID.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("Pg Fragment JSON Object", "JSON Object is null!!!!");
        }

        listViewAdapter.notifyDataSetChanged();
    }

    // Render pagination buttons
    private void renderBtns( View view, int btnPrevID, int btnNextID, JSONObject jsonObj) {
        Button btnPrev = (Button) view.findViewById(btnPrevID);
        Button btnNext = (Button) view.findViewById(btnNextID);

        final int btnPrevIDFinal = btnPrevID;
        final int btnNextIdFinal = btnNextID;
        final String nextURL;
        final String prevURL;
        final View viewFinal = view;

        if(jsonObj != null && jsonObj.length() != 0) {
            try {
                JSONObject pagingJSON = null;
                if(!jsonObj.isNull("paging")) {
                    pagingJSON = jsonObj.getJSONObject("paging");
                }
                if(pagingJSON != null && pagingJSON.length() != 0) {


                    if(pagingJSON.isNull("previous")) {
                        btnPrev.setEnabled(false);
                    } else {
                        prevURL  = pagingJSON.getString("previous");
                        btnPrev.setEnabled(true);

                        // Add listener onto the prev button
                        btnPrev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject prevPageJSON;
                                prevPageJSON = JSONRequestTask.getJSONObj(prevURL);

                                // Reset buttons
                                renderBtns(viewFinal, btnPrevIDFinal, btnNextIdFinal, prevPageJSON);

                                // Refresh list view
                                refreshRows(prevPageJSON);
                            }
                        });
                    }

                    if(pagingJSON.isNull("next")) {
                        btnNext.setEnabled(false);
                    } else {
                        nextURL = pagingJSON.getString("next");
                        btnNext.setEnabled(true);

                        // Add listener onto the next button
                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                JSONObject nextPageJSON;
                                nextPageJSON = JSONRequestTask.getJSONObj(nextURL);

                                // Reset buttons
                                renderBtns(viewFinal, btnPrevIDFinal, btnNextIdFinal, nextPageJSON);

                                // Refresh list view
                                refreshRows(nextPageJSON);
                            }
                        });
                    }
                } else {
                    btnPrev.setEnabled(false);
                    btnNext.setEnabled(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
