package com.zz.teaencyclopedia;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zz.teaencyclopedia.beans.TeasMessage;
import com.zz.teaencyclopedia.beans.WebMessage;
import com.zz.teaencyclopedia.interfaces.DataCallback;
import com.zz.teaencyclopedia.net.MyAsyncTask;
import com.zz.teaencyclopedia.sqlites.MySqliteOpenHelper;

public class DetailActivity extends AppCompatActivity {

    private String mPatn;
    private WebView mWebView;
    private TextView title;
    private TextView source;
    private TextView time;
    private TeasMessage.DataBean dataBean;
    private MySqliteOpenHelper mOpenHelper;
    private SQLiteDatabase db;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().hide();
        initView();
        Intent intent = getIntent();
        mPatn = intent.getStringExtra("path");
        String json = intent.getStringExtra("json");
        dataBean = JSON.parseObject(json, TeasMessage.DataBean.class);
        initWebView();
    }


    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        new MyAsyncTask(new DataCallback() {
            @Override
            public void callback(byte[] bytes) {
                String data = new String(bytes);
                WebMessage webMessage = JSON.parseObject(data, WebMessage.class);
//                String url = webMessage.getData().getWeiboUrl();
                WebMessage.DataBean dataBean = webMessage.getData();
                title.setText(dataBean.getTitle());
                time.setText("时间："+dataBean.getCreate_time());
                source.setText("来源："+dataBean.getSource());

                String html = dataBean.getWap_content();
//                Log.d("flag", "----------------->initWebView: webdata" +url);
//                mWebView.loadUrl(url);
                mWebView.loadDataWithBaseURL(null,html,"text/html","utf-8",null);
            }
        }).execute(mPatn);



    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.myweb);
        title = (TextView) findViewById(R.id.webTitle);
        source = (TextView) findViewById(R.id.webSource);
        time = (TextView) findViewById(R.id.webTime);

        mOpenHelper = new MySqliteOpenHelper(this);
        db = mOpenHelper.getReadableDatabase();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void btnClick(View view) {
        switch (view.getId()){
            case R.id.back:
                this.finish();
                break;
            case R.id.share:
                Toast.makeText(this, "分享成功！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.collect:
                String id = dataBean.getId();
                mCursor = db.query("collection", new String[]{"id"}, "id=" + id, null, null, null, null);
                if (mCursor.getCount()==0){
                    ContentValues values=new ContentValues();
                    values.put("id", id);
                    values.put("title",dataBean.getTitle());
                    values.put("source",dataBean.getSource());
                    values.put("wap_thumb",dataBean.getWap_thumb());
                    values.put("create_time",dataBean.getCreate_time());
                    values.put("nickname",dataBean.getNickname());
                    db.insert("collection",null,values);
                    Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "请勿重复收藏！", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
