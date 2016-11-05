package com.codepath.apps.tweetsatease.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.api_helpers.MentionsTimelineFetcher;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TableTweetOperations;

/**
 * Created by aditikakadebansal on 11/3/16.
 */

public class MentionsTimelineFragment
        extends TweetsListFragment
{
    private SwipeRefreshLayout mSwipeContainer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Helper.isInternetConnected(getActivity().getApplicationContext())) {
            fetchMentionsTimeline(getView().findViewById(android.R.id.content));

        } else {
            clear();
            addAll(TableTweetOperations.getAllTweets());
            Helper.showErrorSnackBar(getView().findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
        }

        setOnScrollListener();
        mSwipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        setUpMentionsTimelineListeners(mSwipeContainer);
    }



    public void setUpMentionsTimelineListeners(final SwipeRefreshLayout mSwipeContainer) {
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                MentionsTimelineFetcher.setMinIdAvailable();
                if (Helper.isInternetConnected(getContext())) {
                    fetchMentionsTimeline(getActivity().findViewById(android.R.id.content));
                } else {
                    clear();
                    addAll(TableTweetOperations.getAllTweets());
                    Helper.showErrorSnackBar(getActivity().findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
                }
                mSwipeContainer.setRefreshing(false);
            }
        });


    }

}
