package com.gongdian.qmcb.activity.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.image.AbImageLoader;
import com.ab.model.AbResult;
import com.ab.soap.AbSoapParams;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbFileUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.activity.photo.AddPhotoActivity;
import com.gongdian.qmcb.activity.photo.CropImageActivity;
import com.gongdian.qmcb.adapter.UserChooseAdapter;
import com.gongdian.qmcb.model.Menu;
import com.gongdian.qmcb.model.MenuListResult;
import com.gongdian.qmcb.model.QmcbUsers;
import com.gongdian.qmcb.model.Users;
import com.gongdian.qmcb.model.UsersListResult;
import com.gongdian.qmcb.others.OverScrollView;
import com.gongdian.qmcb.utils.Constant;
import com.gongdian.qmcb.utils.MsgUtil;
import com.gongdian.qmcb.utils.MyApplication;
import com.gongdian.qmcb.utils.WebServiceUntils;
import com.gongdian.qmcb.utils.WebServiceUntils2;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MineActivity extends AbActivity {

    private MyApplication application;
    private QmcbUsers user;
    private List<Menu> mList = null;
    private List<Menu> tempList = null;
    @AbIocView(id = R.id.uname)
    TextView mText_uname;
    @AbIocView(id = R.id.txt_name)
    TextView mText_txt_name;
    @AbIocView(id = R.id.pname)
    TextView mText_pname;
    @AbIocView(id = R.id.uids)
    TextView mText_uids;
    @AbIocView(id = R.id.pcode)
    TextView mText_pcode;
    @AbIocView(id = R.id.menu)
    TextView mText_menu;
    @AbIocView(id = R.id.head)
    ImageView img_head;
    @AbIocView(id = R.id.debug)
    Button btn_debug;
    @AbIocView(id = R.id.scrollView)
    OverScrollView scrollView;
    @AbIocView(id = R.id.exit)
    Button btn_exit;
    private View mAvatarView = null;
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
    private String mFileName;
    private AbHttpUtil mAbHttpUtil = null;
    private AbImageLoader mAbImageLoader = null;
    private View mView = null;
    private UserChooseAdapter mUserChooseAdapter = null;
    private List<Users> mList_ry = new ArrayList<>();
    private int choose = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_mine);
        application = (MyApplication) abApplication;
        mAbHttpUtil = AbHttpUtil.getInstance(this);
        mAbImageLoader = new AbImageLoader(this);
        mList = new ArrayList<>();
        tempList = new ArrayList<>();

        AbTitleBar mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("我");
        mAbTitleBar.setLogo(R.drawable.title_back_n);
        mAbTitleBar.setTitleBarBackground(R.color.colorPrimaryDark);
        mAbTitleBar.setTitleTextMargin(20, 0, 0, 0);
        mAbTitleBar.setLogoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            }
        });
        img_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiag();
            }
        });
        //初始化图片保存路径
        String photo_dir = AbFileUtil.getImageDownloadDir(this);
        if (AbStrUtil.isEmpty(photo_dir)) {
            AbToastUtil.showToast(MineActivity.this, "存储卡不存在");
        } else {
            PHOTO_DIR = new File(photo_dir);
        }
        if (Constant.DEBUG) {
            btn_debug.setVisibility(View.VISIBLE);
        } else {
            btn_debug.setVisibility(View.GONE);
        }
        btn_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init_ry();
            }
        });

        tempList = application.getMenuList();
        initdata();

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outLogin();
            }
        });

        scrollViewDown();
    }


    private void initdata() {
        user = application.getUser();
        if (!AbStrUtil.isEmpty(user.getHeadurl())) {
            mAbImageLoader.display(img_head, user.getHeadurl());
        } else {
            img_head.setImageResource(R.drawable.ic_sex_male);
        }

        mText_uname.setText(user.getFzr());
        mText_txt_name.setText(user.getFzr());
        mText_pname.setText(user.getMc());
        mText_uids.setText("");
        mText_pcode.setText(user.getUsername());

        for (int i = 0; i < tempList.size(); i++) {
            mList.add(tempList.get(i));
        }

        String txt_menu = "";
        if (AbStrUtil.isEquals(String.valueOf(user.getRole()), "0")) {
            txt_menu = "系统管理员" + "\n";
        }

        for (int i = 0; i < mList.size(); i++) {
            if (i < mList.size() - 1) {
                txt_menu = txt_menu + mList.get(i).getMenu() + "\n";
            } else {
                txt_menu = txt_menu + mList.get(i).getMenu();
            }
        }
        mText_menu.setText(txt_menu);
    }

    private void showDiag() {
        AlertView mAlertView = new AlertView("请选择", null, "取消", null,
                new String[]{"拍摄照片", "相册选择"},
                MineActivity.this, AlertView.Style.ActionSheet, new OnItemClickListener() {
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
                        doPickPhotoAction();
                        break;
                    case 1:
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                        break;
                }

            }
        });
        mAlertView.show();
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
            AbToastUtil.showToast(MineActivity.this, "没有可用的存储卡");
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
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (Exception e) {
            AbToastUtil.showToast(MineActivity.this, "未找到系统相机程序");
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    public String getPath(Uri uri) {

        String path = null;
        Bitmap bm = null;
        ContentResolver resolver = getContentResolver();

        try {
            bm = MediaStore.Images.Media.getBitmap(resolver, uri); //显得到bitmap图片
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFileName = System.currentTimeMillis() + ".jpg";
        mCurrentPhotoFile = new File(PHOTO_DIR, mFileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mCurrentPhotoFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bm.compress(Bitmap.CompressFormat.JPEG, Constant.imageCompress, fos);

        return mCurrentPhotoFile.getPath();
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
            case PHOTO_PICKED_WITH_DATA:
                Uri uri = mIntent.getData();
                String currentFilePath = getPath(uri);
                if (!AbStrUtil.isEmpty(currentFilePath)) {
                    Intent intent1 = new Intent(this, CropImageActivity.class);
                    intent1.putExtra("PATH", currentFilePath);
                    startActivityForResult(intent1, CAMERA_CROP_DATA);
                } else {
                    AbToastUtil.showToast(MineActivity.this, "未在存储卡中找到这个文件");
                }
                break;
            case CAMERA_WITH_DATA:
                AbLogUtil.e(AddPhotoActivity.class, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
                String currentFilePath2 = mCurrentPhotoFile.getPath();
                Intent intent2 = new Intent(this, CropImageActivity.class);
                intent2.putExtra("PATH", currentFilePath2);
                startActivityForResult(intent2, CAMERA_CROP_DATA);
                break;
            case CAMERA_CROP_DATA:
                String path = mIntent.getStringExtra("PATH");
                AbLogUtil.e(AddPhotoActivity.class, "裁剪后得到的图片的路径是 = " + path);
                uploadFile(path);
                break;
        }
    }

    public void uploadFile(final String path) {
        final String url = Constant.UPLOADURL;
        AbRequestParams params = new AbRequestParams();
        try {
            params.put("data1", URLEncoder.encode("welcome", HTTP.UTF_8));
            params.put("data2", "100");
            File file = new File(path);
            params.put(file.getName(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAbHttpUtil.post(url, params, new AbStringHttpResponseListener() {
            @Override
            public void onSuccess(int statusCode, String content) {
                AbResult abResult = new AbResult(content);
                if (abResult.getResultCode() > 0) {
                    String urlPath = Constant.UPLOADURLHEAD + AbStrUtil.resultToUrl(abResult.getResultMessage());
                    AbLogUtil.d("xxxx", urlPath);
                    modifyUser(urlPath);
                    /**上传成功*/
                    AbFileUtil.deleteFile(new File(path));
                }
            }

            // 开始执行前
            @Override
            public void onStart() {
            }

            @Override
            public void onFailure(int statusCode, String content,
                                  Throwable error) {
                MsgUtil.sendMsgTop(MineActivity.this, Constant.MSG_ALERT, "上传文件出错,请联系系统管理员!" + error.getMessage());
            }

            // 进度
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
            }

            // 完成后调用，失败，成功，都要调用
            public void onFinish() {
            }
        });
    }

    //上传修改user表
    private void modifyUser(final String url) {
        AbSoapParams abSoapParams = new AbSoapParams();
        abSoapParams.put("username", user.getUsername());
        abSoapParams.put("url", url);
        WebServiceUntils.call(MineActivity.this, Constant.Modify_Users_Head, abSoapParams, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean && AbStrUtil.isEquals(result, "1")) {
                    application.getUser().setHeadurl(url);
                    mAbImageLoader.display(img_head, url);
                    application.setUserChanged1(true);
                    application.setUserChanged2(true);
                    MsgUtil.sendMsgTop(MineActivity.this, Constant.MSG_INFO, "上传头像成功");
                }
            }
        });
    }

    /**
     * Scroll 拉到最底下
     */
    private void scrollViewDown() {
//        new android.os.Handler().post(new Runnable() {
//            @Override
//            public void run() {
//               scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
    }

    private void init_ry() {
        AbSoapParams params = new AbSoapParams();
        WebServiceUntils2 webServiceUntils2 = WebServiceUntils2.newInstance(MineActivity.this, Constant.GetUsers, params);
        webServiceUntils2.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result1) {
                mList_ry.clear();
                if (aBoolean) {
                    AbResult result = new AbResult(result1);
                    if (result.getResultCode() > 0) {
                        //成功
                        UsersListResult UsersListResult = AbJsonUtil.fromJson(result1, UsersListResult.class);
                        List<Users> UserssList = UsersListResult.getItems();
                        if (UserssList != null && UserssList.size() > 0) {
                            mList_ry.addAll(UserssList);
                            UserssList.clear();
                            userDialog();
                        }
                    }
                }

            }
        });
    }

    private void userDialog() {
        AbDialogUtil.removeDialog(MineActivity.this);
        mView = mInflater.inflate(R.layout.dialog_choose, null);
        ListView listView = (ListView) mView.findViewById(R.id.list);
        mUserChooseAdapter = new UserChooseAdapter(MineActivity.this, mList_ry);
        listView.setAdapter(mUserChooseAdapter);
        Button leftBtn = (Button) mView.findViewById(R.id.left_btn);
        Button rightBtn = (Button) mView.findViewById(R.id.right_btn);
        AbDialogUtil.showDialog(mView, R.animator.fragment_top_enter, R.animator.fragment_top_exit, R.animator.fragment_pop_top_enter, R.animator.fragment_pop_top_exit);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(MineActivity.this);
            }

        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbDialogUtil.removeDialog(MineActivity.this);
                for (int i = 0; i < mList_ry.size(); i++) {
                    if (mList_ry.get(i).isChoose()) {
//                        application.setUser(mList_ry.get(i));
//                        user = mList_ry.get(i);
//                        initMenu(mList_ry.get(i).getUids());
//                        /**切换用户*/
//                        application.setUserChanged1(true);
//                        application.setUserChanged2(true);
//                        application.setUserChanged3(true);
//                        return;
                    }
                }
            }
        });
    }


    @Override
    protected void onPause() {
        PgyFeedbackShakeManager.unregister();
        super.onPause();
    }

    @Override
    protected void onResume() {
        PgyFeedbackShakeManager.register(MineActivity.this, false);
        super.onResume();
    }

    private void initUser() {
        AbSoapParams params = new AbSoapParams();
        String imei = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("imei", imei);
        WebServiceUntils.call(MineActivity.this, Constant.CheckIMEI, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (result.equals("0") || !aBoolean) {
                    QmcbUsers users = new QmcbUsers();
                    application.setUser(users);
                    application.setIsLogin(false);
                } else {
//                    Users users = UsersPrase.parser(result).get(0);
//                    application.setUsers(users);
//                    application.setIsLogin(true);
//                    initMenu(users.getUids());
                }
            }
        });
    }

    private void initMenu(String uids) {
        AbSoapParams params = new AbSoapParams();
        params.put("role", String.valueOf(application.getUser().getRole()));
        WebServiceUntils2 webServiceUntils = WebServiceUntils2.newInstance(MineActivity.this, Constant.GetMenu, params);
        webServiceUntils.start(new WebServiceUntils2.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String rtn) {
                if (aBoolean) {
                    tempList.clear();
                    mList.clear();
                    AbResult result = new AbResult(rtn);
                    if (result.getResultCode() > 0) {
                        //成功
                        MenuListResult menuListResult = AbJsonUtil.fromJson(rtn, MenuListResult.class);
                        List<Menu> menuList = menuListResult.getItems();
                        if (menuList != null && menuList.size() > 0) {
                            application.setMenuList(menuList);
                            tempList.addAll(menuList);
                            menuList.clear();
                            initdata();
                        }
                    }

                }
            }
        });

    }

    private void outLogin() {
        //删除本地库
        String path = AbFileUtil.getDbDownloadDir(MineActivity.this) + "/weian.db";
        AbLogUtil.d("xxx", path);
        File file = new File(path);
        AbFileUtil.deleteFile(file);

        AbSoapParams params = new AbSoapParams();
//        params.put("user_id", application.getUsers().getId());
        WebServiceUntils.call(MineActivity.this, Constant.Login_out, params, 10000, false, "", new WebServiceUntils.webServiceCallBack() {
            @Override
            public void callback(Boolean aBoolean, String result) {
                if (aBoolean) {
                    android.os.Process.killProcess(android.os.Process.myPid()); //获取PID
                    System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                }
            }
        });

    }


}


