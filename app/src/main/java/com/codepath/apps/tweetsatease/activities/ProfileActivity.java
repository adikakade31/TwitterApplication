package com.codepath.apps.tweetsatease.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.tweetsatease.R;
import com.codepath.apps.tweetsatease.enums.ResponseStatus;
import com.codepath.apps.tweetsatease.fragments.ComposeTweetFragment;
import com.codepath.apps.tweetsatease.fragments.UserTimelineFragment;
import com.codepath.apps.tweetsatease.helpers.Helper;
import com.codepath.apps.tweetsatease.helpers.TwitterActions;
import com.codepath.apps.tweetsatease.models.Tweet;
import com.codepath.apps.tweetsatease.models.User;

/**
 * Created by aditikakadebansal on 11/3/16.
 */

public class ProfileActivity extends AppCompatActivity implements  TwitterActions.OnTweetListener
        ,TwitterActions.OnReTweetListener
         ,TwitterActions.OnFavoriteListener,
        TwitterActions.OnReplyListener, TwitterActions.OnUserProfileView
{
    private User mUser;
    private UserTimelineFragment mFragmentUserTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mUser = getIntent().getParcelableExtra("user");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mUser.getScreenName());
        if (mUser != null) {
            populateUserProfileHeader();
        }
        if(savedInstanceState == null) {
            mFragmentUserTimeline = UserTimelineFragment.newInstance(mUser.getScreenName());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.flContainer, mFragmentUserTimeline);
            fragmentTransaction.commit();
        }

        if (mUser == null && !Helper.isInternetConnected(this)) {
            Toast.makeText(this, "Profile unavailable right now, please try later!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void populateUserProfileHeader() {
        TextView tvName = (TextView) findViewById(R.id.tvUsername);
        tvName.setText(mUser.getName());
        TextView tvTagLine = (TextView) findViewById(R.id.tvTagLine);
        tvTagLine.setText(mUser.getTagLine());
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowers.setText(mUser.getFollowersCount() + " Followers");
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        tvFollowing.setText(mUser.getFollowingCount() + " Following");
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfilePic);
        Glide.with(getApplicationContext()).load(mUser.getProfileImageUrl()).into(ivProfileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTweetSent(Tweet tweet) {
        mFragmentUserTimeline.tweetSent(tweet);
    }

    @Override
    public void onReTweet(Tweet tweet, Tweet newTweet) {
        mFragmentUserTimeline.reTweet(tweet, newTweet);
    }

    @Override
    public void onFavorite(final Tweet tweet, Tweet newTweet) {
        mFragmentUserTimeline.favorite(tweet, newTweet);
    }

    @Override
    public void onUserProfile(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
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

}
