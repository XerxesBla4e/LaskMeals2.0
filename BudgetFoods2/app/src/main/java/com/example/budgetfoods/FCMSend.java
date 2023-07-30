package com.example.budgetfoods;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FCMSend {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void pushNotification(Context context, String token, String title, String message) {
        executor.execute(() -> {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            RequestQueue queue = Volley.newRequestQueue(context);
            try {
                JSONObject json = new JSONObject();
                json.put("to", token);
                JSONObject notification = new JSONObject();
                notification.put("title", title);
                notification.put("body", message);
                json.put("notification", notification);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST, Constants.BASE_URL, json, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("FCM " + response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response here
                        Log.d("TAG",""+error.getMessage());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", Constants.SERVER_KEY);
                        return params;
                    }
                };
                queue.add(jsonObjectRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
