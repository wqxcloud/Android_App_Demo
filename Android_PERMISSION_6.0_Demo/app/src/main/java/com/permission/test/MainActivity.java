package com.permission.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入下一页面

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PermissionsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });



    }


}
