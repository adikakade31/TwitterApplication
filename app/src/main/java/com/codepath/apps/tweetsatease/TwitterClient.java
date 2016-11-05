package com.codepath.apps.tweetsatease;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.tweetsatease.api_helpers.HomeTimelineFetcher;
import com.codepath.apps.tweetsatease.api_helpers.MentionsTimelineFetcher;
import com.codepath.apps.tweetsatease.api_helpers.UserInfoFetcher;
import com.codepath.apps.tweetsatease.api_helpers.UserTimelineFetcher;
import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "BEEtO51Gw8KravG90cRfcIOpq";       // Change this
	public static final String REST_CONSUMER_SECRET = "fUs9N9YeQnYVa4tTml7H3mlbhf5C2yW8PEYtpT2tsFYkalBJof"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://cptweetsatease"; // Change this (here and in manifest)
    public static final String GET_USER_INFO = "account/verify_credentials.json";
	public static final String UPDATE_STATUS = "statuses/update.json";
    public static final String ADD_FAVORITE = "favorites/create.json";
    public static final String DESTROY_FAVORITE = "favorites/destroy.json";


	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeline(AsyncHttpResponseHandler handler) {
        HomeTimelineFetcher fetcher = HomeTimelineFetcher.getInstance();
        getClient().get(getApiUrl(fetcher.getAPIURL()), fetcher.getRequestParams(), handler);
        Log.d("DEBUG", "FetcherPARAMS = "+fetcher.getRequestParams());
    }

    public void getMentionsTimeline(AsyncHttpResponseHandler handler) {
        MentionsTimelineFetcher fetcher = MentionsTimelineFetcher.getInstance();
        getClient().get(getApiUrl(fetcher.getAPIURL()), fetcher.getRequestParams(), handler);
        Log.d("DEBUG", "FetcherPARAMS = "+fetcher.getRequestParams());
    }

    public void getUserTimeline(String screenName,AsyncHttpResponseHandler handler) {
        UserTimelineFetcher fetcher = UserTimelineFetcher.getInstance();
        UserTimelineFetcher.screenName = screenName;
        getClient().get(getApiUrl(fetcher.getAPIURL()), fetcher.getRequestParams(), handler);
        Log.d("DEBUG", "FetcherPARAMS = "+fetcher.getRequestParams());
    }

    public void getUserInfo( AsyncHttpResponseHandler handler) {
        UserInfoFetcher fetcher = UserInfoFetcher.getInstance();
        getClient().get(getApiUrl(fetcher.getAPIURL()), fetcher.getRequestParams(), handler);
        Log.d("DEBUG", "FetcherPARAMS = "+fetcher.getRequestParams());
    }

    public void getCurrentUser(AsyncHttpResponseHandler handler) {
        getClient().get(getApiUrl(GET_USER_INFO), null, handler);
    }

	public void composeTweet(String tweet, String replyStatusID, AsyncHttpResponseHandler handler) {
		String api_url = getApiUrl(UPDATE_STATUS);
		RequestParams params = new RequestParams();
		params.put("status", tweet);
        if (replyStatusID != null)
            params.put("in_reply_to_status_id", replyStatusID);
		getClient().post(api_url, params, handler);
	}

    public void reTweet(String id, AsyncHttpResponseHandler handler) {
        String api_url = getApiUrl(String.format("statuses/retweet/%s.json", id));
        RequestParams params = new RequestParams();
        getClient().post(api_url, params, handler);
    }

    public void favoriteTweet(String id, boolean liked, AsyncHttpResponseHandler handler) {
        String api_url;
        if (liked)
            api_url = getApiUrl(DESTROY_FAVORITE);
        else
            api_url = getApiUrl(ADD_FAVORITE);

        RequestParams params = new RequestParams();
        params.put("id", id);
        getClient().post(api_url, params, handler);
    }
}