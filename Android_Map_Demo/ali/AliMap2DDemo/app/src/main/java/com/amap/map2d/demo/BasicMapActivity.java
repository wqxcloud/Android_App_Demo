package com.amap.map2d.demo;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;

import java.util.ArrayList;

/**
 * 基础地图
 */
public class BasicMapActivity extends Activity {

    //----------地图相关----------
    // 获取地图控件引用
    private MapView mMapView;
    // 地图的控制类
    private AMap mAMap;
    // UI Setting
    private UiSettings mUiSettings;

    //---------定位相关-----------
    private AMapLocationClient mAMapLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mAMapLocationClientOption = null;

    /**
     * 数据
     */
    // 我的位置信息
    private LatLng mLatLng = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);

        // 初始化地图
        initAMapView(savedInstanceState);
        // 地图的控制类
        initAMap();
        // UISetting
        initAMapUISetting();
        // 初始化定位
        initLocation();
        // 开始定位
        startLocation();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        // 停止定位
        stopLocation();
    }


    //#########################################地图相关#############################################

    /**
     * 初始化地图
     *
     * @param savedInstanceState
     */
    private void initAMapView(Bundle savedInstanceState) {
        // 获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

    }

    /**
     * 初始化AMap
     */
    private void initAMap() {
        // 获取地图控制类AMap
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        // -------一像素代表多少米-------
        //mAMap.getScalePerPixel();
        //----英语地图or中文地图----
        //mAMap.setMapLanguage(AMap.ENGLISH);
        //mAMap.setMapLanguage(AMap.CHINESE);
        //------卫星or矢量图------
        //mAMap.setMapType(AMap.MAP_TYPE_NORMAL);
        //mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        // marker点击事件
        mAMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO
                marker.showInfoWindow();
                //
                return true;
            }
        });
        // 移动停止的回调
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                Log.e("xiaxl: ", "onCameraChangeFinish");

                // 生成随机数据
                ArrayList<DataModel> dataModels = getRandomDataModels();
                //
                addOthersMarker(dataModels);
            }
        });


        //------设置定位监听-------
        // 是否可触发定位并显示定位层
