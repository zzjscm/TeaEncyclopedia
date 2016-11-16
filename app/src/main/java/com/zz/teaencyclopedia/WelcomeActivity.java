package com.zz.teaencyclopedia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zz.teaencyclopedia.adapters.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Button comingBtn;
    private LinearLayout indicators;
    private List<ImageView> mData=new ArrayList<>();
    private int lastPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().hide();
        initView();
        initData();
        initViewPager();
        initIndicator();
        initListener();
    }

    private ImageButton[] imgBtn=new ImageButton[3];
    private void initIndicator() {
        for (int i = 0; i < 3; i++) {
            imgBtn[i]= (ImageButton) indicators.getChildAt(i);

        }
        imgBtn[0].setEnabled(false);
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imgBtn[position].setEnabled(false);
                imgBtn[lastPosition].setEnabled(true);
                lastPosition=position;
                if (position==2){
                    comingBtn.setVisibility(View.VISIBLE);
                }else {
                    comingBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {

        for (int i = 0; i < 3; i++) {
            ImageView imageView=new ImageView(this);
            imageView.setImageResource(getResources().getIdentifier("slide"+(i+1),"mipmap",getPackageName()));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mData.add(imageView);
        }
    }

    private void initViewPager() {
        PagerAdapter adapter=new MyPagerAdapter(mData);
        mViewPager.setAdapter(adapter);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.welViewPager);
        comingBtn = (Button) findViewById(R.id.coming);
        indicators = (LinearLayout) findViewById(R.id.indicator);
    }

    public void click(View view) {
        this.startActivity(new Intent(this,MainActivity.class));
        this.finish();
        SharedPreferences sp=getSharedPreferences("appConfig",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //已经使用过APP了 设为false
        editor.putBoolean("isFirst",false);
        editor.commit();
    }
}
