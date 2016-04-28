/*
* Copyright 2015 The Android Open Source Project
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

package com.gongdian.qmcb.permission;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ViewAnimator;

import com.gongdian.qmcb.R;
import com.gongdian.qmcb.permission.listener.AbstractPermissionListener;
import com.gongdian.qmcb.permission.listener.PermissionListener;
import com.gongdian.qmcb.permission.logger.Log;
import com.gongdian.qmcb.permission.logger.LogFragment;
import com.gongdian.qmcb.permission.logger.LogWrapper;
import com.gongdian.qmcb.permission.logger.MessageOnlyLogFilter;
import com.gongdian.qmcb.permission.runtimepermissions.RuntimePermissionsFragment;

/*****
 * 使用例子
 */

public class MainActivity2 extends SampleActivityBase implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "MainActivity";


    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CONTACTS = 1;
    private static final int REQUEST_IMPORTANT_PERMISSION = 2;
    private static final int REQUEST_LOCATION = 3;
    private static final int REQUEST_STORAGE = 4;
    private ImageButton imageButton;


    /**
     * Permissions required to read and write contacts. Used by the {@link --ContactsFragment}.
     */
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    // Whether the Log Fragment is currently shown.
    private boolean mLogShown;
    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //处理权限的授权结果
        PermissionsDispatcher.onRequestPermissionsResult(this, requestCode, permissions, grantResults, permissionListener);
    }


    /**
     * 权限监听器
     */
    private PermissionListener permissionListener = new AbstractPermissionListener() {
        @Override
        public void onPermissionsGranted(Activity act, int requestCode, int[] grantResults, String... permissions) {
            if (requestCode == REQUEST_CAMERA) {
                Log.i(TAG,getString(R.string.permissions_camera_granted));
                Snackbar.make(mLayout, R.string.permissions_camera_granted, Snackbar.LENGTH_SHORT).show();
                showCameraPreview();
            } else if (requestCode == REQUEST_CONTACTS) {
                Log.i(TAG,getString(R.string.permissions_contacts_granted) );
                Snackbar.make(mLayout, R.string.permissions_contacts_granted, Snackbar.LENGTH_SHORT).show();
                showContactDetails();
            } else if (requestCode == REQUEST_IMPORTANT_PERMISSION) {
                //TODO 不需要做什么事
                //TODO don't need to do anything
                Snackbar.make(mLayout, R.string.permissions_impotent_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_LOCATION) {
                Log.i(TAG, getString(R.string.permissions_location_granted) );
                Snackbar.make(mLayout, R.string.permissions_location_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_STORAGE) {
                Log.i(TAG, getString(R.string.permissions_storage_granted) );
                Snackbar.make(mLayout, R.string.permissions_storage_granted, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPermissionsDenied(Activity act, int requestCode, int[] grantResults, String... permissions) {

            if (requestCode == REQUEST_CAMERA) {
                //获取摄像头的权限的授权失败
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_CONTACTS) {
                // 获取通讯录读取权限的授权失败
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_IMPORTANT_PERMISSION) {
                //获取重要的权限
                Log.i(TAG, "important permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_LOCATION) {
                //获取文件的权限
                Log.i(TAG, "LOCATION permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_STORAGE) {
                //获取存储的权限
                Log.i(TAG, "storage permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onShowRequestPermissionRationale(Activity act, int requestCode, boolean isShowRationale, String... permissions) {
            if (requestCode == REQUEST_CAMERA) {
                if (isShowRationale) {
                    //获取摄像头的权限的授权:显示为什么需要这个权限的说明
                    Log.i(TAG, "Displaying camera permission rationale to provide additional context.");
                    Snackbar.make(mLayout, R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_CAMERA, new String[]{Manifest.permission.CAMERA});
                                }
                            })
                            .show();
                } else {
                    //直接获取摄像头的权限的授权
                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_CAMERA, new String[]{Manifest.permission.CAMERA});
                }

            } else if (requestCode == REQUEST_CONTACTS) {
                if (isShowRationale) {
                    //获取通讯录的权限的授权:显示为什么需要这个权限的说明
                    Log.i(TAG, "Displaying contacts permission rationale to provide additional context.");
                    Snackbar.make(mLayout, R.string.permission_contacts_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_CONTACTS, PERMISSIONS_CONTACT);
                                }
                            })
                            .show();
                } else {
                    //直接获取通讯录的权限的授权
                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_CONTACTS, PERMISSIONS_CONTACT);
                }

            } else if (requestCode == REQUEST_IMPORTANT_PERMISSION) {
                if (isShowRationale) {

                    //获取重要的权限的授权:显示为什么需要这个权限的说明

                    Log.i(TAG, "Displaying important permission rationale to provide additional context.");
                    Snackbar.make(mLayout, R.string.permission_important_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestImportantPermissions();
                                }
                            })
                            .show();
                } else {
                    //直接获取重要的权限的授权
                    requestImportantPermissions();

                }

            } else if (requestCode == REQUEST_LOCATION) {
                if (isShowRationale) {
                    //获取文件的权限的授权:显示为什么需要这个权限的说明
                    Log.i(TAG, "Displaying LOCATION permission rationale to provide additional context.");
                    Snackbar.make(mLayout, R.string.permission_location_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_LOCATION, PERMISSIONS_LOCATION);
                                }
                            })
                            .show();
                } else {
                    //直接获取文件的权限的授权
                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_LOCATION, PERMISSIONS_LOCATION);
                }


            } else if (requestCode == REQUEST_STORAGE) {
                if (isShowRationale) {
                    //获取存储权限的授权:显示为什么需要这个权限的说明
                    Log.i(TAG, "Displaying storage permission rationale to provide additional context.");
                    Snackbar.make(mLayout, R.string.permission_storage_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_STORAGE, PERMISSIONS_STORAGE);
                                }
                            })
                            .show();
                } else {
                    //直接获取通讯录的权限的授权
                    PermissionsDispatcher.requestPermissions(MainActivity2.this, REQUEST_STORAGE, PERMISSIONS_STORAGE);
                }
            }
        }
    };


    /**
     * 点击事件----各种检查
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showCamera(View view) {
        //检查授权1
        PermissionsDispatcher.checkPermissions(this, REQUEST_CAMERA, permissionListener, new String[]{Manifest.permission.CAMERA});
    }

    public void showContacts(View v) {
        //检查授权2
        PermissionsDispatcher.checkPermissions(this, REQUEST_CONTACTS, permissionListener, PERMISSIONS_CONTACT);
    }


    public void showLocation(View v) {
        //检查授权3
        PermissionsDispatcher.checkPermissions(this, REQUEST_LOCATION, permissionListener, PERMISSIONS_LOCATION);
    }

    public void showStorage(View v) {
        //检查授权4
        PermissionsDispatcher.checkPermissions(this, REQUEST_STORAGE, permissionListener, PERMISSIONS_STORAGE);
    }




    /*****
     * 获取重要的权限ALL
     */
    private void requestImportantPermissions() {
        PermissionsDispatcher.requestPermissions(this, REQUEST_IMPORTANT_PERMISSION,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE});
    }



    /* Note: Methods and definitions below are only used to provide the UI for this sample and are
    not relevant for the execution of the runtime permissions API. */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
    }

    public void onBackClick(View view) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main2);
      //  getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_mian2_titlebar); //titlebar为自己标题栏的布局

        mLayout = findViewById(R.id.sample_main_layout);
        //软件activity的布局

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // TODO: 12/28/15  在这里加载的按钮Fragment
            RuntimePermissionsFragment fragment = new RuntimePermissionsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commitAllowingStateLoss();
        }

        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();

        //TODO 在应用打开后， 获取重要的权限
        checkImportantPermissions();

//        imageButton = (ImageButton)findViewById(R.id.back);
//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

    private void checkImportantPermissions() {
        //检查授权all
        PermissionsDispatcher.checkPermissions(this, REQUEST_IMPORTANT_PERMISSION, permissionListener,
                new String[]{Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    /**
     * Display the {@link CameraPreviewFragment} in the content area if the required Camera
     * permission has been granted.
     */
    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance())
                .addToBackStack("contacts")
                .commitAllowingStateLoss();
    }

    /**
     * Display the {@link --ContactsFragment} in the content area if the required contacts
     * permissions
     * have been granted.
     */
    private void showContactDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commitAllowingStateLoss();
    }


}
