package fans.time.com.android_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import fans.time.com.android_test.area.AreaManager;

public class MainActivity extends AppCompatActivity {

    TextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        textView = (TextView) findViewById(R.id.text01);
        textView.setText(AreaManager.getProvinceListForRank(this, null).toString());

    }
}
