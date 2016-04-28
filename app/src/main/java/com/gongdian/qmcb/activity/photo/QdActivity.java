package com.gongdian.qmcb.activity.photo;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbAppUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.ImageShowAdapter;
import com.gongdian.qmcb.model.Qd;
import com.gongdian.qmcb.others.GrapeGridview;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.PictureUtil;
import com.gongdian.qmcb.utils.WebServiceUntils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class QdActivity extends AbActivity {
    @AbIocView(id = R.id.dwmc)
    TextView mView_dwmc;
    @AbIocView(id = R.id.kcry)
    TextView mView_kcry;
    @AbIocView(id = R.id.addBtn)
    Button addBtn;
    private MyApplication application;
    private GrapeGridview mGridView = null;
    private ImageShowAdapter mImagePathAdapter = null;
    private ArrayList<String> mPhotoList = null;
    private int selectIndex = 0;
    private int camIndex = 0;
    private View mAvatarView = null;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private File mCurrentSmallFile;
    private String mFileName;

    private DialogFragment mAlertDialog = null;
    private AbHttpUtil mAbHttpUtil = null;
    private LocationClient mLocationClient;
    private TextView LocationResult;
    private String flag = null;

    private boolean isCommit = false;
    private String cameraPath = String.valueOf(R.drawable.chat_tool_camera);
    private int choose = -1;
    private Qd mQd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_qd);
        application = (MyApplication) abApplication;
        mLocationClient = application.mLocationClient;
        LocationResult = (TextView) findViewById(R.id.textView1);
        LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        application.mLocationResult = LocationResult;
        mView_kcry.setText(application.getUser().getMc() + " "+application.getUser().getFzr());

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("到岗签到");
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        //获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        //初始化AbSqliteStorage

        //默认
        mGridView = (GrapeGridview) findViewById(R.id.myGrid);
        DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(this); //获取屏幕长宽
        int w = displayMetrics.widthPixels; //720 1440
        int h = displayMetrics.heightPixels; //1280 2392

        mPhotoList = new ArrayList<>();
        mPhotoList.add(cameraPath);
        mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, (w - 100) / 3, (w - 100) / 3);
        mGridView.setAdapter(mImagePathAdapter);

        //初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_ALERT, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectIndex = position;
                if (camIndex > 2) {
                    return;
                }
                //点击了拍照图片
                if (AbStrUtil.isEquals(mPhotoList.get(0),cameraPath)) {
                    if (application.mLocation.isSucess() && !AbStrUtil.isEmpty(application.mLocation.getAddress())) {
                        doPickPhotoAction();
                    } else {
                        MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_CONFIRM, "未取得定位信息,请稍后再试...");
                    }
                } else {
                    showDiag(position);
                }
            }

        });

        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addBtn.setEnabled(false);
                //同时已保存
                if (application.mLocation.isSucess() && !AbStrUtil.isEmpty(application.mLocation.getAddress())) {
                    final AbLoadDialogFragment mDialogFragment = AbDialogUtil.showLoadDialog(QdActivity.this, R.drawable.list_load, "请稍候...", AbDialogUtil.ThemeLightPanel);
                    commit();
                } else {
                    MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_CONFIRM, "未取得定位信息,请稍后再试...");
                }
            }
        });


        initGPS();

        initLocation();


    }

    /**
     * 从照相机获取
     */
    private void doPickPhotoAction() {
        String status = Environment.getExternalStorageState();
        //判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            doTakePhoto();
        } else {
            MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_ALERT, "没有可用的存储卡");
        }
    }

    /**
     * 拍照获取图片并保存原图
     */
    protected void doTakePhoto() {
        try {
            mFileName = System.currentTimeMillis() + ".jpg";
            mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            //intent.putExtra()
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_ALERT, "未找到系统相机程序");
        }
    }

    /**
     * 描述：因为调用了Camera和Gally所以要判断他们各自的返回情况,
     * 他们启动时是这样的startActivityForResult
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CAMERA_WITH_DATA:
                try {
                    mCurrentSmallFile = new File(PHOTO_DIR, "small_" + mCurrentPhotoFile.getName());
                    Bitmap bm = PictureUtil.getSmallBitmap(mCurrentPhotoFile.getPath());
                    FileOutputStream fos = new FileOutputStream(mCurrentSmallFile);
                    bm.compress(Bitmap.CompressFormat.JPEG, Constant.imageCompress, fos);
                    AbFileUtil.deleteFile(mCurrentPhotoFile);
                } catch (Exception e) {
                }
                String currentFilePath = mCurrentSmallFile.getPath();
                savePhotoListLocation();
                mImagePathAdapter.changerItem(currentFilePath);
                camIndex = 1;
                break;
        }
    }

    private void showDiag(final int position) {
        choose = -1;
        AlertView mAlertView = new AlertView("操作菜单", null, "取消", null,
                new String[]{"查看原图", "删除照片"},
                QdActivity.this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                choose = position;
            }
        });
        mAlertView.setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                switch (choose) {
                    case 0:
                        Intent intent = new Intent();
                        intent.putExtra("filepath", mPhotoList.get(position));
                        intent.setClass(QdActivity.this, ShowPhotoActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        new AlertView("请再次确认", "确认删除这张照片 ?", "取消", new String[]{"确定"}, null, QdActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    deleteFile(position);
                                }
                            }
                        }).show();
                        break;
                }

            }
        });
        mAlertView.show();

    }

    private void deleteFile(int position) {
        //删除本地文件
        String filepath = mPhotoList.get(position);
        File file = new File(filepath);
        AbFileUtil.deleteFile(file);
        //移除
        camIndex = camIndex - 1;
        mPhotoList.clear();
        mPhotoList.add(cameraPath);
        mImagePathAdapter.notifyDataSetChanged();
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//坐标系可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(2000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，
        // 默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private boolean checkProject() {
        if (mPhotoList.size() < 1 || AbStrUtil.isEquals(mPhotoList.get(0),cameraPath)) {
            MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_CONFIRM, "请拍摄照片");
            return false;
        }
        return true;
    }

    private void savePhotoListLocation() {
        String latitude = String.valueOf(application.mLocation.getLatitude());
        String lontitude = String.valueOf(application.mLocation.getLontitude());
        String address = application.mLocation.getAddress();
        String locationdescribe = application.mLocation.getLocationdescribe();
        mQd = new Qd();
        mQd.setLatitude(latitude);
        mQd.setLontitude(lontitude);
        mQd.setAddress(address);
        mQd.setLocationdescribe(locationdescribe);
        mQd.setDwmc(application.getUser().getMc());
        mQd.setUsername(application.getUser().getUsername());

    }


    private void commit() {
        savePhotoListLocation();
        String gson = AbJsonUtil.toJson(mQd);
        AbSoapParams params = new AbSoapParams();
        params.put("action", "add");
        params.put("gson", gson);
        AbLogUtil.d("xxxx",gson);
        WebServiceUntils.call(QdActivity.this, Constant.Modify_QD, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean && AbStrUtil.isEquals(result, "1")) {
                    MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_INFO, "登记启动成功!");
                    isCommit = true;
                    application.setIsAddProject(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    MsgUtil.sendMsgTop(QdActivity.this, Constant.MSG_ALERT, "上传失败,请重试");
                    addBtn.setEnabled(true);
                    //下载完成取消进度框
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                }
            }
        });

    }

    @Override
    protected void onResume() {
        mLocationClient.start();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
    }


    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            new AlertView("请确认", "GPS未打开,请打开GPS开关!","取消", new String[]{"确定"}, null, QdActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    }
                }
            }).show();
        }
    }


}
