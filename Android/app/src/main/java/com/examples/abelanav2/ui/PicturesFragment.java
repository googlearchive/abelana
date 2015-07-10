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

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.examples.abelanav2.R;
import com.examples.abelanav2.grpcclient.AbelanaClient;
import com.examples.abelanav2.grpcclient.AbelanaClientException;
import com.examples.abelanav2.grpcclient.PhotoInfo;
import com.examples.abelanav2.ui.utils.FloatingActionButton;
import com.examples.abelanav2.ui.utils.PhotoAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * This Fragment handles the display of a list of picture cards.
 * Use the {@link PicturesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PicturesFragment extends Fragment
        implements View.OnClickListener, PhotoAdapter.PhotoAdapterListener,
        MenuItem.OnMenuItemClickListener {

    /**
     * This is the name of the argument for the mFragmentType field.
     */
    private static final String ARG_FRAGMENT_TYPE = "ArgFragmentType";
    /**
     * This is the type of the pictures list to display in this fragment: home,
     * favorites or my pictures.
     */
    private AbelanaClient.PhotoListType mFragmentType;
    /**
     * The recycler view that contains the picture cards.
     */
    private RecyclerView mRecyclerView;
    /**
     * The RecyclerView Adapter.
     */
    private PhotoAdapter mPhotoAdapter;
    /**
     * Indicates if we want to load more results or refresh completely the list.
     */
    private boolean mLoadMore = false;


    /**
     * Empty constructor.
     */
    public PicturesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param pFragmentType the type of pictures list for this fragment.
     * @return A new instance of fragment PicturesFragment.BackendConstants
     */
    public static PicturesFragment newInstance(
            AbelanaClient.PhotoListType pFragmentType) {
        PicturesFragment fragment = new PicturesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRAGMENT_TYPE, pFragmentType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mFragmentType = (AbelanaClient.PhotoListType) getArguments()
                    .getSerializable(ARG_FRAGMENT_TYPE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem refreshMenu = menu.add(R.string.action_refresh);
        refreshMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        refreshMenu.setOnMenuItemClickListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        refreshPhotoList();
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if (mFragmentType == AbelanaClient.PhotoListType.PHOTO_LIST_STREAM) {
            rootView = inflater.inflate(R.layout.fragment_pictures, container,
                    false);
            FloatingActionButton floatingActionButton =
                    (FloatingActionButton) rootView.findViewById(
                            R.id.fab_add_photo);
            floatingActionButton.setOnClickListener(this);
        } else {
            rootView = inflater.inflate(R.layout.fragment_likes, container,
                    false);
        }
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        
        mPhotoAdapter = new PhotoAdapter(new ArrayList<PhotoInfo>(),
                mFragmentType, this, mRecyclerView, this);
        mPhotoAdapter.setOnLoadMoreListener(
                new PhotoAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        loadMorePhotoList();
                    }
                });
        mRecyclerView.setAdapter(mPhotoAdapter);

        refreshPhotoList();

        registerForContextMenu(mRecyclerView);

        return rootView;
    }

    /**
     * Refreshes the photo list completely.
     */
    private void refreshPhotoList() {
        mLoadMore = false;
        mRecyclerView.getLayoutManager().scrollToPosition(0);
        new LoadPhotoList().execute(mFragmentType);
    }

    /**
     * Gets more photos.
     */
    private void loadMorePhotoList() {
        mLoadMore = true;
        if (((MainActivity) getActivity())
                .getAbelanaClient().hasMorePages(mFragmentType)) {
            new LoadPhotoList().execute(mFragmentType);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_photo:
                final FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.addToBackStack(null);
                ft.replace(R.id.container, UploadFragment.newInstance());
                ft.commit();
                break;
            default:
                break;
        }
    }

    /**
     * If an item is selected in the contextual menu of a picture.
     * @param item the item selected.
     * @return whether the event was handled or not.
     */
    public boolean onContextItemSelected(MenuItem item) {
        final int position = ((PhotoAdapter) mRecyclerView.getAdapter())
                    .getPosition();
        switch (item.getItemId()) {
            case R.id.context_menu_report:
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string
                                .photo_list_report_photo_title))
                        .setMessage(getString(R.string
                                .photo_list_report_photo_message))
                        .setPositiveButton(R.string.report,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new ReportTask().execute(position);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onPhotoCardClick(View v, final int position) {
        ((PhotoAdapter) mRecyclerView.getAdapter()).setPosition(position);
        switch (v.getId()) {
            case R.id.imageButtonThumbsUp:
                int vote = 1;
                CardView card = (CardView) v.getParent().getParent();
                card.findViewById(R.id.imageButtonThumbsDown)
                        .setSelected(false);
                if (card.findViewById(R.id.imageButtonThumbsUp).isSelected()) {
                    vote = 0;
                    card.findViewById(R.id.imageButtonThumbsUp)
                            .setSelected(false);
                } else {
                    card.findViewById(R.id.imageButtonThumbsUp)
                            .setSelected(true);
                }
                new VoteTask().execute(vote);
                break;
            case R.id.imageButtonThumbsDown:
                vote = -1;
                card = (CardView) v.getParent().getParent();
                card.findViewById(R.id.imageButtonThumbsUp).setSelected(false);
                if (card.findViewById(R.id.imageButtonThumbsDown)
                        .isSelected()) {
                    vote = 0;
                    card.findViewById(R.id.imageButtonThumbsDown)
                            .setSelected(false);
                } else {
                    card.findViewById(R.id.imageButtonThumbsDown)
                            .setSelected(true);
                }
                new VoteTask().execute(vote);
                break;
            case R.id.imageButtonWallpaper:
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string
                                .photo_list_wallpaper_title))
                        .setMessage(getString(R.string
                                .photo_list_wallpaper_message))
                        .setPositiveButton(R.string.set_wallpaper,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new WallpaperTask().execute();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                })
                        .setIcon(R.drawable.ic_wallpaper)
                        .show();
                break;
            case R.id.imageButtonEdit:
                final EditText editText = new EditText(getActivity()
                        .getApplicationContext());
                editText.setText(mPhotoAdapter.getPhotoList()
                        .get(position).description);
                editText.setTextColor(getResources().getColor(android.R.color
                        .black));
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string
                                .photo_list_edit_photo_title))
                        .setMessage(getString(R.string
                                .photo_list_edit_photo_message))
                        .setView(editText)
                        .setPositiveButton(R.string.edit,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new EditTask().execute(editText
                                                .getText().toString());
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                })
                        .setIcon(R.drawable.ic_edit)
                        .show();
                break;
            case R.id.imageButtonDelete:
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string
                                .photo_list_delete_photo_title))
                        .setMessage(getString(R.string
                                .photo_list_delete_photo_message))
                        .setPositiveButton(R.string.delete,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new DeleteTask().execute();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                })
                        .setIcon(R.drawable.ic_delete)
                        .show();
                break;
            default:
                break;
        }

    }

    /**
     * AsyncTask used to refresh the photo list.
     * Takes the photo list type as first argument, and the string "loadMore"
     * to indicate that we want to get the next page or not.
     */
    private class LoadPhotoList extends AsyncTask<AbelanaClient.PhotoListType,
            Void, String> {

        /**
         * Temporary list of new photos.
         */
        private List<PhotoInfo> mNewPhotoList;

        @Override
        protected void onPreExecute() {
            //add progress item
            mPhotoAdapter.getPhotoList().add(null);
            mPhotoAdapter.notifyItemInserted(mPhotoAdapter
                    .getPhotoList().size() - 1);
        }

        @Override
        protected String doInBackground(final AbelanaClient.PhotoListType...
                                                        params) {
            try {
                mNewPhotoList = ((MainActivity) getActivity())
                        .getAbelanaClient().getPhotoList(params[0], mLoadMore);
                return null;
            } catch (AbelanaClientException e) {
                mNewPhotoList = ((MainActivity) getActivity())
                        .getAbelanaClient().getPhotoListFromCache(params[0]);
                if (!e.getMessage().equals(
                        getString(R.string.abelana_auth_error_message))) {
                    return getString(R.id.data_shown_from_cache);
                } else {
                    return e.getMessage();
                }
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            }
            //remove progress item
            mPhotoAdapter.getPhotoList().remove(mPhotoAdapter
                    .getPhotoList().size() - 1);
            mPhotoAdapter.notifyItemRemoved(mPhotoAdapter.getPhotoList().size());

            int oldCount = mPhotoAdapter.getPhotoList().size();
            mPhotoAdapter.getPhotoList().clear();
            mPhotoAdapter.getPhotoList().addAll(mNewPhotoList);
            mPhotoAdapter.notifyItemRangeInserted(oldCount, mPhotoAdapter
                    .getPhotoList().size()-oldCount);


            mPhotoAdapter.setLoaded();
        }
    }

    /**
     * AsyncTask used to report the photo.
     */
    private class ReportTask extends AsyncTask<Integer, Void, String> {

        /**
         * The position in the list of the photo.
         */
        private int mPhotoPosition;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(final Integer... params) {
            try {
                mPhotoPosition = params[0];
                long photoId = mPhotoAdapter.getPhotoList().get(mPhotoPosition)
                        .photoId;
                boolean res = ((MainActivity) getActivity()).getAbelanaClient()
                        .flagPhoto(photoId);
                return (res) ? null : getString(R.string.server_error);
            } catch (AbelanaClientException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            } else {
                mPhotoAdapter.getPhotoList().remove(mPhotoPosition);
                mPhotoAdapter.notifyItemRemoved(mPhotoPosition);
            }
        }
    }

    /**
     * AsyncTask used to edit a photo.
     */
    private class EditTask extends AsyncTask<String, Void, String> {

        /**
         * The position in the list of the photo.
         */
        private int mPhotoPosition;

        /**
         * The new description.
         */
        private String mDescription;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(final String... params) {
            try {
                mPhotoPosition = mPhotoAdapter.getPosition();
                long photoId = mPhotoAdapter.getPhotoList().get(mPhotoPosition)
                        .photoId;
                mDescription = params[0];
                if (mDescription.length() < 1) {
                    return getString(R.string.upload_photo_description_missing);
                }
                boolean res = ((MainActivity) getActivity()).getAbelanaClient()
                        .editPhoto(photoId, mDescription);
                return (res) ? null : getString(R.string.server_error);
            } catch (AbelanaClientException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            } else {
                PhotoInfo photo = mPhotoAdapter.getPhotoList()
                        .get(mPhotoPosition);
                photo.description = mDescription;
                mPhotoAdapter.getPhotoList().set(mPhotoPosition, photo);
                mPhotoAdapter.notifyItemChanged(mPhotoPosition);
            }
        }
    }

    /**
     * AsyncTask used to delete a photo.
     */
    private class DeleteTask extends AsyncTask<Void, Void, String> {

        /**
         * The position in the list of the photo.
         */
        private int mPhotoPosition;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(final Void... params) {
            try {
                mPhotoPosition = mPhotoAdapter.getPosition();
                long photoId = mPhotoAdapter.getPhotoList().get(mPhotoPosition)
                        .photoId;
                boolean res = ((MainActivity) getActivity()).getAbelanaClient()
                        .deletePhoto(photoId);
                return (res) ? null : getString(R.string.server_error);
            } catch (AbelanaClientException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            } else {
                mPhotoAdapter.getPhotoList().remove(mPhotoPosition);
                mPhotoAdapter.notifyItemRemoved(mPhotoPosition);
            }
        }
    }

    /**
     * AsyncTask used to vote for a photo.
     */
    private class VoteTask extends AsyncTask<Integer, Void, String> {

        /**
         * The position in the list of the photo.
         */
        private int mPhotoPosition;
        /**
         * The new vote.
         */
        private int mVote;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(final Integer... params) {
            try {
                mPhotoPosition = mPhotoAdapter.getPosition();
                mVote = params[0];
                long photoId = mPhotoAdapter.getPhotoList().get(mPhotoPosition)
                        .photoId;
                boolean res = ((MainActivity) getActivity()).getAbelanaClient()
                        .votePhoto(photoId, mVote);
                return (res) ? null : getString(R.string.server_error);
            } catch (AbelanaClientException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            } else {
                PhotoInfo photo = mPhotoAdapter.getPhotoList()
                        .get(mPhotoPosition);
                photo.vote = mVote;
                mPhotoAdapter.getPhotoList().set(mPhotoPosition, photo);
            }
            mPhotoAdapter.notifyItemChanged(mPhotoPosition);
        }
    }

    /**
     * AsyncTask used to set a photo as Wallapper.
     */
    private class WallpaperTask extends AsyncTask<Void, Void, String> {

        /**
         * The position in the list of the photo.
         */
        private int mPhotoPosition;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(final Void... params) {
            try {
                mPhotoPosition = mPhotoAdapter.getPosition();
                String url = mPhotoAdapter.getPhotoList().get(mPhotoPosition)
                        .url;
                url = url.replace(".webp", "_o" + ".webp");
                WallpaperManager wpm = WallpaperManager
                        .getInstance(getActivity().getApplicationContext());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins);
                return null;
            } catch (IOException e) {
                return getString(R.string.wallpaper_error);
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result != null) {
                showErrorMessage(result);
            }
        }
    }

    /**
     * Displays a Toast with an error message.
     * @param message the message to display.
     */
    private void showErrorMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message,
                Toast.LENGTH_LONG).show();

        // If it is an auth error, let's redirect the user also
        if (message.equals(getString(R.string.abelana_auth_error_message))) {
            ((MainActivity) getActivity()).startGitkitActivity();
        }
    }
}
