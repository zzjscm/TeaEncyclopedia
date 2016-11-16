package com.zz.teaencyclopedia;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.zz.teaencyclopedia.adapters.TabFragmentAdapter;
import com.zz.teaencyclopedia.beans.TeasMessage;
import com.zz.teaencyclopedia.sqlites.MySqliteOpenHelper;
import com.zz.teaencyclopedia.url.Urls;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView empty;
    private BaseAdapter mAdapter;
    private MySqliteOpenHelper mHelper;
    private SQLiteDatabase db;
    private Cursor mCursor;
    private List<TeasMessage.DataBean> mData=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        getSupportActionBar().hide();
        initView();
        initListView();
        initData();
        initItemClickListener();
        initItemLongClickListener();
    }

    private void initItemLongClickListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(CollectionActivity.this);
                builder.setTitle("提示");
                builder.setIcon(R.mipmap.icon_dialog);
                builder.setMessage("确定要取消收藏吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int num = db.delete("collection", "id=" + mData.get(position).getId(), null);

                        if (num>0){
                            final TranslateAnimation translate=new TranslateAnimation(
                                    Animation.RELATIVE_TO_SELF,0,
                                    Animation.RELATIVE_TO_SELF,-1,
                                    Animation.RELATIVE_TO_SELF,0,
                                    Animation.RELATIVE_TO_SELF,0);
                            translate.setDuration(1000);
                            view.startAnimation(translate);

                            translate.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                    TranslateAnimation animation1=new TranslateAnimation(Animation.RELATIVE_TO_SELF,0,
                                            Animation.RELATIVE_TO_SELF,0,
                                            Animation.RELATIVE_TO_SELF,1,Animation.RELATIVE_TO_SELF,0);
                                    animation1.setDuration(1000);
                                    int currentTop=view.getTop();
                                    int count=mListView.getChildCount();
                                    for (int i = 0; i < count; i++) {
                                        View itemView=mListView.getChildAt(i);
                                        if (itemView.getTop()>=currentTop){
                                            itemView.startAnimation(animation1);
                                        }
                                    }
                                    mData.clear();
                                    initData();

                                    animation1.setAnimationListener(new Animation.AnimationListener() {

                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {

                                            Toast.makeText(CollectionActivity.this, "取消收藏成功！", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                        }
                                    });

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                        }else {
                            Toast.makeText(CollectionActivity.this, "取消收藏失败，请重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create().show();
                return false;
            }
        });
    }

    private void initItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CollectionActivity
                        .this, DetailActivity.class);
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
        });
    }


    private void initListView() {
        mAdapter = new TabFragmentAdapter(this,mData);

        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(empty);
    }

    private void initData() {
        mCursor = db.query("collection",
                new String[]{"id","title","source","wap_thumb","create_time","nickname"},
                null, null, null, null,"_id desc");
        List<TeasMessage.DataBean> data = new ArrayList<>();
        while (mCursor.moveToNext()) {
            String id = mCursor.getString(mCursor.getColumnIndex("id"));
            String title = mCursor.getString(mCursor.getColumnIndex("title"));
            String source = mCursor.getString(mCursor.getColumnIndex("source"));
            String wap_thumb = mCursor.getString(mCursor.getColumnIndex("wap_thumb"));
            String create_time = mCursor.getString(mCursor.getColumnIndex("create_time"));
            String nickname = mCursor.getString(mCursor.getColumnIndex("nickname"));
            TeasMessage.DataBean dataBean = new TeasMessage.DataBean();
            dataBean.setId(id);
            dataBean.setTitle(title);
            dataBean.setSource(source);
            dataBean.setCreate_time(create_time);
            dataBean.setWap_thumb(wap_thumb);
            dataBean.setNickname(nickname);
            data.add(dataBean);
        }
        mData.addAll(data);
        mAdapter.notifyDataSetChanged();
    }


    private void initView() {
        mListView = (ListView) findViewById(R.id.listView);
        empty = (TextView) findViewById(R.id.empty);
        mHelper = new MySqliteOpenHelper(this);
        db = mHelper.getReadableDatabase();
    }

    public void collectClick(View view) {
        switch (view.getId()){
            case R.id.collectBack:
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
    protected void onDestroy() {
        super.onDestroy();
        mCursor.close();
        db.close();
    }

}
