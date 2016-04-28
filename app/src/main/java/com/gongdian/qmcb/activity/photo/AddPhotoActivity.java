package com.gongdian.qmcb.activity.photo;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbResult;
import com.ab.util.AbAppUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.progress.AbHorizontalProgressBar;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.adapter.ImageShowAdapter;
import com.gongdian.qmcb.others.GrapeGridview;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.PictureUtil;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AddPhotoActivity extends AbActivity {
    private MyApplication application;
    private GrapeGridview mGridView = null;
    private ImageShowAdapter mImagePathAdapter = null;
    private ArrayList<String> mPhotoList = null;
    private int selectIndex = 0;
    private int camIndex = 0;
    private View mAvatarView = null;
    private int startUpload = 0;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    /* 拍照的照片存储位置 */
    private File PHOTO_DIR = null;
    // 照相机拍照得到的图片
    private File mCurrentPhotoFile;
    private File mCurrentSmallFile;
    private String mFileName;
    /* ProgressBar进度控制 */
    private AbHorizontalProgressBar mAbProgressBar;
    /* 最大100 */
    private int max = 100;
    private int progress = 0;
    private TextView numberText, maxText;
    private DialogFragment mAlertDialog = null;
    private AbHttpUtil mAbHttpUtil = null;
    private LocationClient mLocationClient;
    private TextView LocationResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.add_photo);
        application = (MyApplication)abApplication;
        mLocationClient = application.mLocationClient;
        LocationResult = (TextView)findViewById(R.id.textView1);
        LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        application.mLocationResult = LocationResult;
        initLocation();
        mLocationClient.start();

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(R.string.photo_add_name);
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });

        initTitleRightLayout();

        mPhotoList = new ArrayList<>();

        //获取Http工具类
        mAbHttpUtil = AbHttpUtil.getInstance(this);

        //默认
        mPhotoList.add(String.valueOf(R.drawable.chat_tool_camera));

        mGridView = (GrapeGridview) findViewById(R.id.myGrid);
        DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(this); //获取屏幕长宽
        int w = displayMetrics.widthPixels; //720 1440
        int h = displayMetrics.heightPixels; //1280 2392
        mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, w / 3, h / 4);
        mGridView.setAdapter(mImagePathAdapter);

        //初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(AddPhotoActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }


        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectIndex = position;
                //点击了拍照图片
                if (selectIndex == camIndex) {
                    doPickPhotoAction();
//					mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
//					Button albumButton = (Button)mAvatarView.findViewById(R.id.choose_album);
//					Button camButton = (Button)mAvatarView.findViewById(R.id.choose_cam);
//					Button cancelButton = (Button)mAvatarView.findViewById(R.id.choose_cancel);
//
//					albumButton.setOnClickListener(new OnClickListener(){
//
//						@Override
//						public void onClick(View v) {
//							AbDialogUtil.removeDialog(AddPhotoActivity.this);
//							// 从相册中去获取
//								Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//								intent.setType("image/*");
//								startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
//						}
//
//					});
//
//					camButton.setOnClickListener(new OnClickListener(){
//
//						@Override
//						public void onClick(View v) {
//							AbDialogUtil.removeDialog(AddPhotoActivity.this);
//							doPickPhotoAction();
//						}
//
//					});
//
//					cancelButton.setOnClickListener(new OnClickListener(){
//
//						@Override
//						public void onClick(View v) {
//							AbDialogUtil.removeDialog(AddPhotoActivity.this);
//						}
//
//					});
//
//					//显示选择按钮
//					AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);
                }
                //点击了已照图片
                else {
//					for(int i=0;i<mImagePathAdapter.getCount() - 1;i++){
//						ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder)mGridView.getChildAt(i).getTag();
//						if(mViewHolder!=null){
//							mViewHolder.mImageView2.setBackgroundDrawable(null);
//						}
//					}
//					ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder)view.getTag();
//					mViewHolder.mImageView2.setBackgroundResource(R.drawable.photo_select);
                    showDiag(position);
                }
            }

        });


        Button addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload = 0;
                beforeUpLoade();
            }
        });

    }

    private void beforeUpLoade() {
        for (int i = startUpload; i < mPhotoList.size() - 1; i++) {
            String path = mPhotoList.get(i);
            if (path.indexOf("http://") == -1) {
                uploadFile(path);
                return; //只循环一次
            }
        }
    }

    private void initTitleRightLayout() {

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
            AbToastUtil.showToast(AddPhotoActivity.this, "没有可用的存储卡");
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
            AbToastUtil.showToast(AddPhotoActivity.this, "未找到系统相机程序");
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
//			case PHOTO_PICKED_WITH_DATA:
//				Uri uri = mIntent.getData();
//				String currentFilePath = getPath(uri);
//				if(!AbStrUtil.isEmpty(currentFilePath)){
//					Intent intent1 = new Intent(this, CropImageActivity.class);
//					intent1.putExtra("PATH", currentFilePath);
//					startActivityForResult(intent1, CAMERA_CROP_DATA);
//		        }else{
//		        	AbToastUtil.showToast(AddPhotoActivity.this, "未在存储卡中找到这个文件");
//		        }
//				break;
            case CAMERA_WITH_DATA:
                try {
                    mCurrentSmallFile = new File(PHOTO_DIR, "small_" + mCurrentPhotoFile.getName());
                    Bitmap bm = PictureUtil.getSmallBitmap(mCurrentPhotoFile.getPath());
                    FileOutputStream fos = new FileOutputStream(mCurrentSmallFile);
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    AbFileUtil.deleteFile(mCurrentPhotoFile);
                } catch (Exception e) {
                }

                //AbLogUtil.d(AddPhotoActivity.class, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
                String currentFilePath = mCurrentSmallFile.getPath();
//				Intent intent2 = new Intent(this, CropImageActivity.class);
//				intent2.putExtra("PATH", currentFilePath);
//				startActivityForResult(intent2,CAMERA_CROP_DATA);
                mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, 2,currentFilePath);
                camIndex++;
                break;
