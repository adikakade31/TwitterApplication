package com.codepath.apps.tweetsatease.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterApplication;
import com.codepath.apps.tweetsatease.TwitterClient;
import com.codepath.apps.tweetsatease.adapters.TweetsArrayAdapter;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TableTweetOperations;
import com.codepath.apps.tweetsatease.listeners.EndlessRecyclerViewScrollListener;
import com.codepath.apps.tweetsatease.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aditikakadebansal on 11/2/16.
 */
 public class TweetsListFragment extends Fragment {

    private ArrayList<Tweet> mTweets;
    private TweetsArrayAdapter mTweetsArrayAdapter;
    private RecyclerView mRvTweets;
    private EndlessRecyclerViewScrollListener scrollListener;
    private LinearLayoutManager mLinearLayoutManager;
    private TwitterClient mClient;
    public String mScreenName;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        setUpData(view);
        setUpListeners();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void loadNextDataFromApi(View view) {
        if(this instanceof HomeTimelineFragment) {
            if (Helper.isInternetConnected(getActivity().getApplicationContext())) {
                mTweetsArrayAdapter.addMoreToTimeline(view.findViewById(android.R.id.content), mClient);
            } else {
                mTweetsArrayAdapter.clear();
                mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
                Helper.showErrorSnackBar(getActivity().findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
            }
        }else if(this instanceof MentionsTimelineFragment) {
            if (Helper.isInternetConnected(getActivity().getApplicationContext())) {
                mTweetsArrayAdapter.addMoreToMentionsTimeline(view.findViewById(android.R.id.content), mClient);
            } else {
                mTweetsArrayAdapter.clear();
                mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
                Helper.showErrorSnackBar(getActivity().findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
            }
        }else if(this instanceof UserTimelineFragment) {
            if (Helper.isInternetConnected(getActivity().getApplicationContext())) {
                mTweetsArrayAdapter.addMoreToUserTimeline(mScreenName, view.findViewById(android.R.id.content), mClient);
            } else {
                mTweetsArrayAdapter.clear();
                mTweetsArrayAdapter.addAll(TableTweetOperations.getAllTweets());
                Helper.showErrorSnackBar(getActivity().findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
            }
        }

    }

    public void setUpData(View view) {
        mClient = TwitterApplication.getRestClient();

        mTweets = new ArrayList<>();
        mTweetsArrayAdapter = new TweetsArrayAdapter(getActivity(), mClient, mTweets);
        mRvTweets = (RecyclerView) view.findViewById(R.id.rvTweets);
        mRvTweets.setAdapter(mTweetsArrayAdapter);

        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRvTweets.setLayoutManager(mLinearLayoutManager);

        Helper.setCurrentUser(TwitterApplication.getRestClient());
    }

    public void setUpListeners() {
        scrollListener = new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(view);
            }
        };

        mRvTweets.addOnScrollListener(scrollListener);
    }

    public void tweetSent(Tweet tweet) {

        mTweetsArrayAdapter.insert(tweet, 0);
        mTweetsArrayAdapter.notifyDataSetChanged();
    }

    public void reTweet(Tweet tweet, Tweet newTweet) {
        int position = mTweetsArrayAdapter.getPosition(tweet);

        tweet.setRetweetCount(newTweet.getRetweetCount());
        tweet.setRetweeted(newTweet.isRetweeted());

        mTweetsArrayAdapter.remove(position);
        mTweetsArrayAdapter.insert(tweet, position);
        mTweetsArrayAdapter.notifyDataSetChanged();
    }

    public void favorite(final Tweet tweet, Tweet newTweet) {
        int position = mTweetsArrayAdapter.getPosition(tweet);

        mTweetsArrayAdapter.remove(position);
        mTweetsArrayAdapter.insert(newTweet, position);
        mTweetsArrayAdapter.notifyDataSetChanged();
    }

    public void clear() {
        mTweetsArrayAdapter.clear();
    }

    public void addAll(List<Tweet> tweets) {
        mTweetsArrayAdapter.addAll(tweets);
    }

    public void fetchTimeline(View view) {
        mTweetsArrayAdapter.fetchTimeline(view, mClient);
    }
    public void fetchMentionsTimeline(View view) {
        mTweetsArrayAdapter.fetchMentionsTimeline(view, mClient);
    }
    public void fetchUserTimeline(View view) {
        mTweetsArrayAdapter.fetchUserTimeline(mScreenName, view, mClient);
    }

    public void setOnScrollListener() {
        final FloatingActionButton mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab_compose);

        mRvTweets.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && mFab.isShown()) {
                    mFab.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mFab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
}

