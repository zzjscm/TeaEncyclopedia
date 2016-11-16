package com.zz.teaencyclopedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FeedbackActivity extends AppCompatActivity {

    private EditText title;
    private EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        getSupportActionBar().hide();
        initView();
    }

    private void initView() {
        title = (EditText) findViewById(R.id.editText);
        msg = (EditText) findViewById(R.id.feedbackMsg);
    }

    public void feedbackClick(View view) {
        switch (view.getId()){
            case R.id.feedBack:
                this.finish();
                break;
            case R.id.backHome:
                Intent home=new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                break;
            case R.id.submit:
                String title = this.title.getText().toString().trim();
                String msg = this.msg.getText().toString().trim();
                if (title.equals("")||msg.equals("")){
                    Toast.makeText(this, "标题和内容不能为空！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "意见反馈提交失败，请重新尝试！", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

}
