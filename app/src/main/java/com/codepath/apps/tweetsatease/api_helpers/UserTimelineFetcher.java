package com.codepath.apps.tweetsatease.api_helpers;

import android.util.Log;

import com.loopj.android.http.RequestParams;

/**
 * Created by aditikakadebansal on 11/03/16.
 */

public class UserTimelineFetcher implements IAPIFetcher {

    private static UserTimelineFetcher sInstance;

    public static final String SINCE_ID = "since_id";
    public static final String MAX_ID = "max_id";
    public static final String COUNT = "count";
    public static String screenName;
    public static final String SCREENNAME = "screen_name";

    private long minIDAvailable = 1;

    public static UserTimelineFetcher getInstance() {
        if (sInstance == null) {
            sInstance = new UserTimelineFetcher();
        }
        return sInstance;
    }

    public static void setMinIdAvailable() {
        getInstance().minIDAvailable = 1;
    }

    public static void observeTweetID(long id) {
        long minID = getInstance().minIDAvailable;
        if (minID == 1) {
            getInstance().minIDAvailable = id;
            return;
        }
        getInstance().minIDAvailable = Math.min(getInstance().minIDAvailable, id);
        Log.d("DEBUG", "minIDAvailable = "+getInstance().minIDAvailable);
    }

    public String getAPIURL() {
        return "statuses/user_timeline.json";
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(COUNT, 10);
        params.put(SINCE_ID, 1);
        params.put(SCREENNAME, screenName);
        if (minIDAvailable == 1)
            params.put(SINCE_ID, 1);
        else
            params.put(MAX_ID, minIDAvailable - 1);
        return params;
    }
}
