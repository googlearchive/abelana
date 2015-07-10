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

package com.examples.abelanav2.ui.utils;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.examples.abelanav2.R;
import com.examples.abelanav2.grpcclient.AbelanaClient;
import com.examples.abelanav2.grpcclient.PhotoInfo;
import com.examples.abelanav2.ui.PicturesFragment;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * This is the adapter used to put the photos in the recycler view cards.
 */
public class PhotoAdapter extends RecyclerView.Adapter {

    /**
     * The list of photo to display.
     */
    private List<PhotoInfo> mPhotoList;

    /**
     * The position of the selected item in the list where the contextual
     * menu was called.
     */
    private int mPosition;

    /**
     * The fragment type.
     */
    private AbelanaClient.PhotoListType mFragmentType;
    /**
     * The fragment.
     */
    private PicturesFragment mFragment;
    /**
     * A listener for events in the Photo cards.
     */
    private PhotoAdapterListener mListener;

    /**
     * The type of ViewHolder for a progress indicator.
     */
    private static final int VIEW_PROGRESS = 0;
    /**
     * The type of ViewHolder for a Photo.
     */
    private static final int VIEW_PHOTO = 1;

    /**
     * The minimum amount of items to have below your current scroll position
     * before loading more.
     */
    private int mVisibleThreshold = 2;
    /**
     * The last visible item in the recyclerview and the total number of items.
     */
    private int mLastVisibleItem, mTotalItemCount;
    /**
     * Are we already loading images.
     */
    private boolean mLoading;
    /**
     * A listener to inform that we need to fetch more photos.
     */
    private OnLoadMoreListener mOnLoadMoreListener;


