package com.masich.yatc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.masich.yatc.activity.LoginActivity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Twitter connection.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class TwitterConnection {
    public static final String OAUTH_TOKEN = "oauthToken";
    public static final String OAUTH_TOKEN_SECRET = "oauthTokenSecret";
    
    private SharedPreferences preferences;
    private Context context;

    public TwitterConnection(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    /**
     * Save OAuth token.
     *
     * @param token OAuth token
     */
    public void setToken(String token) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(OAUTH_TOKEN, token);
        editor.commit();
    }

    /**
     * Get OAuth token.
     *
     * @return OAuth token.
     */
    public String getToken() {
        return preferences.getString(OAUTH_TOKEN, null);
    }

    /**
     * Set OAuth token secret.
     *
     * @param tokenSecret OAuth token secret.
     */
    public void setTokenSecret(String tokenSecret) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(OAUTH_TOKEN_SECRET, tokenSecret);
        editor.commit();
    }

    /**
     * Get OAuth token secret.
     *
     * @return OAuth token secret.
     */
    public String getTokenSecret() {
        return preferences.getString(OAUTH_TOKEN_SECRET, null);
    }

    /**
     * Remove OAuth token and OAuth token secret from storage.
     */
    public void clean() {
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(OAUTH_TOKEN);
        editor.remove(OAUTH_TOKEN_SECRET);
        editor.commit();
    }

    /**
     * Check if OAuth token and OAuth token secret exist in storage.
     *
     * @return true it exist and false if no.
     */
    public boolean hasConnectionData()
    {
        return getToken() != null && getTokenSecret() != null;
    }

    /**
     * Get {@link Twitter} instance.
     *
     * @return {@link Twitter} instance.
     */
    public Twitter getTwitter() {
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(OAuthConnectionData.CONSUMER_KEY, OAuthConnectionData.CONSUMER_SECRET);

            AccessToken accessToken = new AccessToken(getToken(), getTokenSecret());
            twitter.setOAuthAccessToken(accessToken);

            return twitter;
        } catch (Exception e) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }

        return null;
    }
}
