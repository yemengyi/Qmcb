package com.gongdian.qmcb.activity.photo;
/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.cache.image.AbImageBaseCache;
import com.ab.image.AbImageLoader;
import com.ab.util.AbFileUtil;
import com.ab.util.AbStrUtil;
import com.gongdian.qmcb.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

public class ShowPhotoActivity extends AbActivity {

    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %%";

    private ImageView mImageView,back;
    private TextView mCurrMatrixTv;
    private PhotoViewAttacher mAttacher;
    private Toast mCurrentToast;
    private AbImageLoader mAbImageLoader = null;
    int count = 0;
    long firClick;
    long secClick;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_photo);

        mImageView = (ImageView) findViewById(R.id.iv_photo);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

//        mImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_DOWN == event.getAction()) {
//                    count++;
//                    if (count == 1) {
//                        firClick = System.currentTimeMillis();
//
//                    } else if (count == 2) {
//                        secClick = System.currentTimeMillis();
//                        if (secClick - firClick < 500) {
//                            //双击事件
//                            mAbTitleBar.setVisibility(View.GONE);
//                        }
//                        count = 0;
//                        firClick = 0;
//                        secClick = 0;
//
//                    }
//                }
//                return true;
//            }
//        });


        mCurrMatrixTv = (TextView) findViewById(R.id.tv_current_matrix);
        mAbImageLoader = new AbImageLoader(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if (!AbStrUtil.isEmpty(info)) {
            mCurrMatrixTv.setVisibility(View.VISIBLE);
            mCurrMatrixTv.setText(info);
        } else {
            mCurrMatrixTv.setVisibility(View.GONE);
        }

        String imagePath = intent.getStringExtra("filepath");
        //从缓存中获取图片，很重要否则会导致页面闪动
        Bitmap bitmap = AbImageBaseCache.getInstance().getBitmap(imagePath);
        //缓存中没有则从网络和SD卡获取
        if (bitmap == null) {
            mImageView.setImageResource(R.drawable.image_loading);
            if (imagePath.indexOf("http://") != -1) {
                //图片的下载
                mAbImageLoader.download(mImageView, imagePath, -1, -1, new AbImageLoader.OnImageListener() {
                    @Override
                    public void onError(ImageView imageView) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onEmpty(ImageView imageView) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onLoading(ImageView imageView) {

                    }

                    @Override
                    public void onSuccess(ImageView imageView, Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                        //本地图片
                        // BitmapDrawable bd= new BitmapDrawable(path);
                        // mImageView.setImageDrawable(bd);

                        // The MAGIC happens here!
                        mAttacher = new PhotoViewAttacher(mImageView);

                        // Lets attach some listeners, not required though!
                        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
                        mAttacher.setOnPhotoTapListener(new PhotoTapListener());
                    }
                });
                return;

            } else if (imagePath.indexOf("/") == -1) {
                //在资源文件里索引图片
                try {
                    int res = Integer.parseInt(imagePath);
                    mImageView.setImageDrawable(this.getResources().getDrawable(res));
                } catch (Exception e) {
                    mImageView.setImageResource(R.drawable.image_error);
                }
            } else {
                Bitmap mBitmap = AbFileUtil.getBitmapFromSD(new File(imagePath));
                if (mBitmap != null) {
                    mImageView.setImageBitmap(mBitmap);
                } else {
                    // 无图片时显示
                    mImageView.setImageResource(R.drawable.image_empty);
                }
            }
        } else {
            //直接显示
            mImageView.setImageBitmap(bitmap);
        }


        //本地图片
        // BitmapDrawable bd= new BitmapDrawable(path);
        // mImageView.setImageDrawable(bd);

        // The MAGIC happens here!
        mAttacher = new PhotoViewAttacher(mImageView);

        // Lets attach some listeners, not required though!
        mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        mAttacher.setOnPhotoTapListener(new PhotoTapListener());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Need to call clean-up
        mAttacher.cleanup();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem zoomToggle = menu.findItem(R.id.menu_zoom_toggle);
        zoomToggle.setTitle(mAttacher.canZoom() ? R.string.menu_zoom_disable : R.string.menu_zoom_enable);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_zoom_toggle:
                mAttacher.setZoomable(!mAttacher.canZoom());
                return true;

            case R.id.menu_scale_fit_center:
                mAttacher.setScaleType(ScaleType.FIT_CENTER);
                return true;

            case R.id.menu_scale_fit_start:
                mAttacher.setScaleType(ScaleType.FIT_START);
                return true;

            case R.id.menu_scale_fit_end:
                mAttacher.setScaleType(ScaleType.FIT_END);
                return true;

            case R.id.menu_scale_fit_xy:
                mAttacher.setScaleType(ScaleType.FIT_XY);
                return true;

            case R.id.menu_scale_scale_center:
                mAttacher.setScaleType(ScaleType.CENTER);
                return true;

            case R.id.menu_scale_scale_center_crop:
                mAttacher.setScaleType(ScaleType.CENTER_CROP);
                return true;

            case R.id.menu_scale_scale_center_inside:
                mAttacher.setScaleType(ScaleType.CENTER_INSIDE);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class PhotoTapListener implements OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            if (null != mCurrentToast) {
                mCurrentToast.cancel();
            }

//            mCurrentToast = Toast.makeText(ShowPhotoActivity.this,
//                    String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage), Toast.LENGTH_SHORT);
//            mCurrentToast.show();
        }
    }

    private class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {
//            mCurrMatrixTv.setText(rect.toString());
        }
    }


}
