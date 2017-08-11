package com.permission.test;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * 权限获取页面
 */
public class PermissionsActivity extends Activity {
    private static final String TAG = PermissionsActivity.class.getSimpleName();


    /**
     * 要请求的所有权限
     */
    // 危险权限：录音
    private static final String permissionAudio = Manifest.permission.RECORD_AUDIO;
    // 普通权限：允许应用程序修改全局声音设置的权限
    private static final String permissionModifyAudioSettings = Manifest.permission.MODIFY_AUDIO_SETTINGS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d(TAG, "---onCreate---");
        //
        setContentView(R.layout.activity_permissions);

        // 普通权限
        PermissionsUtils.checkPermissionGranted(PermissionsActivity.this, permissionAudio);
        // 危险权限
        if (PermissionsUtils.checkPermissionGranted(PermissionsActivity.this, permissionModifyAudioSettings) == false) {
            //
            PermissionsUtils.requestPermissions(PermissionsActivity.this, PermissionsUtils.PERMISSION_REQUESTCODE_AUDIO, permissionModifyAudioSettings);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d(TAG, "---onResume---");
    }

    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        LogUtils.d(TAG, "---onRequestPermissionsResult---");
        LogUtils.d(TAG, "requestCode: " + requestCode);
        LogUtils.d(TAG, "permissions: " + permissions);
        for (int i = 0; i < permissions.length; i++) {
            LogUtils.d(TAG, "-----" + i + "-------");
            LogUtils.d(TAG, "permissions[i]: " + permissions[i]);
        }
        LogUtils.d(TAG, "grantResults: " + grantResults);
        for (int i = 0; i < grantResults.length; i++) {
            LogUtils.d(TAG, "-----" + i + "-------");
            LogUtils.d(TAG, "grantResults[i]: " + grantResults[i]);
        }
        // 权限相关
        if (requestCode == PermissionsUtils.PERMISSION_REQUESTCODE_AUDIO) {
            //
            if (PermissionsUtils.checkGrantStatus(grantResults)) {
                LogUtils.d(TAG, "---权限授予---");
                // TODO 权限全部被授予
            } else {
                // TODO 权限未被授予
                LogUtils.d(TAG, "---权限拒绝---");
                showMissingPermissionDialog();
            }
        }
    }


    /**
     * 显示缺失权限提示
     */
    private void showMissingPermissionDialog() {
        //
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PermissionsActivity.this);
        builder.setMessage(R.string.string_help_text);
        builder.setTitle(R.string.help);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //  引导，进入设置
                PermissionsUtils.startAppSettings(PermissionsActivity.this);
            }
        });
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // 关闭 当前页面...
                PermissionsActivity.this.finish();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }
}