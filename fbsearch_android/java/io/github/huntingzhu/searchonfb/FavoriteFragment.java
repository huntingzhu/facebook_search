package io.github.huntingzhu.searchonfb;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class FavoriteFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private Map<String,?> userMap;
    private Map<String,?> pageMap;
    private Map<String,?> eventMap;
    private Map<String,?> placeMap;
    private Map<String,?> groupMap;

    private ArrayList<String> itemName = new ArrayList<>();
    private ArrayList<String> profileURL = new ArrayList<>();
    private ArrayList<String> detailID = new ArrayList<>();

    private FavorListAdapter listViewAdapter;


    public static FavoriteFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);

        FavoriteFragment fragment = new FavoriteFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        int listviewID = R.id.list_favorite;

        // Fetch all the data from shared preference
        fetchJSON();

        switch (mPage) {
            case 0: // Fragment user
                // Render each row
                renderRows(view, listviewID, userMap, "user");
                break;
            case 1: // Fragment page
                // Render each row
                renderRows(view, listviewID, pageMap, "page");
                break;
            case 2: // Fragment event
                // Render each row
                renderRows(view, listviewID, eventMap,"event");
                break;
            case 3: // Fragment place
                // Render each row
                renderRows(view, listviewID, placeMap, "place");
                break;
            case 4: // Fragment group
                // Render each row
                renderRows(view, listviewID, groupMap, "group");
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

        View view = getView();
        int listviewID = R.id.list_favorite;

        // Fetch all the data from shared preference
        fetchJSON();

        switch (mPage) {
            case 0: // Fragment user
                // Render each row
                renderRows(view, listviewID, userMap, "user");
                break;
            case 1: // Fragment page
                // Render each row
                renderRows(view, listviewID, pageMap, "page");
                break;
            case 2: // Fragment event
                // Render each row
                renderRows(view, listviewID, eventMap,"event");
                break;
            case 3: // Fragment place
                // Render each row
                renderRows(view, listviewID, placeMap, "place");
                break;
            case 4: // Fragment group
                // Render each row
                renderRows(view, listviewID, groupMap, "group");
                break;
            default:
                break;
        }

//        listViewAdapter.notifyDataSetChanged();
    }

    // Fetch JSON object from local storage
    private void fetchJSON() {
        userMap = LocalStorage.getAll(getContext(),"user");
        pageMap = LocalStorage.getAll(getContext(),"page");
        eventMap = LocalStorage.getAll(getContext(),"event");
        placeMap = LocalStorage.getAll(getContext(),"place");
        groupMap = LocalStorage.getAll(getContext(),"group");
    }

    // Render each row of ListView
    private void renderRows(View view, int listviewID, Map<String,?> mapData, String type) {
        ListView list;

        // Clear previous result
        itemName.clear();
        profileURL.clear();
        detailID.clear();

        if(mapData != null && !mapData.isEmpty()) {
           for(Map.Entry<String, ?> entry : mapData.entrySet()) {
               try {
                   JSONObject favorJSONObj = new JSONObject(entry.getValue().toString());
                   itemName.add(favorJSONObj.getString("name"));
                   profileURL.add(favorJSONObj.getString("profile"));
                   detailID.add(favorJSONObj.getString("id"));
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }
        } else {
            Log.e("Favor data loading", "Map Object is null!!!!");
        }

        listViewAdapter = new FavorListAdapter(getActivity(), itemName, profileURL, detailID, type);
        list = (ListView) view.findViewById(listviewID);
        list.setAdapter(listViewAdapter);
    }

}
