/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.examples.abelanav2.R;
import com.examples.abelanav2.grpcclient.AbelanaClient;
import com.examples.abelanav2.grpcclient.AbelanaClientException;

/**
 * The MainActivity class. It creates all the necessary fragments, and adds
 * the navigation Drawer to the UI.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link
     * #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Returns the Abelana client.
     * @return the Abelana client.
     */
    public AbelanaClient getAbelanaClient() {
        return mAbelanaClient;
    }

    /**
     * Abelana GRPC gitkitClient.
     */
    private AbelanaClient mAbelanaClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The gRPC client
        mAbelanaClient = new AbelanaClient(getApplicationContext());

        // Is the user connected? If no, redirect him to the GitkitActivity
        if (!mAbelanaClient.isSignedIn()) {
            startGitkitActivity();
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAbelanaClient.shutdown();
    }

    /**
     * Starts the GitkitActivity to sign the user in.
     */
    public void startGitkitActivity() {
        Intent intent = new Intent(this, GitkitActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container,
                                UploadFragment.newInstance())
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PicturesFragment.newInstance(
                                AbelanaClient.PhotoListType.PHOTO_LIST_MINE))
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PicturesFragment.newInstance(
                                AbelanaClient.PhotoListType.PHOTO_LIST_LIKES))
                        .commit();
                break;
            case 5:
                try {
                    mAbelanaClient.signOut();
                    startGitkitActivity();
                } catch (AbelanaClientException e) {
                    Toast.makeText(this, R.string.sign_out_failed,
                            Toast.LENGTH_LONG).show();
                }
                break;
            case 0:
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PicturesFragment.newInstance(
                                AbelanaClient.PhotoListType.PHOTO_LIST_STREAM))
                        .commit();
                break;
        }
        onSectionAttached(position);
    }

    /**
     * This function is called when a section from the navigation drawer is
     * attached to set the corresponding title in the activity.
     * @param number the number of the section in the navigation drawer.
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 0:
            default:
                mTitle = getString(R.string.title_section1);
                break;
        }
    }

    /**
     * Restore the ActionBar state.
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

}