//			case CAMERA_CROP_DATA:
//				String path = mIntent.getStringExtra("PATH");
//		    	AbLogUtil.d(AddPhotoActivity.class, "裁剪后得到的图片的路径是 = " + path);
//		    	mImagePathAdapter.addItem(mImagePathAdapter.getCount()-1,path);
//		     	camIndex++;
//				break;
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri) {
        if (AbStrUtil.isEmpty(uri.getAuthority())) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }


    public void uploadFile(final String path) {
        //已经在后台上传
        if (mAlertDialog != null) {
            mAlertDialog.show(getFragmentManager(), "dialog");
            return;
        }
        String url = Constant.UPLOADURL;

        AbRequestParams params = new AbRequestParams();

        try {
            //多文件上传添加多个即可
            params.put("data1", URLEncoder.encode("welcome", HTTP.UTF_8));
            params.put("data2", "100");
            //文件参数，去掉后边那个按钮
            File file = new File(path);
            params.put(file.getName(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAbHttpUtil.post(url, params, new AbStringHttpResponseListener() {

            @Override
            public void onSuccess(int statusCode, String content) {
                //上传成功后mphotoList 本地地址换成 URL 并删除本地文件 并打钩
                AbLogUtil.d("xxxxxx", content);
                AbResult abResult = new AbResult(content);
                if (abResult.getResultCode() > 0) {
                    String urlPath = Constant.UPLOADURLHEAD + AbStrUtil.resultToUrl(abResult.getResultMessage());
                    int i = mPhotoList.indexOf(path);
                    if (i != -1) {
                        ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) mGridView.getChildAt(i).getTag();
                        mViewHolder.mImageView2.setBackgroundResource(R.drawable.photo_select);
                        mPhotoList.set(i,urlPath);
                        AbFileUtil.deleteFile(new File(path));
                    }
                }
            }

            // 开始执行前
            @Override
            public void onStart() {
                //打开进度框
                View v = LayoutInflater.from(AddPhotoActivity.this).inflate(R.layout.progress_bar_horizontal, null, false);
                mAbProgressBar = (AbHorizontalProgressBar)v.findViewById(R.id.horizontalProgressBar);
                numberText = (TextView) v.findViewById(R.id.numberText);
                maxText = (TextView) v.findViewById(R.id.maxText);
                maxText.setText(progress + "/" + String.valueOf(max));
                mAbProgressBar.setMax(max);
                mAbProgressBar.setProgress(progress);
                mAlertDialog = AbDialogUtil.showAlertDialog("正在上传", v);
                startUpload++;
            }

            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {
                AbToastUtil.showToast(AddPhotoActivity.this, error.getMessage());
            }

            // 进度
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                maxText.setText(bytesWritten / (totalSize / max) + "/" + max);
                mAbProgressBar.setProgress((int) (bytesWritten / (totalSize / max)));
            }

            // 完成后调用，失败，成功，都要调用
            public void onFinish() {
                //下载完成取消进度框
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                //判断还有没有后续,传后面的
                beforeUpLoade();
            }

        });
    }

    private void showDiag(final int position) {
        mAvatarView = mInflater.inflate(R.layout.choose_avatar, null);
        Button albumButton = (Button) mAvatarView.findViewById(R.id.choose_album);
        Button camButton = (Button) mAvatarView.findViewById(R.id.choose_cam);
        Button cancelButton = (Button) mAvatarView.findViewById(R.id.choose_cancel);
        albumButton.setText("查看原图");
        camButton.setText("删除照片");

        albumButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddPhotoActivity.this);
                //预览
                Intent intent = new Intent();
                intent.putExtra("filepath", mPhotoList.get(position));
                intent.setClass(AddPhotoActivity.this, ShowPhotoActivity.class);
                startActivity(intent);
            }

        });

        camButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddPhotoActivity.this);
                deleteFile(position);
            }

        });

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(AddPhotoActivity.this);
            }

        });

        //显示选择按钮
        AbDialogUtil.showDialog(mAvatarView, Gravity.BOTTOM);


    }

    private void deleteFile(int position) {
        //删除本地文件
        String filepath = mPhotoList.get(position);
        File file = new File(filepath);
        AbFileUtil.deleteFile(file);
        //移除
        camIndex = camIndex - 1;
        mPhotoList.remove(position);
        mImagePathAdapter.notifyDataSetChanged();
    }


    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//坐标系可选，默认gcj02，设置返回的定位结果坐标系，
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onStop() {
        mLocationClient.stop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(AddPhotoActivity.this, false);
        super.onResume();
    }


}
