package com.masich.yatc.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.masich.yatc.OAuthConnectionData;
import com.masich.yatc.R;
import com.masich.yatc.TwitterConnection;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Activity for login in twitter using OAuth.
 *
 * @author Masich Ivan <john@masich.com>
 */
public class LoginActivity extends BaseActivity {
    private static final String LOG_TAG = "LoginActivity";

    private final String CALLBACK_SCHEME = "yatcoauthtwitter";
    private final String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

    private RequestToken oAuthRequestToken;
    private Twitter twitter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        
        final LoginActivity activity = this;

        Button tweet = (Button)findViewById(R.id.buttonLogIn);

        tweet.setOnClickListener(
            new View.OnClickListener() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void onClick(View v) {
                    activity.sendRequest();
                }
            }
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.login, menu);

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri uri = intent.getData();
        if (uri != null && uri.getScheme().equals(CALLBACK_SCHEME)) {
            if (saveOAuthData(intent.getData())) {
                startActivity(new Intent(this, YATCActivity.class));
                finish();
            } else {
                showError(getString(R.string.loginErrorMessage));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        hideErrorMessage();
    }

    /**
     * Get OAuth request token, and then redirect to browser.
     */
    private void sendRequest() {
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(OAuthConnectionData.CONSUMER_KEY, OAuthConnectionData.CONSUMER_SECRET);

        try {
            oAuthRequestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(oAuthRequestToken.getAuthenticationURL())));
        } catch (TwitterException e) {
            showError(getString(R.string.loginErrorMessage));

            Log.e(LOG_TAG, "Error when get OAuth request token.", e);
        }
    }

    /**
     * Save token and token secret to properties storage.
     *
     * @param uri {@link Uri} from passed {@link Intent} object
     * @return If access token in retried from server then return true, otherwise false.
     */
    private boolean saveOAuthData(Uri uri) {
        AccessToken accessToken;
        try {
            accessToken = twitter.getOAuthAccessToken(oAuthRequestToken, uri.getQueryParameter("oauth_verifier"));

            TwitterConnection storage = new TwitterConnection(this);
            storage.setToken(accessToken.getToken());
            storage.setTokenSecret(accessToken.getTokenSecret());

            return true;
        } catch (TwitterException e) {
            Log.e(LOG_TAG, "Error when get OAuth access token.", e);

            return false;
        }
    }

    /**
     * Show error message.
     *
     * @param message Message to set in error box.
     */
    private void showError(String message) {
        TextView error = (TextView) findViewById(R.id.loginError);

        error.setText(message);
        error.setVisibility(TextView.VISIBLE);
    }

    /**
     * Hide error message.
     */
    private void hideErrorMessage() {
        TextView error = (TextView) findViewById(R.id.loginError);

        error.setVisibility(TextView.GONE);
    }
}
