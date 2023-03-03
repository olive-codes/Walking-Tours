package com.oliviabecht.obechtwalkingtours;

import android.location.Location;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class fenceDownloader {

    private static final String Url = "http://www.christopherhield.com/data/WalkingTourContent.json";
    private static final ArrayList<FenceData> fenceList = new ArrayList<>();
    private static final String TAG = "fenceDownloader";


    public static void downloadRoutes(MapsActivity mapsActivityIn) {

        RequestQueue queue = Volley.newRequestQueue(mapsActivityIn);

        Uri.Builder buildURL = Uri.parse(Url).buildUpon();
        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener = response -> {
            try {
                handleSuccess(response.toString(), mapsActivityIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        Response.ErrorListener error = fenceDownloader::handleFail;

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


    }


    private static void handleSuccess(String responseText, MapsActivity mapsActivityIn) throws JSONException {
        JSONObject response = new JSONObject(responseText);


        JSONArray JFence = response.getJSONArray("fences");
        fenceList.clear();
        for (int i = 0; i < JFence.length(); i++) {
            JSONObject route = JFence.getJSONObject(i);
            String buildingID = route.getString("id");
            String buildingAddress = route.getString("address");
            String buildingLat = route.getString("latitude");
            String buildingLon = route.getString("longitude");
            LatLng ll = new LatLng(Double.parseDouble(buildingLat), Double.parseDouble(buildingLon));
            String buildingRadius = route.getString("radius");
            String buildingDescription = route.getString("description");
            String buildingFenceColor = route.getString("fenceColor");
            String buildingImage = route.getString("image");

            FenceData fenceObj = new FenceData(
                        buildingID,
                        ll,
                        Double.parseDouble(buildingLat),
                        Double.parseDouble(buildingLon),
                        Double.parseDouble(buildingRadius),
                        buildingAddress,
                        buildingDescription,
                        buildingFenceColor,
                        buildingImage);

                fenceList.add(fenceObj);

        }
        //int size = fenceList.size();
        mapsActivityIn.runOnUiThread(() -> MapsActivity.acceptStops(fenceList, mapsActivityIn));

    }


    private static void handleFail(VolleyError ve) {
        Log.d(TAG, "handleFail: " + ve.getMessage());
    }


}
