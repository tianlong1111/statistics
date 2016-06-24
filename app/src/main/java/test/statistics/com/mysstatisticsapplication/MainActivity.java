package test.statistics.com.mysstatisticsapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jiyoutang.statistics.CommonUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CommonUtil.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonUtil.onPause(this);
    }
}
