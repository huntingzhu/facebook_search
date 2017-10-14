package io.github.huntingzhu.searchonfb;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumExpandableListData {
    private HashMap<String, List<String>> expandableListDetail;

    public AlbumExpandableListData(JSONObject albumJSON) {
        this.expandableListDetail  = new HashMap<String, List<String>>();

        if(albumJSON != null && !albumJSON.isNull("data")) {
            try {
                JSONArray albumArr = albumJSON.getJSONArray("data");
                for(int i = 0; i < albumArr.length(); i++) {
                    List<String> albumPhotos = new ArrayList<>();
                    String albumName = "";

                    // Album name
                    if(!albumArr.getJSONObject(i).isNull("name")) {
                        albumName = albumArr.getJSONObject(i).getString("name");
                    }

                    // Album photos
                    if(!albumArr.getJSONObject(i).isNull("photos")
                            && !albumArr.getJSONObject(i).getJSONObject("photos").isNull("data")) {
                        JSONArray photoArr = albumArr.getJSONObject(i).getJSONObject("photos").getJSONArray("data");
                        for(int j = 0; j < photoArr.length(); j++) {
                            if(!photoArr.getJSONObject(j).isNull("picture")) {
                                String photoURL = photoArr.getJSONObject(j).getString("picture");
                                albumPhotos.add(photoURL);
                            }
                        }
                    }

                    // Put items into hashMap
                    expandableListDetail.put(albumName,albumPhotos);

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }


    public HashMap<String, List<String>> getData() {
        return this.expandableListDetail;
    }
}