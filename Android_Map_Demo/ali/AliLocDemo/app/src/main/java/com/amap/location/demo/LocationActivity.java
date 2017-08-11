package com.amap.location.demo;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.xiaxl.gaodemap.R;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 文档地址：
 * http://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation#configure
 */
public class LocationActivity extends PermissionsActivity
        implements
        OnCheckedChangeListener,
        OnClickListener {

    // 低功耗、仅设备、高精度 三个模式的RadioGroup
    private RadioGroup rgLocationMode;
    // 定位周期输入框
    private EditText etInterval;
    // 网络超时的输入框
    private EditText etHttpTimeout;
    // 单次定位
    private CheckBox cbOnceLocation;
    // 逆地理编码
    private CheckBox cbAddress;
    // GPS优先
    private CheckBox cbGpsFirst;
    // 开启缓存
    private CheckBox cbCacheAble;
    // 提高首次定位精度
    private CheckBox cbOnceLastest;
    // 使用传感器
    private CheckBox cbSensorAble;
    // 定位结果显示
    private TextView tvResult;
    // 开始定位
    private Button btLocation;

    //--------------------
    private AMapLocationClient mAMapLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mAMapLocationClientOption = new AMapLocationClientOption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setTitle(R.string.title_location);

        // 初始化View
        initView();
        //初始化定位
        initLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLocation();
    }

    /**
     * 开始定位按钮
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bt_location) {
            // 开始定位
            if (btLocation.getText().equals(
                    getResources().getString(R.string.startLocation))) {
                setViewEnable(false);
                btLocation.setText(getResources().getString(
                        R.string.stopLocation));
                tvResult.setText("正在定位...");
                startLocation();
            }
            // 停止定位
            else {
                setViewEnable(true);
                btLocation.setText(getResources().getString(
                        R.string.startLocation));
                stopLocation();
                tvResult.setText("定位停止");
            }
        }
    }

    /**
     * 低功耗、仅设备、高精度 三个模式的RadioGroup
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (null == mAMapLocationClientOption) {
            mAMapLocationClientOption = new AMapLocationClientOption();
        }
        switch (checkedId) {
            // 低功耗定位模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）；
            case R.id.rb_batterySaving:
                mAMapLocationClientOption.setLocationMode(AMapLocationMode.Battery_Saving);
                break;
            // 仅设备模式: 不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位
            case R.id.rb_deviceSensors:
                mAMapLocationClientOption.setLocationMode(AMapLocationMode.Device_Sensors);
                break;
            //高精度定位模式：会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息。
            case R.id.rb_hightAccuracy:
                mAMapLocationClientOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
                break;
            default:
                break;
        }
    }


    /**
     * 初始化View
     */
    private void initView() {
        // 低功耗、仅设备、高精度 三个模式的RadioGroup
        rgLocationMode = (RadioGroup) findViewById(R.id.rg_locationMode);
        rgLocationMode.setOnCheckedChangeListener(this);
        // 定位周期输入框
        etInterval = (EditText) findViewById(R.id.et_interval);
        // 网络超时的输入框
        etHttpTimeout = (EditText) findViewById(R.id.et_httpTimeout);
        // 单次定位
        cbOnceLocation = (CheckBox) findViewById(R.id.cb_onceLocation);
        // GPS优先
        cbGpsFirst = (CheckBox) findViewById(R.id.cb_gpsFirst);
        // 逆地理编码
        cbAddress = (CheckBox) findViewById(R.id.cb_needAddress);
        // 开启缓存
        cbCacheAble = (CheckBox) findViewById(R.id.cb_cacheAble);
        // 提高首次定位精度
        cbOnceLastest = (CheckBox) findViewById(R.id.cb_onceLastest);
        // 使用传感器
        cbSensorAble = (CheckBox) findViewById(R.id.cb_sensorAble);
        // 定位结果显示
        tvResult = (TextView) findViewById(R.id.tv_result);
        // 开始定位
        btLocation = (Button) findViewById(R.id.bt_location);
        btLocation.setOnClickListener(this);
    }

    /**
     * 设置所有的控件是否可用
     *
     * @param isEnable
     */
    private void setViewEnable(boolean isEnable) {
        for (int i = 0; i < rgLocationMode.getChildCount(); i++) {
            rgLocationMode.getChildAt(i).setEnabled(isEnable);
        }
        etInterval.setEnabled(isEnable);
        etHttpTimeout.setEnabled(isEnable);
        cbOnceLocation.setEnabled(isEnable);
        cbGpsFirst.setEnabled(isEnable);
        cbAddress.setEnabled(isEnable);
        cbCacheAble.setEnabled(isEnable);
        cbOnceLastest.setEnabled(isEnable);
        cbSensorAble.setEnabled(isEnable);
    }


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        mAMapLocationClient = new AMapLocationClient(this.getApplicationContext());
        //设置定位参数
        mAMapLocationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        mAMapLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (null != aMapLocation) {
                    //解析定位结果
                    String result = Utils.getLocationStr(aMapLocation);
                    tvResult.setText(result);
                } else {
                    tvResult.setText("定位失败，loc is null");
                }
            }
        });
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        // 默认高精度
        // 低功耗定位模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）；
        // 仅设备模式: 不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位
        // 高精度定位模式：会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息。
        mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
        // 设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setGpsFirst(false);
        // 设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setHttpTimeOut(30000);
        // 设置定位间隔。默认为2秒
        mOption.setInterval(2000);
        // 设置是否返回逆地理地址信息。默认是true
        // 根据经纬度返回地理位置信息
        mOption.setNeedAddress(true);
        // 设置是否单次定位。默认是false
        mOption.setOnceLocation(false);
        // 设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mOption.setOnceLocationLatest(false);
        // 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);
        // 设置是否使用传感器。默认是false
        mOption.setSensorEnable(false);
        // 设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setWifiScan(true);
        // 设置是否使用缓存定位，默认为true
        mOption.setLocationCacheEnable(true);
        return mOption;
    }


    // 根据控件的选择，重新设置定位参数
    private void resetOption() {
        // 设置是否需要显示地址信息
        mAMapLocationClientOption.setNeedAddress(cbAddress.isChecked());
        /**
         * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
         * 注意：只有在高精度模式下的单次定位有效，其他方式无效
         */
        mAMapLocationClientOption.setGpsFirst(cbGpsFirst.isChecked());
        // 设置是否开启缓存
        mAMapLocationClientOption.setLocationCacheEnable(cbCacheAble.isChecked());
        // 设置是否单次定位
        mAMapLocationClientOption.setOnceLocation(cbOnceLocation.isChecked());
        //设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
        mAMapLocationClientOption.setOnceLocationLatest(cbOnceLastest.isChecked());
        //设置是否使用传感器
        mAMapLocationClientOption.setSensorEnable(cbSensorAble.isChecked());
        //设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        String strInterval = etInterval.getText().toString();
        if (!TextUtils.isEmpty(strInterval)) {
            try {
                // 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
                mAMapLocationClientOption.setInterval(Long.valueOf(strInterval));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        String strTimeout = etHttpTimeout.getText().toString();
        if (!TextUtils.isEmpty(strTimeout)) {
            try {
                // 设置网络请求超时时间
                mAMapLocationClientOption.setHttpTimeOut(Long.valueOf(strTimeout));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
        resetOption();
        // 设置定位参数
        mAMapLocationClient.setLocationOption(mAMapLocationClientOption);
        // 启动定位
        mAMapLocationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        mAMapLocationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mAMapLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mAMapLocationClient.onDestroy();
            mAMapLocationClient = null;
            mAMapLocationClientOption = null;
        }
    }
}
