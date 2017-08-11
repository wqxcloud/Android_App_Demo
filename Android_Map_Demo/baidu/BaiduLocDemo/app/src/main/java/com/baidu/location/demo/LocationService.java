package com.baidu.location.demo;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.content.Context;

public class LocationService {
    //
    private LocationClient mLocationClient = null;
    private LocationClientOption mLocationClientOption = null;
    //
    private Object objLock = new Object();

    /***
     *
     * @param locationContext
     */
    public LocationService(Context locationContext) {
        synchronized (objLock) {
            if (mLocationClient == null) {
                mLocationClient = new LocationClient(locationContext);
                mLocationClient.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    /***
     *
     * @param listener
     * @return
     */

    public boolean registerListener(BDLocationListener listener) {
        boolean isSuccess = false;
        if (listener != null) {
            mLocationClient.registerLocationListener(listener);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void unregisterListener(BDLocationListener listener) {
        if (listener != null) {
            mLocationClient.unRegisterLocationListener(listener);
        }
    }

    /***
     *
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getDefaultLocationClientOption() {
        if (mLocationClientOption == null) {
            mLocationClientOption = new LocationClientOption();
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mLocationClientOption.setLocationMode(LocationMode.Hight_Accuracy);
            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mLocationClientOption.setCoorType("bd09ll");
            //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mLocationClientOption.setScanSpan(3000);
            //可选，设置是否需要地址信息，默认不需要
            mLocationClientOption.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            mLocationClientOption.setIsNeedLocationDescribe(true);
            //可选，设置是否需要设备方向结果
            mLocationClientOption.setNeedDeviceDirect(false);
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mLocationClientOption.setLocationNotify(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mLocationClientOption.setIgnoreKillProcess(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mLocationClientOption.setIsNeedLocationDescribe(true);
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mLocationClientOption.setIsNeedLocationPoiList(true);
            //可选，默认false，设置是否收集CRASH信息，默认收集
            mLocationClientOption.SetIgnoreCacheException(false);
            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            mLocationClientOption.setIsNeedAltitude(false);

        }
        return mLocationClientOption;
    }

    public void start() {
        synchronized (objLock) {
            if (mLocationClient != null && !mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }

    public void stop() {
        synchronized (objLock) {
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
        }
    }


}
