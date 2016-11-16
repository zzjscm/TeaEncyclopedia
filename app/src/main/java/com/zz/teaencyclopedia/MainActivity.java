package com.zz.teaencyclopedia;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;
import com.zz.teaencyclopedia.adapters.MyFragmentPagerAdapter;
import com.zz.teaencyclopedia.ui.TabFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private List<Fragment> data=new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private LinearLayout drawer;
    private EditText searchMsg;
    private boolean isExit=false;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    isExit=false;
                    break;
            }
        }
    };

    public ViewPager getViewPager(){
        return mViewPager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initView();

        initViewPager();
        initTabLayout();

    }

    private void initViewPager() {
        initData();

        mAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager(),data);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
//        mViewPager.setOffscreenPageLimit(4);
    }

    private void initData() {
        for (int i = 0; i < 5; i++) {
            TabFragment fragment=new TabFragment();

            Bundle args=new Bundle();
            args.putInt("key",i);
            fragment.setArguments(args);
            data.add(fragment);
        }
    }

    private void initTabLayout() {
        String[] tabs=new String[]{"头条","百科","资讯","经营","数据"};
        for (int i = 0; i < tabs.length; i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(tabs[i]);
            mTabLayout.addTab(tab);
        }
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
        drawer = (LinearLayout) findViewById(R.id.drawer);
        searchMsg = (EditText) findViewById(R.id.searchMsg);
    }

    //滑出侧滑菜单的点击事件
    public void drawerMore(View view) {
        switch (view.getId()){
            case R.id.more:
                mDrawerLayout.openDrawer(drawer);
                break;
            case R.id.closeMore:
                mDrawerLayout.closeDrawer(drawer);
                break;
            case R.id.home:
                //返回桌面，程序保持后台运行
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                break;
            case R.id.search:
                String msg = searchMsg.getText().toString().trim();
                Log.d("flag", "----------------->drawerMore: msg" +msg);
                if (!msg.equals("")){
                    Intent intent=new Intent(this,SearchResultActivity.class);
                    intent.putExtra("msg",msg);
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "搜索的关键词不能为空！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.collection:
                Intent collection=new Intent(this,CollectionActivity.class);
                startActivity(collection);
                break;
            case R.id.history:
                Intent history=new Intent(this,HistoryActivity.class);
                startActivity(history);
                break;
            case R.id.message:
                Intent message=new Intent(this,MessageActivity.class);
                startActivity(message);
                break;
            case  R.id.callback:
                Intent callback=new Intent(this,FeedbackActivity.class);
                startActivity(callback);
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if(!isExit){
            isExit=true;
            Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(1,2000);
        }else {
            finish();
            System.exit(0);
        }
    }
}
