package com.gongdian.qmcb.activity.project;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.db.storage.AbSqliteStorage;
import com.ab.db.storage.AbSqliteStorageListener;
import com.ab.db.storage.AbStorageQuery;
import com.ab.fragment.AbLoadDialogFragment;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbAppUtil;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.progress.AbHorizontalProgressBar;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.photo.ShowPhotoActivity;
import com.gongdian.qmcb.adapter.ImageShowAdapter;
import com.gongdian.qmcb.dao.ProjectDao;
import com.gongdian.qmcb.model.Project;
import com.gongdian.qmcb.model.Project_jd;
import com.gongdian.qmcb.model.Project_photo;
import com.gongdian.qmcb.others.GrapeGridview;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.PictureUtil;
import com.gongdian.qmcb.utils.WebServiceUntils;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AddProjectPhotoActivity extends AbActivity {
    @AbIocView(id = R.id.nr)
    TextView mView_nr;
    @AbIocView(id = R.id.project_info)
    TextView mView_project_info;
    @AbIocView(id = R.id.task)
    TextView mView_task;
    @AbIocView(id = R.id.qk)
    TextView mView_qk;
    @AbIocView(id = R.id.dz)
    EditText mView_dz;
    @AbIocView(id = R.id.kcry)
    TextView mView_kcry;
    private MyApplication application;
    private GrapeGridview mGridView = null;
    private ImageShowAdapter mImagePathAdapter = null;
    private ArrayList<String> mPhotoList = null;
    private int selectIndex = 0;
    private int camIndex = 0;
    private View mAvatarView = null;
    private int startUpload = 0;
    private int uploadCount = 0;
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
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
    private String flag = null;
    private Button addBtn = null;

    private Project mProject;
    private Project_jd mProject_jd;
    private List<Project_jd> mProject_jd_list;
    private Project_photo mProject_photo;
    private List<Project_photo> mProject_photo_list;

    //数据库操作类
    private AbSqliteStorage mAbSqliteStorage = null;
    //定义数据库操作实现类
    private ProjectDao mProjectDao = null;
    private boolean isCommit = false;
    private boolean isFinish = false;
    private String cameraPath = String.valueOf(R.drawable.chat_tool_camera);
    private String menu_id = null;
    private String pro_id = null;
    private String pid = null;
    private Project project_temp;
    private Boolean isNewUpload = false;

    private int choose = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_exploration);
        application = (MyApplication) abApplication;
        mLocationClient = application.mLocationClient;
        LocationResult = (TextView) findViewById(R.id.textView1);
        LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        application.mLocationResult = LocationResult;
        initLocation();
        mLocationClient.start();

        Intent intent = this.getIntent();
        project_temp = (Project) intent.getSerializableExtra("project");
        pro_id = project_temp.getId();
        pid = project_temp.getPid();
        menu_id = intent.getStringExtra("menu_id");
        String menu = intent.getStringExtra("menu");
        addBtn = (Button) findViewById(R.id.addBtn);
        switch (menu) {
            case Constant.MENU3: //计划
                flag = Constant.FLAG1;
                break;
            case Constant.MENU4: //勘查
                flag = Constant.FLAG2;
                addBtn.setText("勘查完成");
                mView_task.setText("勘查");
                break;
            case Constant.MENU5: //开工
                flag = Constant.FLAG3;
                addBtn.setText("工作许可");
                mView_task.setText("开工");
                break;
            case Constant.MENU6: //到岗
                flag = Constant.FLAG5;
                addBtn.setText("到岗确认");
                mView_task.setText("到岗");
                break;
            case Constant.MENU7: //监督
                flag = Constant.FLAG6;
                addBtn.setText("上传督察结果");
                mView_task.setText("督察");
                mView_qk.setText("督察情况");
                break;
            case Constant.MENU8: //完工
                flag = Constant.FLAG4;
                addBtn.setText("工作终结");
                mView_task.setText("完工");
                break;
        }
        String info = project_temp.getId() + " " + project_temp.getMc() + "\n" + "施工部门: " + project_temp.getDw() + "\n" + "施工地址: " + project_temp.getDz();
        mView_project_info.setText(info);

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText(menu);
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
        mAbSqliteStorage = AbSqliteStorage.getInstance(this);
        //初始化数据库操作实现类
        mProjectDao = new ProjectDao(this);
        mProject = new Project();
        mPhotoList = new ArrayList<>();
        mProject_photo = new Project_photo();
        mProject_photo_list = new ArrayList<>();
        mProject_jd = new Project_jd();
        mProject_jd_list = new ArrayList<>();

        //查询本地
        queryDataById(pro_id, flag);

        //默认
        mGridView = (GrapeGridview) findViewById(R.id.myGrid);
        DisplayMetrics displayMetrics = AbAppUtil.getDisplayMetrics(this); //获取屏幕长宽
        int w = displayMetrics.widthPixels; //720 1440
        int h = displayMetrics.heightPixels; //1280 2392
        mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, (w - 100) / 3, (w - 100) / 3);
        mGridView.setAdapter(mImagePathAdapter);

