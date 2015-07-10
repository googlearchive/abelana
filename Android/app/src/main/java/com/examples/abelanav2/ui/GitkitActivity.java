/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.examples.abelanav2.AndroidConstants;
import com.examples.abelanav2.R;
import com.examples.abelanav2.grpcclient.AbelanaClient;
import com.examples.abelanav2.grpcclient.AbelanaClientException;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitUser;
import com.google.identitytoolkit.IdToken;

/**
 * Activity used to sign the user in.
 */
public class GitkitActivity extends Activity implements OnClickListener {

    /**
     * Gitkit gitkitClient.
     */
    private GitkitClient mGitkitClient;

    /**
     * Abelana GRPC gitkitClient.
     */
    private AbelanaClient mAbelanaClient;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The gRPC client
        mAbelanaClient = new AbelanaClient(getApplicationContext());

        // Is the user connected? If yes, redirect him to the MainActivity
        if (mAbelanaClient.isSignedIn()) {
            startMainActivity();
        }

        // Step 1: Create a GitkitClient.
        mGitkitClient = GitkitClient.newBuilder(this,
                new GitkitClient.SignInCallbacks() {
                    // This method is called when the sign-in process succeeds.
                    @Override
                    public void onSignIn(final IdToken idToken,
                                         final GitkitUser user) {
                        // Get a session token from the server
                        new SignInTask().execute(idToken.getTokenString());
                    }

                    // This method is called when the sign-in process fails.
                    @Override
                    public void onSignInFailed() {
                        Toast.makeText(GitkitActivity.this, "Sign in failed",
                                Toast.LENGTH_LONG).show();
                        showSignInPage();
                    }
                })
                .showProviders(AndroidConstants.PROVIDERS)
                .useGooglePlus(AndroidConstants.USE_GOOGLE_PLUS)
                .setApiKey(AndroidConstants.API_KEY)
                .setServerClientId(AndroidConstants.SERVER_CLIENT_ID)
                .setServerWidgetUrl(AndroidConstants.SERVER_WIDGET_URL)
                .build();
        showSignInPage();
    }


    // Step 3: Override the onActivityResult method.
    // When a result is returned to this activity, it is maybe intended for
    // GitkitClient. Call  GitkitClient.handleActivityResult to check the
    // result. If the result is for GitkitClient, the method returns true to
    // indicate the result has been consumed.
    @Override
    protected final void onActivityResult(final int requestCode, final int
            resultCode, final Intent intent) {
        if (!mGitkitClient.handleActivityResult(requestCode, resultCode,
                intent)) {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    // Step 4: Override the onNewIntent method.
    // When the app is invoked with an intent, it is possible that the intent is
    // for GitkitClient. Call GitkitClient.handleIntent to check it. If the
    // intent is for GitkitClient, the method returns true to indicate the
    // intent has been consumed.
    @Override
    protected final void onNewIntent(final Intent intent) {
        if (!mGitkitClient.handleIntent(intent)) {
            super.onNewIntent(intent);
        }
    }

    /**
     * Shows the sign in page.
     */
    private void showSignInPage() {
        setContentView(R.layout.signin_welcome);
        Button button = (Button) findViewById(R.id.sign_in);
        button.setOnClickListener(this);
    }

    // Step 5: Respond to user actions.
    // If the user clicks sign in, call GitkitClient.startSignIn() to trigger
    // the sign in flow.
    @Override
    public final void onClick(final View v) {

        if (v.getId() == R.id.sign_in) {
            mGitkitClient.startSignIn();
        }
    }


    /**
     * AsyncTask used to sign in the user.
     */
    private class SignInTask extends AsyncTask<String, Void, String> {

        /**
         * A progress dialog shown while trying to sign the user in.
         */

        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            mProgress = ProgressDialog.show(GitkitActivity.this,
                    getString(R.string.sign_in_progress_title),
                    getString(R.string.sign_in_progress_message), true);
        }

        @Override
        protected String doInBackground(final String... params) {
            try {
                boolean res = mAbelanaClient.signIn(params[0]);
                return (res) ? null : getString(R.string.server_error);
            } catch (AbelanaClientException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mProgress.dismiss();
            if (result == null) {
                startMainActivity();
                findViewById(R.id.sign_in).setActivated(false);
            } else {
                showErrorMessage(result);
                showSignInPage();

            }
        }
    }

    /**
     * Navigates to the MainActivity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Displays a Toast with an error message.
     * @param message the message to display.
     */
    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
