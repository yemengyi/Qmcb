package com.gongdian.qmcb.activity.baidu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.ab.activity.AbActivity;
import com.ab.util.AbStrUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.gongdian.qmcb.R;
import com.gongdian.qmcb.model.Project_dw2;
import com.gongdian.qmcb.model.Project_jd2;
import com.gongdian.qmcb.model.Project_photo;
import com.gongdian.qmcb.utils.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaiduMapShowPhoto extends AbActivity {

    private static final String TAG = "BaiduMapShowPhoto";
    //    double mLat1 = 119.126559;
//    double mLon1 = 34.845686;
// 天安门坐标
    double mLat1 = 39.915291;
    double mLon1 = 116.403857;
    // 百度大厦坐标
    double mLat2 = 40.056858;
    double mLon2 = 116.308194;

    private MyApplication application;
    private AbTitleBar mAbTitleBar = null;
    MapView mMapView;
    BaiduMap mBaiduMap;
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    boolean isFirstLoc = true; // 是否首次定位
    private RadioButton r1, r2;
    private CheckBox checkTraffice, checkBaiduHeatMap;
    private ImageView location;
    private Marker[] mMarker = null;
    // 初始化全局 bitmap 信息，不用时及时 recycle
    List<Project_photo> mList = new ArrayList<>();

    Map<Marker, Project_photo> mLatLngMap = new HashMap<>();
    private InfoWindow mInfoWindow=null;
    private Project_dw2 project_dw2 = null;
    private Boolean isSp = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.baidu_fragment);
        application = (MyApplication) getApplication();
        mAbTitleBar = this.getTitleBar();
        mAbTitleBar.setTitleText("地图");
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

        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        location = (ImageView) findViewById(R.id.location);
        float level = mBaiduMap.getMaxZoomLevel();
        //   MapStatusUpdateFactory.zoomIn(level);
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(level - 8);
        mBaiduMap.animateMapStatus(u);


        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();


        r1 = (RadioButton) findViewById(R.id.normal);
        r2 = (RadioButton) findViewById(R.id.statellite);
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapMode(v);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMapMode(v);
            }
        });

        checkTraffice = (CheckBox) findViewById(R.id.traffice);
        checkTraffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTraffic(v);
            }
        });

        checkBaiduHeatMap = (CheckBox) findViewById(R.id.baiduHeatMap);
        checkBaiduHeatMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBaiduHeatMap(v);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //定位当前位置
                //设置为跟随模式
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, mCurrentMarker));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));
                    }
                }, 2000);
            }
        });

        Intent intent = getIntent();
        if (AbStrUtil.isEquals(intent.getStringExtra("sp"),"1")) {
            List<Project_jd2> mList = (List<Project_jd2>)intent.getSerializableExtra("project_jd");
            initData(mList);
        }else {
            project_dw2 = (Project_dw2) intent.getSerializableExtra("project_dw");
            if (project_dw2.getProject_jd() != null) {
                initData(project_dw2.getProject_jd());
            }
        }


    }

    /**
     * 设置底图显示模式
     *
     * @param view
     */
    public void setMapMode(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.normal:
                if (checked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.statellite:
                if (checked) {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置是否显示交通图
     *
     * @param view
     */
    public void setTraffic(View view) {
        mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
    }

    /**
     * 设置是否显示百度热力图
     *
     * @param view
     */
    public void setBaiduHeatMap(View view) {
        mBaiduMap.setBaiduHeatMapEnabled(((CheckBox) view).isChecked());
    }


    @Override
    public void onResume() {
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }
    }

    public void onReceivePoi(BDLocation poiLocation) {
    }

    private void initOverlay() {
//        mBaiduMap.hideInfoWindow();

        mMarker = new Marker[mList.size()];
        for (int i = 0; i < mList.size(); i++) {
            Double la = Double.parseDouble(mList.get(i).getLatitude());
            Double lo = Double.parseDouble(mList.get(i).getLontitude());
            //定义Maker坐标点
            LatLng point = new LatLng(la, lo);
            BitmapDescriptor bitmap;
            //构建Marker图标
            switch (mList.get(i).getFlag()){
                case "2":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_202);
                    break;
                case "3":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_203);
                    break;
                case "4":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_206);
                    break;
                case "5":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_204);
                    break;
                case "6":
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_205);
                    break;
                default:
                    bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
            }

            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            //在地图上添加Marker，并显示
            mMarker[i] = (Marker) (mBaiduMap.addOverlay(option));
            Project_photo photo = mList.get(i);
            mLatLngMap.put(mMarker[i], photo);

//            //添加Title
//            TextView button = new TextView(BaiduMapShowPhoto.this);
//            button.setBackgroundResource(R.drawable.btn_blue);
//            InfoWindow.OnInfoWindowClickListener listener = null;
//            listener = new InfoWindow.OnInfoWindowClickListener() {
//                public void onInfoWindowClick() {
//                }
//            };
//            button.setText(getInfo(photo));
//            mInfoWindow[i] = new InfoWindow(BitmapDescriptorFactory.fromView(button), point, - 150 , listener);
//            mBaiduMap.showInfoWindow(mInfoWindow[i]);
        }

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final Project_photo project = mLatLngMap.get(marker);
                mBaiduMap.hideInfoWindow();
                Button button = new Button(BaiduMapShowPhoto.this);
                button.setBackgroundResource(R.drawable.btn_blue);
                button.setTextColor(getResources().getColor(R.color.white));
                InfoWindow.OnInfoWindowClickListener listener = null;
                button.setText(getInfo(project));

                listener = new InfoWindow.OnInfoWindowClickListener() {
                    public void onInfoWindowClick() {
//                        LatLng end = marker.getPosition();
//                        startRoutePlanDriving(marker.getPosition());
//                        startNavi(end, project);
                    }
                };
                LatLng ll = marker.getPosition();
                mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -200, listener);
                mBaiduMap.showInfoWindow(mInfoWindow);

                return true;
            }
        });

    }

    public void initData(List<Project_jd2> project_jd2) {
        if (project_jd2==null) {
            return;
        }
        if (mMarker != null) {
            for (int i = 0; i < mMarker.length; i++) {
                mMarker[i].remove();
            }
        }
        mList.clear();
        mLatLngMap.clear();


        for (int i = 0; i < project_jd2.size(); i++) {
            String menu = project_jd2.get(i).getMenu();
            String uname = project_jd2.get(i).getUname();
            List<Project_photo> project_photos = project_jd2.get(i).getProject_photo();

            if (project_photos != null) {
                for (int j = 0; j < project_photos.size(); j++) {
                    Project_photo photo = project_photos.get(j);
                    photo.setMenu(menu);
                    photo.setUname(uname);
                    if (!AbStrUtil.isEmpty(photo.getLatitude())) {
                        mList.add(photo);
                    }
                }
            }
        }

        if (mList.size() > 0) {
            mMarker = new Marker[mList.size()];
            initOverlay();
        }
    }

    private String getInfo(Project_photo project) {
        return project.getInfo2();
    }







}
