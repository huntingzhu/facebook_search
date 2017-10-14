package io.github.huntingzhu.searchonfb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FavorListAdapter extends ArrayAdapter {
    public static final String EXTRA_DETAILID = "io.github.huntingzhu.searchonfb.DETAILID";
    public static final String EXTRA_TYPE = "io.github.huntingzhu.searchonfb.TYPE";

    private Activity context;
    private ArrayList<String> itemName;
    private ArrayList<String> imgURL;
    private ArrayList<String> detailID;
    private String type;

    public FavorListAdapter(Activity context, ArrayList<String> itemName, ArrayList<String> imgURL, ArrayList<String> detailID, String type) {
        super(context, R.layout.result_list, itemName);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemName=itemName;
        this.imgURL=imgURL;
        this.detailID = detailID;
        this.type = type;

//        Log.i(type, LocalStorage.getAll(context,type).toString());
    }

    public View getView(int position, View view, ViewGroup parent) {
        final int positionFinal = position;
        LayoutInflater inflater=context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.result_list, null,true);

        ImageView profileView = (ImageView) rowView.findViewById(R.id.item_profile);
        TextView nameView = (TextView) rowView.findViewById(R.id.item_name);
        ImageView favoriteView = (ImageView) rowView.findViewById(R.id.item_favorite);
        ImageView detailView = (ImageView) rowView.findViewById(R.id.item_detail);

        // Set the content for each element in the row
        String imageUri = imgURL.get(position);  // Use Picasso to load image
        Picasso.with(context).load(imageUri).into(profileView);

        nameView.setText(itemName.get(position));
        detailView.setImageResource(R.drawable.details);

        // Set favorite icon
        if(LocalStorage.contains(context, detailID.get(position), type)) {
            favoriteView.setImageResource(R.drawable.favorites_on);
        } else {
            favoriteView.setImageResource(R.drawable.favorites_off);
        }


        // Set onClick event of detail button;
        detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detailIDStr = detailID.get(positionFinal);
//                Toast.makeText(context, detailIDStr, Toast.LENGTH_SHORT).show();

                // Start Detail Activity
                Intent intentDetail = new Intent(getContext(), DetailActivity.class);

                // Transmit info to DetailActivity
                intentDetail.putExtra(EXTRA_DETAILID, detailIDStr);
                intentDetail.putExtra(EXTRA_TYPE, type);

                getContext().startActivity(intentDetail);

            }
        });

        favoriteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String currentDetailID = detailID.get(positionFinal);
                String currentType = type;
                Context currentContext = context;

                // Remove item;
                LocalStorage.remove(currentContext,currentDetailID,currentType);

                imgURL.remove(positionFinal);
                itemName.remove(positionFinal);
                detailID.remove(positionFinal);

                notifyDataSetChanged();
            }
        });

        return rowView;

    };
}