//        mAMap.setMyLocationEnabled(true);
//        mAMap.setLocationSource(new LocationSource() {
//            //激活定位
//            @Override
//            public void activate(OnLocationChangedListener listener) {
//                // 初始化定位相关
//                initLocation();
//                // 开启定位
//                startLocation();
//            }
//
//            // 停止定位
//            @Override
//            public void deactivate() {
//                // 停止定位
//                stopLocation();
//                destroyLocation();
//            }
//        });
    }


    /**
     * UI Setting 相关
     */
    private void initAMapUISetting() {
        if (mAMap == null) {
            return;
        }
        // 获取UI Setting
        if (mUiSettings == null) {
            mUiSettings = mAMap.getUiSettings();
        }
        //--------------UI设置开始---------------------
        //设置地图默认的比例尺是否显示
        mUiSettings.setScaleControlsEnabled(true);
        // 设置地图默认的缩放按钮是否显示
        mUiSettings.setZoomControlsEnabled(true);
        // 设置地图默认的指南针是否显示
        mUiSettings.setCompassEnabled(true);
        // 设置地图是否可以手势滑动
        mUiSettings.setScrollGesturesEnabled(true);
        // 设置地图是否可以手势缩放大小
        mUiSettings.setZoomGesturesEnabled(true);

        //-------定位按钮-------
        // 是否显示默认的定位按钮
        //mUiSettings.setMyLocationButtonEnabled(true);
        //--------地图logo位置---------
        // 设置地图logo显示在左下方
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
        // 设置地图logo显示在底部居中
        //mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_CENTER);
        // 设置地图logo显示在右下方
        //mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
    }


    /**
     * 添加marker
     *
     * @param target
     */
    private void addMyLocationMarker(LatLng target) {
        //
        mAMap.clear();
        //
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(target);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        // 我的位置
        markerOptions.title("我的位置");
        markerOptions.draggable(true);

        // 添加红点
        Marker marker = mAMap.addMarker(markerOptions);
        //marker.showInfoWindow();
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void animateCamera(LatLng target, AMap.CancelableCallback callback) {
        if (mAMap == null) {
            return;
        }
        // target - 目标位置的屏幕中心点经纬度坐标 。
        // zoom - 目标可视区域的缩放级别。缩放级别为3~19。
        // tilt - 目标可视区域的倾斜度，2D地图此参数无意义 。
        // bearing - 可视区域指向的方向，2D地图此参数无意义 。
        CameraPosition cameraPosition = new CameraPosition(target, 17, 0, 30);
        // camera要移动到的位置
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        // 移动camera
        mAMap.animateCamera(update, 1000, callback);
    }


    //#########################################定位相关#############################################

    /**
     * 初始化定位
     */
    private void initLocation() {
        if (mAMapLocationClient == null) {
            //初始化client
            mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
            //设置定位参数
            mAMapLocationClient.setLocationOption(getDefaultLocationOption());
            // 设置定位监听
            mAMapLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    //  定位结果
                    if (aMapLocation != null) {
                        //  获取经纬度
                        BasicMapActivity.this.mLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        // 添加marker
                        addMyLocationMarker(mLatLng);
                        // 移动地图
                        animateCamera(mLatLng, null);
                    }
                }
            });
        }
    }

    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultLocationOption() {
        mAMapLocationClientOption = new AMapLocationClientOption();
        // 默认高精度
        // 低功耗定位模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）；
        // 仅设备模式: 不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位
        // 高精度定位模式：会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息。
        mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置是否gps优先，只在高精度模式下有效。默认关闭
        mAMapLocationClientOption.setGpsFirst(false);
        // 设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mAMapLocationClientOption.setHttpTimeOut(30000);
        // 设置定位间隔。默认为2秒,这里暂时设置为60s*30
        mAMapLocationClientOption.setInterval(60000 * 30);
        // 设置是否返回逆地理地址信息。默认是true
        // 根据经纬度返回地理位置信息
        mAMapLocationClientOption.setNeedAddress(true);
        // 设置是否单次定位。默认是false
        mAMapLocationClientOption.setOnceLocation(false);
        // 设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mAMapLocationClientOption.setOnceLocationLatest(false);
        // 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);
        // 设置是否使用传感器。默认是false
        mAMapLocationClientOption.setSensorEnable(false);
        // 设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mAMapLocationClientOption.setWifiScan(true);
        // 设置是否使用缓存定位，默认为true
        mAMapLocationClientOption.setLocationCacheEnable(true);
        return mAMapLocationClientOption;
    }

    /**
     * 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
     * 在定位结束后，在合适的生命周期调用onDestroy()方法
     * 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
     */
    private void startLocation() {
        // 启动定位
        if (mAMapLocationClient != null) {
            mAMapLocationClient.startLocation();
        }

    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        // 停止定位

        if (null != mAMapLocationClient) {
            // 停止定位
            mAMapLocationClient.stopLocation();
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mAMapLocationClient.onDestroy();
            mAMapLocationClient = null;
            mAMapLocationClientOption = null;
        }
    }


    //#################################随机坐标和数据######################################

    String[] randomNames = {"小明", "小强", "小刚", "小磊", "小智", "聪聪", "亮亮", "天天", "莹莹", "盈盈"};
    String[] randomClasses = {"感动中国", "经济学原理", "微积分", "疯狂旅行", "营造的艺术", "迷信从何来", "中国兵器秀", "疯狂旅行", "中国兵器秀", "经济学原理"};

    /**
     * 生成随机数据
     *
     * @return
     */
    private ArrayList<DataModel> getRandomDataModels() {
        // 获取View宽高
        int width = mMapView.getMeasuredWidth();
        int height = mMapView.getMeasuredHeight();
        if (width == 0 || height == 0) {
            return null;
        }
        int margin = 80;
        //
        ArrayList<DataModel> dataModels = new ArrayList<DataModel>();
        for (int i = 0; i < 10; i++) {
            int tempWidth = (int) (margin + (width - margin * 2) * Math.random());
            int tempHeight = (int) (margin + (height - margin * 2) * Math.random());
            // 随机生成经纬度
            Point point = new Point(tempWidth, tempHeight);
            LatLng latlng = mAMap.getProjection().fromScreenLocation(point);
            //
            DataModel dataModel = new DataModel(randomNames[i], randomClasses[i], latlng);
            //----
            dataModels.add(dataModel);
        }
        return dataModels;

    }


    public static class DataModel {
        public String userName;
        public String className;
        public LatLng latLng;

        public DataModel(String userName, String className, LatLng latLng) {
            this.userName = userName;
            this.className = className;
            this.latLng = latLng;
        }
    }


    /**
     * 添加marker
     *
     * @param targets
     */
    private void addOthersMarker(ArrayList<DataModel> targets) {
        if (targets == null || targets.size() == 0) {
            return;
        }
        // 添加我的位置marker
        addMyLocationMarker(mLatLng);
        // 添加其他兴趣点
        for (int i = 0; i < targets.size(); i++) {
            DataModel dataModel = targets.get(i);

            //
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(dataModel.latLng);

//            // 自定义Marker
//            TextView textView = new TextView(getApplicationContext());
//            textView.setText("adfadfadf");
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//            textView.setTextColor(Color.BLACK);
//            textView.setBackgroundResource(R.drawable.custom_info_bubble);
//            BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromView(textView);
//            markerOptions.icon(markerIcon);

            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // 我的位置
            markerOptions.title(dataModel.userName);
            markerOptions.snippet(dataModel.className);
            markerOptions.draggable(true);
            // 添加红点
            Marker marker = mAMap.addMarker(markerOptions);
            marker.showInfoWindow();


        }
    }


}
