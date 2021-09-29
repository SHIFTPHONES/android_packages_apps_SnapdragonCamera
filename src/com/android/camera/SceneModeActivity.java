/*
 * Copyright (c) 2016, The Linux Foundation. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.camera.ui.DotsView;
import com.android.camera.ui.DotsViewItem;
import com.android.camera.ui.RotateImageView;
import com.android.camera.util.CameraUtil;

import org.codeaurora.snapcam.R;
import org.lineageos.quickreader.ScannerActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SceneModeActivity extends Activity {
    private static final int ELEMENTS_PER_PAGE = 12;

    private ViewPager mPager;
    private View mCloseButton;
    private RotateImageView mButton;
    private DotsView mDotsView;
    private MyPagerAdapter mAdapter;
    private SettingsManager mSettingsManager;
    private List<CharSequence> mEntries;
    private List<CharSequence> mEntryValues;
    private List<Integer> mThumbnails;
    private int mCurrentScene;
    private int mNumElement;
    private int mNumPage;

    private static class PageItems implements DotsViewItem {
        int number;

        public PageItems(int number) {
            this.number = number;
        }

        @Override
        public int getTotalItemNums() {
            return number;
        }

        @Override
        public boolean isChosen(int index) {
            return true;
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mPager != null) {
            mAdapter = new MyPagerAdapter(this);
            mPager.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final boolean isSecureCamera = getIntent().getBooleanExtra(
                CameraUtil.KEY_IS_SECURE_CAMERA, false);
        if (isSecureCamera) {
            setShowInLockScreen();
        }
        setContentView(R.layout.scene_mode_menu_layout);
        mSettingsManager = SettingsManager.getInstance();

        mCurrentScene = mSettingsManager.getValueIndex(SettingsManager.KEY_SCENE_MODE);

        mEntries = loadEntries();
        mEntryValues = loadEntryValues();
        mThumbnails = loadThumbnails();

        mNumElement = mThumbnails.size();
        int pages = mNumElement / ELEMENTS_PER_PAGE;
        if (mNumElement % ELEMENTS_PER_PAGE != 0) pages++;
        mNumPage = pages;

        mAdapter = new MyPagerAdapter(this);

        mPager = findViewById(R.id.pager);
        mPager.setOverScrollMode(ViewPager.OVER_SCROLL_NEVER);
        mPager.setAdapter(mAdapter);

        mCloseButton = findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(v -> finish());

        int pageCount = mAdapter.getCount();
        mDotsView = findViewById(R.id.page_indicator);
        mPager.setCurrentItem(mCurrentScene / ELEMENTS_PER_PAGE);
        mDotsView.update(mCurrentScene / ELEMENTS_PER_PAGE, 0f);
        if (pageCount > 1) {
            mDotsView.setItems(new PageItems(pageCount));
            mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    mDotsView.update(position, positionOffset);
                }
            });
        } else {
            mDotsView.setVisibility(View.GONE);
        }

        mButton = findViewById(R.id.setting_button);
        mButton.setOnClickListener(v -> {
            final Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            intent.putExtra(CameraUtil.KEY_IS_SECURE_CAMERA, isSecureCamera);
            startActivity(intent);
            finish();
        });
    }

    public int getElmentPerPage() {
        return ELEMENTS_PER_PAGE;
    }

    public int getNumberOfPage() {
        return mNumPage;
    }

    public int getNumberOfElement() {
        return mNumElement;
    }

    public int getCurrentPage() {
        return mPager.getCurrentItem();
    }

    public List<CharSequence> getEntries() {
        return mEntries;
    }

    public List<CharSequence> getEntryValues() {
        return mEntryValues;
    }

    public List<Integer> getThumbnails() {
        return mThumbnails;
    }

    public int getCurrentScene() {
        return mCurrentScene;
    }

    private void setShowInLockScreen() {
        // Change the window flags so that secure camera can show when locked
        Window win = getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        win.setAttributes(params);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    void openQr() {
        startActivity(new Intent(this, ScannerActivity.class));
    }

    private List<CharSequence> loadEntries() {
        final CharSequence[] sceneEntries = mSettingsManager.getEntries(SettingsManager.KEY_SCENE_MODE);
        final ArrayList<CharSequence> entryList = new ArrayList<>();
        Collections.addAll(entryList, sceneEntries);
        return entryList;
    }

    private List<CharSequence> loadEntryValues() {
        final CharSequence[] sceneEntryValues = mSettingsManager.getEntryValues(SettingsManager.KEY_SCENE_MODE);
        final ArrayList<CharSequence> valueList = new ArrayList<>();
        Collections.addAll(valueList, sceneEntryValues);
        return valueList;
    }

    private List<Integer> loadThumbnails() {
        final int[] sceneThumbnails = mSettingsManager.getResource(SettingsManager.KEY_SCENE_MODE,
                SettingsManager.RESOURCE_TYPE_THUMBNAIL);
        final ArrayList<Integer> thumbnailList = new ArrayList<>();
        for (final int sceneThumbnail : sceneThumbnails) {
            thumbnailList.add(sceneThumbnail);
        }
        return thumbnailList;
    }
}

class MyPagerAdapter extends PagerAdapter {
    private final SceneModeActivity mActivity;

    public MyPagerAdapter(SceneModeActivity activity) {
        mActivity = activity;
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        final int orientation = mActivity.getResources().getConfiguration().orientation;
        final int layoutResId = (orientation == Configuration.ORIENTATION_PORTRAIT) ? R.layout.scene_mode_grid : R.layout.scene_mode_grid_landscape;

        final ViewGroup rootView = (ViewGroup) mActivity.getLayoutInflater().inflate(layoutResId, viewGroup, false);
        GridView mGridView = rootView.findViewById(R.id.grid);
        mGridView.setAdapter(new GridAdapter(mActivity, position));
        viewGroup.addView(rootView);

        mGridView.setOnItemClickListener((parent, view, itemPosition, id) -> {
            final int page = mActivity.getCurrentPage();
            final int index = page * mActivity.getElmentPerPage() + itemPosition;
            // Clear bg
            for (int j = 0; j < parent.getChildCount(); j++) {
                View v = parent.getChildAt(j);
                if (v != null) {
                    v.setBackground(null);
                }
            }

            final List<CharSequence> valueList = mActivity.getEntryValues();
            final String entryValue = valueList.get(index).toString();
            if (("" + SettingsManager.SCENE_MODE_SHIFT_QR_READER).equals(entryValue)) {
                mActivity.openQr();
            } else {
                SettingsManager.getInstance().setValueIndex(SettingsManager.KEY_SCENE_MODE, index);
            }

            mActivity.finish();
        });

        return rootView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // do nothing
    }

    @Override
    public int getCount() {
        return mActivity.getNumberOfPage();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static class GridAdapter extends BaseAdapter {
        private final SceneModeActivity mActivity;
        private final LayoutInflater mInflater;
        private final int mPage;

        public GridAdapter(SceneModeActivity activity, int i) {
            mActivity = activity;
            mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mPage = i;
        }

        @Override
        public int getCount() {
            int elem = mActivity.getElmentPerPage();
            if (mPage == mActivity.getNumberOfPage() - 1) {
                elem = mActivity.getNumberOfElement() - mPage * elem;
            }
            return elem;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            ViewHolder viewHolder;

            if (view == null) {
                view = mInflater.inflate(R.layout.scene_mode_menu_view, parent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final List<CharSequence> entryList = mActivity.getEntries();
            final List<Integer> thumbnailList = mActivity.getThumbnails();
            final int idx = position + mPage * mActivity.getElmentPerPage();

            final int activeSceneModeIndex = SettingsManager.getInstance().getValueIndex(SettingsManager.KEY_SCENE_MODE);
            final boolean isSceneActive = (activeSceneModeIndex == idx);
            viewHolder.imageView.setBackgroundResource(isSceneActive ? R.drawable.bg_custom_icon_active : R.drawable.bg_custom_icon);
            viewHolder.imageView.setImageResource(thumbnailList.get(idx));
            viewHolder.textTitle.setText(entryList.get(position + mPage * mActivity.getElmentPerPage()));

            return view;
        }

        private static class ViewHolder {
            public final ImageView imageView;
            public final TextView textTitle;

            ViewHolder(final View rootView) {
                imageView = rootView.findViewById(R.id.image);
                textTitle = rootView.findViewById(R.id.label);
            }
        }
    }
}
