package com.zz.teaencyclopedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().hide();
    }



    public void messageClick(View view) {
        switch (view.getId()){
            case R.id.messageBack:
                this.finish();
                break;
            case R.id.backHome:
                Intent home=new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                break;
        }
    }
}
