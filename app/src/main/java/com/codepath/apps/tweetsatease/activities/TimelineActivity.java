package com.codepath.apps.tweetsatease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.TwitterApplication;
import com.codepath.apps.tweetsatease.api_helpers.UserTimelineFetcher;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.fragments.ComposeTweetFragment;
import com.codepath.apps.tweetsatease.fragments.HomeTimelineFragment;
import com.codepath.apps.tweetsatease.fragments.MentionsTimelineFragment;
import com.codepath.apps.tweetsatease.fragments.TweetsListFragment;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;


public class TimelineActivity
        extends AppCompatActivity
        implements TwitterActions.OnTweetListener,
        TwitterActions.OnReTweetListener, TwitterActions.OnFavoriteListener,
        TwitterActions.OnReplyListener, TwitterActions.OnUserProfileView{

    private FloatingActionButton mFab;
    private TweetsPagerAdapter mTweetsPagerAdapter;
    private ViewPager mViewPager;
    private PagerSlidingTabStrip mTabStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        setUpData();
        setUpListeners();
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTweetsPagerAdapter);
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mTabStrip.setViewPager(mViewPager);
    }

    public void setUpData(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_launcher));

        Helper.setCurrentUser(TwitterApplication.getRestClient());

        mFab = (FloatingActionButton) findViewById(R.id.fab_compose);
    }

    public void setUpListeners(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment tweetDialog = ComposeTweetFragment.newInstance(User.getloggedInUser(), null);
                if(Helper.isInternetConnected(getApplicationContext())) {
                    tweetDialog.show(fm, "Compose Tweet");
                }else {
                    Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTweetSent(Tweet tweet) {
        TweetsListFragment fragmentTweetsList = (TweetsListFragment) mTweetsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        fragmentTweetsList.tweetSent(tweet);

    }
    @Override
    public void onReTweet(Tweet tweet, Tweet newTweet) {
        TweetsListFragment fragmentTweetsList = (TweetsListFragment) mTweetsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        fragmentTweetsList.reTweet(tweet, newTweet);

    }

    @Override
    public void onFavorite(final Tweet tweet, Tweet newTweet) {
        TweetsListFragment fragmentTweetsList = (TweetsListFragment) mTweetsPagerAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
        fragmentTweetsList.favorite(tweet, newTweet);
    }

    @Override
    public void onReply(Tweet tweet) {
        FragmentManager fm = getSupportFragmentManager();
        ComposeTweetFragment composeTweetDialog = ComposeTweetFragment.newInstance(User.getloggedInUser(), tweet);
        if(Helper.isInternetConnected(getApplicationContext())) {
            composeTweetDialog.show(fm, "Compose Tweet");
        }else {
            Helper.showErrorSnackBar(findViewById(android.R.id.content), ResponseStatus.NO_INTERNET_CONNECTION);

        }
    }

    public void onProfileView(MenuItem mi) {
        UserTimelineFetcher.setMinIdAvailable();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", User.getloggedInUser());
        startActivity(intent);
    }

    @Override
    public void onUserProfile(User user) {
        Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }


    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                return new HomeTimelineFragment();
            }else if(position == 1) {
                return new MentionsTimelineFragment();
            }else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }


}
