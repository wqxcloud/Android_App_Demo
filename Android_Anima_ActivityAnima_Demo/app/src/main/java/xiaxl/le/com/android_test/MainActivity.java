package xiaxl.le.com.android_test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    Button btn = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_main);

        //
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSecond();
            }
        });
    }


    private void gotoSecond() {

        int[] viewPos = new int[2];
        if (btn != null && btn != null) {
            btn.getLocationOnScreen(viewPos);
        }


        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SecondActivity.class);
        //
        intent.putExtra(SecondActivity.INTENT_KEY_WINDOW_ANIMATION_SATRT_Y1, viewPos[1]);
        intent.putExtra(SecondActivity.INTENT_KEY_WINDOW_ANIMATION_SATRT_Y2, viewPos[1] + btn.getHeight());
        //
        MainActivity.this.startActivity(intent);
    }

}

