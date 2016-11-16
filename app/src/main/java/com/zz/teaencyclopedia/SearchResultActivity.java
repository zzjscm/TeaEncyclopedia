package com.zz.teaencyclopedia;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.zz.teaencyclopedia.adapters.TabFragmentAdapter;
import com.zz.teaencyclopedia.beans.TeasMessage;
import com.zz.teaencyclopedia.interfaces.DataCallback;
import com.zz.teaencyclopedia.net.MyAsyncTask;
import com.zz.teaencyclopedia.sqlites.MySqliteOpenHelper;
import com.zz.teaencyclopedia.url.Urls;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements DataCallback, AdapterView.OnItemClickListener {

    private ListView mListView;
    private TextView mText;
    private List<TeasMessage.DataBean> mData=new ArrayList<>();
    private TextView empty;
    private BaseAdapter mAdapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        getSupportActionBar().hide();
        initView();
        initListView();
        initData();
        initOnClickListener();
    }

    private void initOnClickListener() {
        mListView.setOnItemClickListener(this);
    }

    private void initListView() {
        mAdapter = new TabFragmentAdapter(this,mData);

        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(empty);
    }

    private void initData() {
        String msg = getIntent().getStringExtra("msg");
        mText.setText(msg);
        String path= Urls.SEARCH_URL+msg;
        new MyAsyncTask(this).execute(path);
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.listView);
        mText = (TextView) findViewById(R.id.searchMessage);
        empty = (TextView) findViewById(R.id.empty);
    }

    public void searchClick(View view) {
        switch (view.getId()){
            case R.id.searchBack:
                this.finish();
                break;
            case R.id.backHome:
                Intent home=new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                break;
        }
    }

    @Override
    public void callback(byte[] bytes) {
        TeasMessage teasMessage = JSON.parseObject(new String(bytes), TeasMessage.class);
        mData.addAll(teasMessage.getData());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        db = new MySqliteOpenHelper(this).getReadableDatabase();
        Intent intent = new Intent(this, DetailActivity.class);
        TeasMessage.DataBean dataBean = mData.get(position);
        String id0 = dataBean.getId();
        String contentPath = Urls.CONTENT_URL + id0;
        String jsonString = JSON.toJSONString(dataBean);
        intent.putExtra("json",jsonString);
        intent.putExtra("path", contentPath);
        startActivity(intent);
        ContentValues values=new ContentValues();
        db.delete("history","id="+id0,null);
        values.put("id", id0);
        values.put("title",dataBean.getTitle());
        values.put("source",dataBean.getSource());
        values.put("wap_thumb",dataBean.getWap_thumb());
        values.put("create_time",dataBean.getCreate_time());
        values.put("nickname",dataBean.getNickname());
        db.insert("history",null,values);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
