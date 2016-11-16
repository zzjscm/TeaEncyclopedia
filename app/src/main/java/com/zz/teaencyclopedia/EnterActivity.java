package com.zz.teaencyclopedia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class EnterActivity extends AppCompatActivity {

    private TextView countdown;
    private int count=3;
    private SharedPreferences sp;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    count--;
                    countdown.setText(count+"秒后自动进入");
                    if (count!=0){
                        this.sendEmptyMessageDelayed(1,1000);
                    }else {
                        boolean isFirst = sp.getBoolean("isFirst", true);
                        if (isFirst) {
                            EnterActivity.this.startActivity(new Intent(EnterActivity.this, WelcomeActivity.class));
                            EnterActivity.this.finish();
                        }else {
                            EnterActivity.this.startActivity(new Intent(EnterActivity.this, MainActivity.class));
                            EnterActivity.this.finish();
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        getSupportActionBar().hide();
        intView();
        initTextView();
    }



    private void initTextView() {
        mHandler.sendEmptyMessageDelayed(1,1000);
    }

    private void intView() {
        countdown = (TextView) findViewById(R.id.countdown);
        sp=getSharedPreferences("appConfig",MODE_PRIVATE);
    }
}