    /**
     * Constructor.
     * @param pPhotoList the initial list of photos.
     * @param pFragmentType the type of fragment using the adapter.
     * @param pPhotoAdapterListener the listener for events on the photo cards.
     * @param recyclerView the RecyclerView.
     * @param fragment the fragment using the adapter.
     */
    public PhotoAdapter(List<PhotoInfo> pPhotoList,
                        AbelanaClient.PhotoListType pFragmentType,
                        PhotoAdapterListener pPhotoAdapterListener,
                        RecyclerView recyclerView,
                        PicturesFragment fragment) {
        this.mPhotoList = pPhotoList;
        this.mFragmentType = pFragmentType;
        this.mListener = pPhotoAdapterListener;
        this.mFragment = fragment;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager =
                    (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.setOnScrollListener(
                    new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    mTotalItemCount = linearLayoutManager.getItemCount();
                    mLastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!mLoading && mTotalItemCount
                            <= (mLastVisibleItem + mVisibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mLoading = true;
                    }
                }
            });
        }
    }

    /**
     * Returns the photo list.
     * @return the photo list.
     */
    public List<PhotoInfo> getPhotoList() {
        return mPhotoList;
    }

    /**
     * Returns the position of the item in the list where the contextual
     * menu was called.
     * @return the item position.
     */
    public int getPosition() {
        return mPosition;
    }

    /**
     * When the contextual menu is called, sets the position of the item in
     * the list.
     * @param position the item position.
     */
    public void setPosition(int position) {
        this.mPosition = position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mPhotoList.get(position) != null) return VIEW_PHOTO;
        else return VIEW_PROGRESS;
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof PhotoViewHolder) {
            final PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            PhotoInfo pi = mPhotoList.get(i);
            photoViewHolder.vDate.setText(getDate(pi.date));
            photoViewHolder.vDescription.setText(pi.description);
            if (mFragmentType == AbelanaClient.PhotoListType.PHOTO_LIST_STREAM
                    || mFragmentType == AbelanaClient
                    .PhotoListType.PHOTO_LIST_LIKES) {
                if (pi.vote == 1) {
                    photoViewHolder.vVoteThumbsUp.setSelected(true);
                    photoViewHolder.vVoteThumbsDown.setSelected(false);
                }
                if (pi.vote == -1) {
                    photoViewHolder.vVoteThumbsDown.setSelected(true);
                    photoViewHolder.vVoteThumbsUp.setSelected(false);
                }
                if (pi.vote == 0) {
                    photoViewHolder.vVoteThumbsUp.setSelected(false);
                    photoViewHolder.vVoteThumbsDown.setSelected(false);
                }
                photoViewHolder.vEdit.setVisibility(View.GONE);
                photoViewHolder.vDelete.setVisibility(View.GONE);
                photoViewHolder.vVoteThumbsUp.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPosition(photoViewHolder.getPosition());
                                mListener.onPhotoCardClick(v, getPosition());
                            }
                        });
                photoViewHolder.vVoteThumbsDown.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPosition(photoViewHolder.getPosition());
                                mListener.onPhotoCardClick(v, getPosition());
                            }
                        });
            }
            if (mFragmentType == AbelanaClient.PhotoListType.PHOTO_LIST_MINE) {
                photoViewHolder.vVoteThumbsUp.setVisibility(View.GONE);
                photoViewHolder.vVoteThumbsDown.setVisibility(View.GONE);
                photoViewHolder.vEdit.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPosition(photoViewHolder.getPosition());
                                mListener.onPhotoCardClick(v, getPosition());
                            }
                        });
                photoViewHolder.vDelete.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setPosition(photoViewHolder.getPosition());
                                mListener.onPhotoCardClick(v, getPosition());
                            }
                        });
            }
            photoViewHolder.vSetWallpaper.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setPosition(photoViewHolder.getPosition());
                            mListener.onPhotoCardClick(v, getPosition());
                        }
                    });
            photoViewHolder.itemView.setOnLongClickListener(
                    new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            setPosition(photoViewHolder.getPosition());
                            return false;
                        }
                    });

            Glide.with(mFragment)
                    .load(pi.url)
                    .crossFade()
                    .into(photoViewHolder.vPhoto);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * Indicates that we are no longer fetching data.
     */
    public void setLoaded() {
        mLoading = false;
    }

    /**
     * Interface used to communicate back with the fragment/activity that we
     * need to get more data for the RecyclerView.
     */
    public interface OnLoadMoreListener {
        /**
         * Called when we need to get more data.
         */
        void onLoadMore();
    }

    /**
     * Adds a LoadMoreListener to this adapter (will be called when we need
     * more data).
     * @param onLoadMoreListener the listener.
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * Interface used to communicate when an even happens on a picture.
     */
    public interface PhotoAdapterListener {
        /**
         * Called when a button on a photo card is clicked.
         * @param v the view clicked.
         * @param position the position in the list of photos.
         */
        void onPhotoCardClick(View v, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                              int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_PHOTO) {
            View photoView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.photo_card, viewGroup, false);
            viewHolder = new PhotoViewHolder(photoView);
        } else {
            View progressBarView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.progress_item, viewGroup, false);

            viewHolder = new ProgressViewHolder(progressBarView);
        }
        return viewHolder;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(viewHolder);
    }

    /**
     * Returns the date as a String.
     * @param time timestamp in microseconds.
     * @return a String formatted date.
     */
    private String getDate(long time) {
        return android.text.format.DateFormat.getDateFormat(mFragment
                .getActivity().getApplicationContext())
                .format(new Date(time / 1000));
    }

    /**
     * A ViewHolder that holds all the useful UI elements to display a picture.
     */
    public class PhotoViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {
        /**
         * The photo ImageView.
         */
        public ImageView vPhoto;
        /**
         * The date TextView.
         */
        public TextView vDate;
        /**
         * The Description TextView.
         */
        public TextView vDescription;
        /**
         * The ThumbsUp button.
         */
        public ImageButton vVoteThumbsUp;
        /**
         * The ThumbsDown button.
         */
        public ImageButton vVoteThumbsDown;
        /**
         * The edit button.
         */
        public ImageButton vSetWallpaper;
        /**
         * The edit button.
         */
        public ImageButton vEdit;
        /**
         * The edit button.
         */
        public ImageButton vDelete;

        /**
         * Creates a ViewHolder from a View.
         * @param v the view.
         */
        public PhotoViewHolder(View v) {
            super(v);
            vPhoto = (ImageView) v.findViewById(R.id.imageViewPhoto);
            vDate = (TextView) v.findViewById(R.id.textViewDate);
            vDescription = (TextView) v.findViewById(R.id
                    .textViewDescription);
            vVoteThumbsUp = (ImageButton) v.findViewById(R.id
                    .imageButtonThumbsUp);
            vVoteThumbsDown = (ImageButton) v.findViewById(R.id
                    .imageButtonThumbsDown);
            vSetWallpaper = (ImageButton) v.findViewById(R.id
                    .imageButtonWallpaper);
            vEdit = (ImageButton) v.findViewById(R.id.imageButtonEdit);
            vDelete = (ImageButton) v.findViewById(R.id.imageButtonDelete);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.context_menu_report, Menu.NONE, R.string
                    .context_menu_report);
        }
    }

    /**
     * A ViewHolder that contains a progressbar.
     */
    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        /**
         * The progress bar.
         */
        public ProgressBar progressBar;

        /**
         * Constructor.
         * @param v the view.
         */
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
