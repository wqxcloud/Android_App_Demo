package baidumapsdk.demo.map;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

import baidumapsdk.demo.R;


public class LocationActivity extends Activity {

    private static final String TAG = "xiaxl: Location";


    private int mCurrentDirection = 0;


    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData mMyLocationData;


    /**
     *
     */
    //一个显示地图的视图（View）。
    // 在使用地图组件之前请确保已经调用了 SDKInitializer.initialize(Context) 函数以提供全局 Context 信息。
    MapView mMapView;
    // 定义 BaiduMap 地图对象的操作方法与接口
    BaiduMap mBaiduMap;

    /**
     * 定位相关
     */
    public LocationService mLocationService;


    /**
     *
     */
    ArrayList<DataModel> dataModels = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);


        // 地图初始化
        mMapView = (MapView) findViewById(R.id.mapView);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        /**
         * 地图状态变化回调
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                Log.e(TAG, "onMapStatusChangeStart");
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                Log.e(TAG, "onMapStatusChange");
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.e(TAG, "onMapStatusChangeFinish");


                //#################################################################
                // 生成随机数据
                dataModels = getRandomDataModels();
                //
                addOthersMarker(dataModels);
            }
        });

        /**
         * marker点击事件
         */
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //
                showInfoWindow(marker);
                return true;
            }
        });


        /***
         * 初始化定位sdk，建议在Application中创建
         */
        mLocationService = new LocationService(getApplicationContext());
        mLocationService.registerListener(mBDLocationListener);
        // 开始定位
        mLocationService.start();
    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //##########################
        //注销掉监听
        mLocationService.unregisterListener(mBDLocationListener);
        //停止定位服务
        mLocationService.stop();
        //#######################
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }


    private BDLocationListener mBDLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mMyLocationData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //
            mBaiduMap.setMyLocationData(mMyLocationData);
            //
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(latLng).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //#################################################################
                        // 生成随机数据
                        dataModels = getRandomDataModels();
                        //
                        addOthersMarker(dataModels);
                    }
                }, 1000);
            }


        }

    };


    //#################################随机坐标和数据######################################

    /**
     * 添加marker
     *
     * @param targets
     */
    private void addOthersMarker(ArrayList<DataModel> targets) {
        if (targets == null || targets.size() == 0) {
            return;
        }
        // 清空
        mBaiduMap.clear();

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
            // 设置marker的icon
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding));
            // 我的位置
            markerOptions.title(dataModel.userName);
            markerOptions.draggable(true);
            // 添加红点
            Marker marker = (Marker) mBaiduMap.addOverlay(markerOptions);

            //
            if (i == 0) {
                showInfoWindow(marker);
            }

        }
    }

    /**
     * 在对应的markder位置显示infowindow
     *
     * @param marker
     */
    private void showInfoWindow(Marker marker) {

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.location_custom_info_view, null);
        InfoWindow mInfoWindow = new InfoWindow(view, marker.getPosition(), -100);
        //
        TextView titleTextView = (TextView) view.findViewById(R.id.info_title_textview);
        TextView descTextView = (TextView) view.findViewById(R.id.info_desc_textview);
        //
        if (dataModels != null) {
            int index = 0;
            for (int i = 0; i < dataModels.size(); i++) {
                if (dataModels.get(i).userName.equals(marker.getTitle())) {
                    index = i;
                    break;
                }
            }
            //
            titleTextView.setText(dataModels.get(index).userName);
            descTextView.setText(dataModels.get(index).className);
        }
        //
        mBaiduMap.showInfoWindow(mInfoWindow);
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
        int margin = 160;
        //
        ArrayList<DataModel> dataModels = new ArrayList<DataModel>();
        for (int i = 0; i < 10; i++) {
            int tempWidth = (int) (margin + (width - margin * 2) * Math.random());
            int tempHeight = (int) (margin + (height - margin * 2) * Math.random());
            // 随机生成经纬度
            Point point = new Point(tempWidth, tempHeight);
            if (mBaiduMap != null) {
                Log.e(TAG, "---mBaiduMap != null---");
                if (mBaiduMap.getProjection() != null) {
                    Log.e(TAG, "---mBaiduMap.getProjection() != null---");
                    LatLng latlng = mBaiduMap.getProjection().fromScreenLocation(point);
                    //
                    DataModel dataModel = new DataModel(randomNames[i], randomClasses[i], latlng);
                    //----
                    dataModels.add(dataModel);
                }

            }

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


}
