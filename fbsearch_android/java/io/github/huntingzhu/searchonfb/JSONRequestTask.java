package io.github.huntingzhu.searchonfb;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by hongzhaozhu on 4/24/17.
 * This class is used to request JSON file;
 */

public class JSONRequestTask extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... urls) {

        String stringUrl = urls[0];
        String result;
        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);

            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);

            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();
        }
        catch(IOException e){
            e.printStackTrace();
            result = "Failed";
        }

        return result;
    }

    // Send request to get JSON object
    public static JSONObject getJSONObj(String url) {

        JSONRequestTask jsonRequest = new JSONRequestTask();

        String resultJSON = null;
        try {
            resultJSON = jsonRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("JSON Request URL", url);
        Log.i("Result JSON String", resultJSON);

        JSONObject resultJSONObj = null;
        try {
            resultJSONObj = new JSONObject(resultJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultJSONObj;
    }

    // Send request to get JSON String
    public static String getJSONStr(String url) {

        JSONRequestTask jsonRequest = new JSONRequestTask();

        String resultJSON = null;
        try {
            resultJSON = jsonRequest.execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("JSON Request URL", url);
        Log.i("Result JSON String", resultJSON);

        return resultJSON;
    }

}
