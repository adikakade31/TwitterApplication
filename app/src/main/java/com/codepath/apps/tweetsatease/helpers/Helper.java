package com.codepath.apps.tweetsatease.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by aditikakadebansal on 10/26/16.
 */

public class Helper  {

    public static void setCurrentUser(TwitterClient client){

        client.getCurrentUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] header, JSONObject jsonObject) {
                User.setloggedInUser(new User(jsonObject));
            }

            @Override
            public void onFailure(int statusCode, Header[] header, Throwable throwable, JSONObject errorResponse) {
                User.setloggedInUser(null);
            }
        });
    }


    public static Boolean isInternetConnected(Context context) {
        return InternetConnectivity.isInternetConnected(
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)
        );
    }

    public static String getErrorString(ResponseStatus responseStatusCode) {
        switch (responseStatusCode) {
            case NO_INTERNET_CONNECTION:
                return "Internet connection lost, offline reading available";
            case NO_RESULTS:
                return "Could not find any results, retry with a different query";
            case REQUEST_FAILURE:
                return "Request failed, retry";
            case RATE_LIMIT_EXCEEDED:
                return "Rate limit exceeded, retry after some time";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void showErrorSnackBar(View view, ResponseStatus responseStatus) {
        Snackbar.make(view, getErrorString(responseStatus), Snackbar.LENGTH_LONG).show();
    }



}
