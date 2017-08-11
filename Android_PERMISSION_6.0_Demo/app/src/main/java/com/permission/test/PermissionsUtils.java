package com.permission.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

/**
 * 检查权限的工具类
 */
public class PermissionsUtils {
    private static final String TAG = PermissionsUtils.class.getSimpleName();

    // 音频
    public static final int PERMISSION_REQUESTCODE_AUDIO = 0x001;


    /**
     * 默认是授予权限的,
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermissionGranted(Context context, String permission) {
        LogUtils.d(TAG, "---checkPermissionGranted---");
        // For Android < Android M, self permissions are always granted.
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (getTargetSdkVersion(context) >= 23) {
                // targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }
        LogUtils.d(TAG, "result: " + result);
        return result;
    }

    /**
     * 请求权限
     *
     * @param activity
     * @param permissions
     */
    public static void requestPermissions(Activity activity, int requestCode, String... permissions) {
        LogUtils.d(TAG, "---requestPermissions---");
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * @param grantResults 请求权限的返回结果
     * @return true 集合中的权限已全部被授予；false 权限未被授予
     */
    public static boolean checkGrantStatus(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取targetSdkVersion
     *
     * @param context
     * @return
     */
    private static int getTargetSdkVersion(Context context) {
        LogUtils.d(TAG, "---getTargetSdkVersion---");
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
            LogUtils.d(TAG, "targetSdkVersion: " + targetSdkVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }


    /**
     * 启动应用的设置,进入手动配置权限页面
     */
    public static void startAppSettings(Context context) {
        LogUtils.d(TAG, "---startAppSettings---");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

}
