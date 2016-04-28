package com.gongdian.qmcb.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.util.AbBase64;
import com.ab.util.AbFileUtil;
import com.ab.view.ioc.AbIocView;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.permission.PermissionsDispatcher;
import com.gongdian.qmcb.permission.logger.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestActivity extends AbActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    @AbIocView(id = R.id.btn, click = "btnclick")
    Button button;
    @AbIocView(id = R.id.btn2, click = "btnclick2")
    Button button2;
    @AbIocView(id = R.id.imageView)
    ImageView mimageView;

    private static int CAMERA_REQUEST_CODE = 1;
    private static int GALLERY_REQUEST_CODE = 2;
    private static int CROP_REQUEST_CODE=3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test);
        requestImportantPermissions();
    }

    public void btnclick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    public void btnclick2(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    //mimageView.setImageBitmap(bitmap);
                    //Uri uri = saveBitmap(bitmap);
                    //startImageZoom(uri);

                    byte[] b = AbFileUtil.Bitmap2Bytes(bitmap,0);
                    String s = AbBase64.encode(b);
                    String uri = AbFileUtil.getFileDownloadDir(TestActivity.this);
                    Log.d("xxxxxx",uri);
                    AbFileUtil.writeBitmapToSD(uri + "/temp2.png",bitmap,true);
                    AbFileUtil.writeByteArrayToSD(uri + "/temp2.txt",s.getBytes(),true);

                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data!=null) {
                Uri uri = data.getData();
              mimageView.setImageURI(uri);
                startImageZoom(convert(uri));
//                mimageView.setImageURI(convert(uri));
            }
        }else if (requestCode == CROP_REQUEST_CODE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    mimageView.setImageBitmap(bitmap);

                }
            }
        }

    }

    //图像裁剪
    private void startImageZoom(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,CROP_REQUEST_CODE);

    }

    //Uri统一资源标示符
    //Uri的处理,将 content类型的URI 转换为 file 类型的
    //src 相对路径,content 统一资源标示符,file 绝对路径,URL 网络地址

    private Uri convert(Uri uri){
        InputStream stream = null;
        try {
            stream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    //将Bitmap存储到SD卡中
    private Uri saveBitmap(Bitmap bitmap){
        File tempDir = new File(Environment.getExternalStorageDirectory()+"/com.gongdian.weian");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File imgfile = new File(tempDir.getAbsolutePath()+"/temp.png");
        try {
            FileOutputStream fos = new FileOutputStream(imgfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fos);  //压缩
            fos.flush();
            fos.close();
            return Uri.fromFile(imgfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }



    private void requestImportantPermissions() {
        PermissionsDispatcher.requestPermissions(this, 1,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE});
    }






}