//        mView_kcry.setText(application.getUsers().getUname());

        //初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, "存储卡不存在");
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
                if (selectIndex == camIndex) {
                    if (application.mLocation.isSucess() && !AbStrUtil.isEmpty(application.mLocation.getAddress())) {
                        doPickPhotoAction();
                    } else {
                        MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_CONFIRM, "未取得定位信息,请稍后再试...");
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
                if (!checkProject()) {
                    addBtn.setEnabled(true);
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                    return;
                }
                final AbLoadDialogFragment mDialogFragment = AbDialogUtil.showLoadDialog(AddProjectPhotoActivity.this, R.drawable.list_load, "请稍候...", AbDialogUtil.ThemeLightPanel);
                startUpload = 0;
                uploadCount = 0;
                beforeUpLoade();
            }
        });

//        mView_nr.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                chooseNr();
//            }
//        });
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.r1);
        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNr();
            }
        });

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
            MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, "没有可用的存储卡");
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
            MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, "未找到系统相机程序");
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
                savePhotoListLocation(currentFilePath);
                mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, 6, currentFilePath);
                camIndex++;
                break;
        }
    }

    private void showDiag(final int position) {
        choose = -1;
        AlertView mAlertView = new AlertView("操作菜单", null, "取消", null,
                new String[]{"查看原图", "删除照片"},
                AddProjectPhotoActivity.this, AlertView.Style.ActionSheet, new com.bigkoo.alertview.OnItemClickListener() {
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
                        intent.setClass(AddProjectPhotoActivity.this, ShowPhotoActivity.class);
                        startActivity(intent);
                        break;
                    case 1://分配人员权限
                        new AlertView("请再次确认", "确认删除这张照片 ?", "取消", new String[]{"确定"}, null, AddProjectPhotoActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
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
        mPhotoList.remove(position);
        mProject_photo_list.remove(position);
        mImagePathAdapter.notifyDataSetChanged();
    }

    private void initLocation() {
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

    private boolean checkProject() {
        saveProject();
        if (mPhotoList.size() < 2) {
            MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_CONFIRM, "请拍摄照片");
            return false;
        }
        if (AbStrUtil.isEmpty(mProject_jd.getNr())) {
            MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_CONFIRM, "请输入现场情况描述");
            return false;
        }
        return true;
    }

    private void savePhotoListLocation(String path) {
        String latitude = String.valueOf(application.mLocation.getLatitude());
        String lontitude = String.valueOf(application.mLocation.getLontitude());
        String address = application.mLocation.getAddress();
        String locationdescribe = application.mLocation.getLocationdescribe();
        Project_photo project_photo = new Project_photo();
        project_photo.setLatitude(latitude);
        project_photo.setLontitude(lontitude);
        project_photo.setAddress(address);
        project_photo.setLocationdescribe(locationdescribe);
        project_photo.setPro_id(pro_id);
        project_photo.setFlag(flag);
//        project_photo.setCreateuser(application.getUsers().getId());
        project_photo.setUrl(path);
        mProject_photo_list.add(project_photo);
    }

    private void saveProject() {
        String dz = mView_dz.getText().toString().trim();
//        String user_id = application.getUsers().getId();

        mProject.setId(pro_id);
        mProject.setFlag(flag);
        mProject_jd.setPro_id(pro_id);
        mProject_jd.setMenu_id(menu_id);
//        mProject_jd.setUser_id(user_id);
        mProject_jd.setPid(pid);
        if (mProject_jd_list == null) {
            mProject_jd_list = new ArrayList<>();
        }
        mProject_jd_list.clear();
        mProject_jd_list.add(mProject_jd);

        mProject.setProject_jd(mProject_jd_list);
        mProject.setProject_photos(mProject_photo_list);
        delData(pro_id);
        saveData(mProject);
    }

    private void beforeUpLoade() {
        int k = 0;
        for (int i = startUpload; i < mPhotoList.size() - 1; i++) {
            String path = mPhotoList.get(i);
            if (path.indexOf("http://") == -1) {
                uploadFile(path);
                k++;
                return; //只循环一次
            } else {
                uploadCount++;
            }
        }
        if (k == 0 && !isNewUpload) {
            saveProject();
            commit();
        }

    }

    public void saveData(Project mProject) {
        mAbSqliteStorage.insertData(mProject, mProjectDao, new AbSqliteStorageListener.AbDataInsertListener() {
            @Override
            public void onSuccess(long id) {
                if (isFinish) {
                    mAbSqliteStorage.release();
                }
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, errorMessage);
                if (isFinish) {
                    mAbSqliteStorage.release();
                }
            }
        });
    }

    public void delData(String pro_id) {
        AbStorageQuery mAbStorageQuery = new AbStorageQuery();
        mAbStorageQuery.equals("pro_id", pro_id);
        mAbSqliteStorage.deleteData(mAbStorageQuery, mProjectDao, new AbSqliteStorageListener.AbDataDeleteListener() {
            @Override
            public void onSuccess(int rows) {
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, errorMessage);
            }
        });
    }

    public void updateData(Project mProject) {
        mAbSqliteStorage.updateData(mProject, mProjectDao, new AbSqliteStorageListener.AbDataUpdateListener() {
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, errorMessage);
            }

            @Override
            public void onSuccess(int rows) {
            }
        });
    }

    public void queryDataById(String pro_id, String flag) {
        AbStorageQuery mAbStorageQuery = new AbStorageQuery();
        mAbStorageQuery.equals("pro_id", String.valueOf(pro_id));
        mAbStorageQuery.equals("flag", String.valueOf(flag));
        mAbSqliteStorage.findData(mAbStorageQuery, mProjectDao, new AbSqliteStorageListener.AbDataSelectListener() {
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, errorMessage);
            }

            @Override
            public void onSuccess(List<?> paramList) {
                // TODO: 2/16/16
                if (paramList != null && paramList.size() > 0) {
                    mProject = (Project) paramList.get(0);
                } else {
//                    mProject = project_temp;
                    mProject = new Project();
                }

                if (mProject.getProject_jd() != null && mProject.getProject_jd().size() > 0) {
                    mProject_jd_list = mProject.getProject_jd();
                    mProject_jd = mProject_jd_list.get(0);
                    mView_nr.setText(mProject_jd.getNr());
                }

                if (mProject.getProject_photos() != null && mProject.getProject_photos().size() > 0) {
                    mProject_photo_list = mProject.getProject_photos();
                    //反写
                    for (int i = 0; i < mProject_photo_list.size(); i++) {
                        // TODO: 1/23/16
                        mPhotoList.add(mProject_photo_list.get(i).getUrl());
                    }
                }
                if (!mPhotoList.contains(cameraPath)) {
                    mPhotoList.add(mPhotoList.size(), cameraPath);
                    camIndex = mPhotoList.size() - 1;
                }
            }
        });

    }

    public void uploadFile(final String path) {
        final String url = Constant.UPLOADURL;
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
                        AbLogUtil.d("xxxxx", String.valueOf(i) + "  " + urlPath);
                        mPhotoList.set(i, urlPath);
                        mProject_photo_list.get(i).setUrl(urlPath);
                        ImageShowAdapter.ViewHolder mViewHolder = (ImageShowAdapter.ViewHolder) mGridView.getChildAt(i).getTag();
                        mViewHolder.mImageView2.setBackgroundResource(R.drawable.photo_selected);
                        AbFileUtil.deleteFile(new File(path));
                        startUpload++;
                        uploadCount++;
                    }
                }
            }

            // 开始执行前
            @Override
            public void onStart() {

            }

            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {
                //下载完成取消进度框
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                    mAlertDialog = null;
                }
                MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, error.getMessage());
            }

            // 进度
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
            }

            // 完成后调用，失败，成功，都要调用
            public void onFinish() {
                //判断还有没有后续,传后面的
                isNewUpload = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        beforeUpLoade();
                    }
                }, 1000);
                //上传图片 完毕 上传工程
                if (uploadCount == mPhotoList.size() - 1) {
                    AbDialogUtil.removeDialog(AddProjectPhotoActivity.this);
                    if (mAlertDialog != null) {
                        mAlertDialog.dismiss();
                        mAlertDialog = null;
                    }
                    saveProject();
                    commit();
                }
            }

        });
    }

    private void commit() {
        String gson = AbJsonUtil.toJson(mProject);
        AbSoapParams params = new AbSoapParams();
        params.put("action", "add");
        params.put("gson", gson);
        WebServiceUntils.call(AddProjectPhotoActivity.this, Constant.Modify_Project_jd, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean && AbStrUtil.isEquals(result, "1")) {
                    MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_INFO, "上传成功");
                    isCommit = true;
                    delData(pro_id);
                    application.setIsAddProject(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else {
                    MsgUtil.sendMsgTop(AddProjectPhotoActivity.this, Constant.MSG_ALERT, "上传失败,请重试");
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

    private void chooseNr() {
        View view = mInflater.inflate(R.layout.project_qk, null);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        final RadioButton radio1 = (RadioButton) view.findViewById(R.id.radio1);
        final RadioButton radio2 = (RadioButton) view.findViewById(R.id.radio2);
        final TextView dw = (TextView) view.findViewById(R.id.dw);
        final TextView ry = (TextView) view.findViewById(R.id.ry);
        final LinearLayout lay1 = (LinearLayout) view.findViewById(R.id.lay1);
        final LinearLayout lay2 = (LinearLayout) view.findViewById(R.id.lay2);
        final EditText ed1 = (EditText) view.findViewById(R.id.nr);
        final EditText dwf = (EditText) view.findViewById(R.id.dwf);
        final EditText dwq = (EditText) view.findViewById(R.id.dwq);
        final EditText ryf = (EditText) view.findViewById(R.id.ryf);
        final EditText ryq = (EditText) view.findViewById(R.id.ryq);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radio1.isChecked()) {
                    lay1.setVisibility(View.INVISIBLE);
                    lay2.setVisibility(View.INVISIBLE);
                } else {
                    lay1.setVisibility(View.VISIBLE);
                    if (menu_id.equals("205")) {
                        dw.setText("处罚施工单位" + project_temp.getDw() + ":");
                        ry.setText("处罚负责人" + project_temp.getFzrxm() + ":");
                        radio2.setText("违规");
                        lay2.setVisibility(View.VISIBLE);
                    } else {
                        lay2.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        //初始化
        if (AbStrUtil.isEquals(mProject_jd.getLb(), "2")) {
            radio1.setChecked(false);
            radio2.setChecked(true);
            String h ;
            int s = mProject_jd.getNr().indexOf("(");
            if (s!=-1) {
                h = mProject_jd.getNr().substring(0,s);
            }else {
                h = mProject_jd.getNr();
            }
            ed1.setText(h);
            if (menu_id.equals("205")) {
                radio2.setText("违规");
                dw.setText("处罚施工单位" + project_temp.getDw() + ":");
                ry.setText("处罚负责人" + project_temp.getFzrxm() + ":");
                dwf.setText(mProject_jd.getDwf());
                dwq.setText(mProject_jd.getDwq());
                ryf.setText(mProject_jd.getRyf());
                ryq.setText(mProject_jd.getRyq());
                lay2.setVisibility(View.VISIBLE);
            } else {
                lay2.setVisibility(View.INVISIBLE);
            }
        } else {
            radio1.setChecked(true);
            radio2.setChecked(false);
            lay2.setVisibility(View.INVISIBLE);
            lay1.setVisibility(View.INVISIBLE);
        }


        AlertView mAlertViewExt = new AlertView("请完善信息", null, "取消", null, new String[]{"完成"}, AddProjectPhotoActivity.this, AlertView.Style.Alert, new com.bigkoo.alertview.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                //判断是否是拓展窗口View，而且点击的是非取消按钮
                if (position != AlertView.CANCELPOSITION) {
                    String s_nr = ed1.getText().toString();
                    String s_dwf = dwf.getText().toString();
                    String s_dwq = dwq.getText().toString();
                    String s_ryf = ryf.getText().toString();
                    String s_ryq = ryq.getText().toString();
                    if (AbStrUtil.isEmpty(s_dwf)) {
                        s_dwf = "0";
                    }
                    if (AbStrUtil.isEmpty(s_dwq)) {
                        s_dwq = "0";
                    }
                    if (AbStrUtil.isEmpty(s_ryf)) {
                        s_ryf = "0";
                    }
                    if (AbStrUtil.isEmpty(s_ryq)) {
                        s_ryq = "0";
                    }
                    String t = "";
                    int show1 = 0, show2 = 0;
                    String f1 = "", f2 = "", q1 = "", q2 = "";
                    if (Integer.parseInt(s_dwf) > 0) {
                        show1++;
                        f1 = s_dwf + "分";
                    }
                    if (Integer.parseInt(s_dwq) > 0) {
                        show1++;
                        q1 = s_dwq + "元";
                    }
                    if (Integer.parseInt(s_ryf) > 0) {
                        show2++;
                        f2 = s_ryf + "分";
                    }
                    if (Integer.parseInt(s_ryq) > 0) {
                        show2++;
                        q2 = s_ryq + "元";
                    }
                    if (show1 > 0) {
                        t = t + "处罚施工单位" + project_temp.getDw() + f1 + q1;
                    }
                    if (show2 > 0) {
                        if (show1 > 0) {
                            t = t + "，处罚负责人" + project_temp.getFzrxm() + f2 + q2;
                        } else {
                            t = t + "处罚负责人" + project_temp.getFzrxm() + f2 + q2;
                        }
                    }

                    mProject_jd.setDwf(s_dwf);
                    mProject_jd.setDwq(s_dwq);
                    mProject_jd.setRyf(s_ryf);
                    mProject_jd.setRyq(s_ryq);
                    if (radio1.isChecked()) {
                        mProject_jd.setLb("1");
                        s_nr = "正常";
                    } else {
                        mProject_jd.setLb("2");
                        if(show1>0||show2>0) {
                            s_nr = s_nr + "(" + t + ")";
                        }
                    }
                    mProject_jd.setNr(s_nr);
                    mView_nr.setText(s_nr);
                    return;
                }
            }
        });
        mAlertViewExt.addExtView(view);
        mAlertViewExt.show();
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
        isFinish = true;
        //必须要释放
        if (!isCommit) {
            saveProject();
        }

        super.finish();
    }


}
