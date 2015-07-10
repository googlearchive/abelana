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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.examples.abelanav2.R;
import com.examples.abelanav2.grpcclient.AbelanaClientException;

import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * This Fragment handles the upload of a new picture.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment
        implements MenuItem.OnMenuItemClickListener, View.OnClickListener {

    /**
     * The code for the select photo intent.
     */
    private static final int SELECT_PHOTO_INTENT = 100;

    /**
     * The photo to upload.
     */
    private Bitmap mBitmap = null;

    /**
     * We are retrieving an image.
     */
    private boolean mRetrieving = false;

    /**
     *  Required empty public constructor.
     */
    public UploadFragment() {
    }

    /**
     * Use this factory method to create a new instance of this fragment
     * using the provided parameters.
     * @return A new instance of fragment PicturesFragment.
     */
    public static UploadFragment newInstance() {
        return new UploadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem uploadMenu = menu.add(R.string.upload_menu_action);
        uploadMenu.setIcon(R.drawable.ic_done_white_24dp);
        uploadMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        uploadMenu.setOnMenuItemClickListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container,
                false);
        ImageView imageView = (ImageView) view.findViewById(R.id
                .imageViewUploadImage);
        imageView.setOnClickListener(this);
        if (!mRetrieving) {
            mRetrieving = true;
            selectPhoto();
        }
        return view;
    }

    /**
     * Starts the select photo intent.
     */
    private void selectPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO_INTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewUploadImage:
                selectPhoto();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent
            imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO_INTENT:
                if (resultCode == Activity.RESULT_OK) {
                    mRetrieving = true;
                    Uri selectedImage = imageReturnedIntent.getData();
                    new RetrieveBitmap().execute(selectedImage);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        uploadNewPhoto();
        return false;
    }

    /**
     * Uploads the new photo to the backend.
     */
    private void uploadNewPhoto() {
        EditText editText = (EditText) getActivity().findViewById(R.id
                .editTextUploadDescription);
        String description = editText.getText().toString();
        String error = "";
        if(!description.equals("") && mBitmap != null) {
            new UploadTask().execute(description);
        } else {
            if(description.equals("")) {
                editText.setError(getString(R.string
                        .upload_photo_description_missing));
                error += getString(R.string.upload_photo_description_missing);
            }
            if (mBitmap == null) {
                if(!error.equals("")) {
                    error += "\n";
                }
                error += getString(R.string.upload_photo_photo_missing);
            }
            new AlertDialog.Builder(getActivity())
                    .setTitle(getString(R.string
                            .upload_photo_error_dialog_title))
                    .setMessage(error)
                    .setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_error_black_48dp)
                    .show();
        }

    }

    /**
     * AsyncTask used to upload the photo.
     */
    private class UploadTask extends AsyncTask<String, Void, String> {

        /**
         * A progress dialog shown while the picture is uploading.
         */
        private ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            mProgress = ProgressDialog.show(getActivity(),
                    getString(R.string.upload_photo_progress_title),
                    getString(R.string.upload_photo_progress_message), true);
        }

        @Override
        protected String doInBackground(final String... params) {
            try {
                boolean res = ((MainActivity) getActivity()).getAbelanaClient()
                        .uploadPhoto(params[0], mBitmap);
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
            } else {
                showErrorMessage(result);
            }
        }
    }

    /**
     * AsyncTask used to retrieve the image selected.
     */
    private class RetrieveBitmap extends AsyncTask<Uri, Void, String> {

        /**
         * A progress dialog shown while the picture is uploading.
         */
        private ProgressDialog mProgress;

        /**
         * The reduced image.
         */
        private Bitmap mSmallImage;

        @Override
        protected void onPreExecute() {
            mProgress = ProgressDialog.show(getActivity(),
                    getString(R.string.upload_photo_progress_retrieve_title),
                    getString(R.string.upload_photo_progress_retrieve_message),
                    true);
        }

        @Override
        protected String doInBackground(final Uri... params) {
            try {
                InputStream imageStream = getActivity().getContentResolver()
                        .openInputStream(params[0]);
                mBitmap = BitmapFactory.decodeStream(imageStream);
                mSmallImage = downSampleBitmap(params[0]);
                return null;
            } catch (FileNotFoundException e) {
                return getString(R.string.upload_photo_retrieve_image_error);
            }
        }

        @Override
        protected void onPostExecute(final String result) {
            mProgress.dismiss();
            if (result == null) {
                ((ImageView) getActivity()
                        .findViewById(R.id.imageViewUploadImage))
                        .setImageBitmap(mSmallImage);
            } else {
                showErrorMessage(result);
            }
        }

        /**
         * Reduces the size of the bitmap for display.
         * @param selectedImage the image Uri.
         * @return the resized Bitmap.
         * @throws FileNotFoundException if the image was not found.
         */
        private Bitmap downSampleBitmap(Uri selectedImage) throws
                FileNotFoundException {

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getActivity().getContentResolver()
                    .openInputStream(selectedImage), null, options);

            // The new size we want to scale to
            ImageView imageView = (ImageView) getActivity()
                    .findViewById(R.id.imageViewUploadImage);
            int requiredSize = Math.min(imageView.getWidth(),
                    imageView.getHeight());

            // Find the correct scale value. It should be the power of 2.
            int width= options.outWidth, height = options.outHeight;
            int scale = 1;
            while (true) {
                if (width / 2 < requiredSize || height / 2 < requiredSize) {
                    break;
                }
                width /= 2;
                height /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getActivity().getContentResolver()
                    .openInputStream(selectedImage), null, options2);

        }
    }

    /**
     * Displays a Toast with an error message.
     * @param message the message to display.
     */
    private void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        // If it is an auth error, let's redirect the user also
        if (message.equals(getString(R.string.abelana_auth_error_message))) {
            ((MainActivity) getActivity()).startGitkitActivity();
        }
    }

    /**
     * Navigates to the MainActivity.
     */
    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }

}
