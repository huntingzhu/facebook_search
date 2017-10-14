package io.github.huntingzhu.searchonfb;

import android.app.Activity;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostListAdapter extends ArrayAdapter {

    private final Activity context;
    private final String postName;
    private final String postProfile;
    private final ArrayList<String> postTime;
    private final ArrayList<String> postContent;

    public PostListAdapter(Activity context, String postName, String postProfile, ArrayList<String> postTime, ArrayList<String> postContent) {
        super(context, R.layout.post_list, postTime);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.postName = postName;
        this.postProfile = postProfile;
        this.postTime = postTime;
        this.postContent = postContent;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final int positionFinal = position;
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.post_list, null,true);

        ImageView profileView = (ImageView) rowView.findViewById(R.id.post_profile);
        TextView nameView = (TextView) rowView.findViewById(R.id.post_name);
        TextView timeView = (TextView) rowView.findViewById(R.id.post_time);
        TextView contentView = (TextView) rowView.findViewById(R.id.post_content);

        // Set the content for each element in the row
        String imageUri = postProfile;  // Use Picasso to load image
        Picasso.with(context).load(imageUri).into(profileView);

        nameView.setText(postName);
        contentView.setText(postContent.get(positionFinal));

        // Convert Date format
        String oldTimeStr = postTime.get(positionFinal);

        oldTimeStr = oldTimeStr.substring(0, 18);

        SimpleDateFormat oldDf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");
        SimpleDateFormat newDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = oldDf.parse(oldTimeStr);
            String newTimeStr = newDf.format(date);
            timeView.setText(newTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i("Get View Post-time", oldTimeStr);


        return rowView;

    };
}