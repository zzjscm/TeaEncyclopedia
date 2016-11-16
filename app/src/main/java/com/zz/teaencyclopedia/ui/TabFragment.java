package com.zz.teaencyclopedia.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zz.teaencyclopedia.DetailActivity;
import com.zz.teaencyclopedia.MainActivity;
import com.zz.teaencyclopedia.R;
import com.zz.teaencyclopedia.adapters.MyPagerAdapter;
import com.zz.teaencyclopedia.adapters.TabFragmentAdapter;
import com.zz.teaencyclopedia.beans.HeaderImage;
import com.zz.teaencyclopedia.beans.TeasMessage;
import com.zz.teaencyclopedia.cache.MyLruCache;
import com.zz.teaencyclopedia.interfaces.DataCallback;
import com.zz.teaencyclopedia.net.MyAsyncTask;
import com.zz.teaencyclopedia.sqlites.MySqliteOpenHelper;
import com.zz.teaencyclopedia.url.Urls;
import com.zz.teaencyclopedia.utils.HttpUtils;
import com.zz.teaencyclopedia.utils.NetworkUtils;
import com.zz.teaencyclopedia.utils.SdCardUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment implements AdapterView.OnItemClickListener {


    private PullToRefreshListView mPullToReFreshListView;
    private BaseAdapter mAdapter;
    private int page0 = 1, page1 = 1, page2 = 1, page3 = 1, page4 = 1;
    private List<TeasMessage.DataBean> mData0 = new ArrayList<>();
    private List<TeasMessage.DataBean> mData1 = new ArrayList<>();
    private List<TeasMessage.DataBean> mData2 = new ArrayList<>();
    private List<TeasMessage.DataBean> mData3 = new ArrayList<>();
    private List<TeasMessage.DataBean> mData4 = new ArrayList<>();
    private int mIndex;
    private ViewPager mViewPager;
    private List<ImageView> mImgData = new ArrayList<>();
    private List<String> headStr = new ArrayList<>();
    private PagerAdapter mPagerAdapter;
    private int currentPosition = -1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    byte[] bytes = (byte[]) msg.obj;
                    String root=getContext().getExternalCacheDir().getAbsolutePath();
                    String fileName=Urls.HEADERIMAGE_URL.replaceAll("/","").replaceAll("/","");
                    SdCardUtils.saveToFile(bytes,root,fileName);
                    getHeadView(bytes);
                    break;
                case 2:
                    if (this.hasMessages(2)) {
                        this.removeMessages(2);
                    }
                    currentPosition++;
                    if (currentPosition > 2) {
                        currentPosition = 0;
                    }
                    mViewPager.setCurrentItem(currentPosition);
                    this.sendEmptyMessageDelayed(2, 3000);
                    break;
                case 3:
                    currentPosition = msg.arg1;
                    this.sendEmptyMessageDelayed(2, 3000);
                    break;
                case 4:
                    if (this.hasMessages(2))
                        this.removeMessages(2);
                    break;
            }
        }
    };
    private LinearLayout mLinearLayout;
    private TextView mHeadText;
    private ImageView[] mCount;
    private MySqliteOpenHelper mOpenHelper;
    private SQLiteDatabase db;
    private ImageView mBtnTop;
    private ListView mListView;
    private MyLruCache mLruCache;
    private LinearLayout empty;

    public TabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_tab, container, false);
        mOpenHelper = new MySqliteOpenHelper(getContext());
        db = mOpenHelper.getReadableDatabase();
        mLruCache = new MyLruCache(((int) (Runtime.getRuntime().maxMemory() / 8)));
        initView(ret);
        initListener();
        return ret;
    }

    private void initView(View ret) {
        mBtnTop = (ImageView) ret.findViewById(R.id.backtotop);
        empty = (LinearLayout) ret.findViewById(R.id.emptyView);
        mPullToReFreshListView = ((PullToRefreshListView) ret.findViewById(R.id.pullToRefreshListView));
        mPullToReFreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        initLoadingLayoutProxy();
        mIndex = getArguments().getInt("key");
        mListView = mPullToReFreshListView.getRefreshableView();
        switch (mIndex) {
            case 0:
                mAdapter = new TabFragmentAdapter(getContext(), mData0);
                getData(Urls.HEADLINE_URL + Urls.HEADLINE_TYPE + page0);

                AbsListView.LayoutParams layoutParams =
                        new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                , ViewGroup.LayoutParams.WRAP_CONTENT);
                View headView = LayoutInflater.from(getContext()).inflate(R.layout.head_item, mPullToReFreshListView, false);
                headView.setLayoutParams(layoutParams);
                mViewPager = (ViewPager) headView.findViewById(R.id.head);
                mLinearLayout = (LinearLayout) headView.findViewById(R.id.headContainer);
                mHeadText = (TextView) headView.findViewById(R.id.headText);
                initViewPager();
                mListView.addHeaderView(headView);
                break;
            case 1:
                mAdapter = new TabFragmentAdapter(getContext(), mData1);
                getData(Urls.BASE_URL + Urls.CYCLOPEDIA_TYPE + page1);
                break;
            case 2:
                mAdapter = new TabFragmentAdapter(getContext(), mData2);
                getData(Urls.BASE_URL + Urls.CONSULT_TYPE + page2);
                break;
            case 3:
                mAdapter = new TabFragmentAdapter(getContext(), mData3);
                getData(Urls.BASE_URL + Urls.OPERATE_TYPE + page3);
                break;
            case 4:
                mAdapter = new TabFragmentAdapter(getContext(), mData4);
                getData(Urls.BASE_URL + Urls.DATA_TYPE + page4);
                break;
        }
        mPullToReFreshListView.setAdapter(mAdapter);
        mPullToReFreshListView.setEmptyView(empty);
        mPullToReFreshListView.setOnItemClickListener(this);

    }

    private LoadingLayoutProxy mLoadingLayoutProxy;
    private void initLoadingLayoutProxy() {
        mLoadingLayoutProxy= (LoadingLayoutProxy) mPullToReFreshListView.getLoadingLayoutProxy();
        String lastTime =  DateUtils.formatDateTime(getContext(), System.currentTimeMillis()
                , DateUtils.FORMAT_ABBREV_TIME | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE);
        mLoadingLayoutProxy.setLastUpdatedLabel("上次更新时间："+lastTime);

    }

    private void initViewPager() {
        mPagerAdapter = new MyPagerAdapter(mImgData);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        initViewPagerData();
        initPageView();
        mHandler.sendEmptyMessageDelayed(2, 3000);
        initViewPagerListener();

        //解决ViewPager嵌套ViewPager滑动冲突问题
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((MainActivity) getContext()).getViewPager().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private int lastPosition = 0;

    private void initPageView() {
        mCount = new ImageView[3];
        for (int i = 0; i < 3; i++) {
            mCount[i] = (ImageView) mLinearLayout.getChildAt(i);
            mCount[i].setEnabled(true);
        }
        mCount[0].setEnabled(false);

    }

    private void initViewPagerListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mCount[position % 3].setEnabled(false);
                mCount[lastPosition % 3].setEnabled(true);
                mHeadText.setText(headStr.get(position % 3));
                lastPosition = position;
                mHandler.sendMessage(Message.obtain(mHandler, 3, position, 0));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING://手正在进行拖拽
                        mHandler.sendEmptyMessage(4);
                        break;
                }
            }
        });
    }

    private void initViewPagerData() {
        if (NetworkUtils.isConnected(getContext())) {
            HttpUtils.loadByte(Urls.HEADERIMAGE_URL, mHandler);
        }else {
            byte[] bytes = SdCardUtils.getBytes(getContext().getExternalCacheDir().getAbsolutePath(),
                    Urls.HEADERIMAGE_URL.replaceAll("/", "").replaceAll("/", ""));
            if (bytes!=null) {
                getHeadView(bytes);
            }else {
                return;
            }
        }
    }

    private void getHeadView(byte[] bytes) {

        HeaderImage headerImage = JSON.parseObject(new String(bytes), HeaderImage.class);
        List<HeaderImage.DataBean> data = headerImage.getData();
        for (int i = 0; i < data.size(); i++) {
            final String imagePath = data.get(i).getImage();
            final ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            headStr.add(data.get(i).getName());
            Bitmap cacheBitmap=getCache(imagePath);
            if (cacheBitmap!=null){
                imageView.setImageBitmap(cacheBitmap);
            }else {//网络下载
                new MyAsyncTask(new DataCallback() {
                    @Override
                    public void callback(byte[] bytes) {
                        if (bytes != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                            //存入缓存和磁盘
                            mLruCache.put(imagePath.replaceAll("/",""),bitmap);
                            String root=getContext().getExternalCacheDir().getAbsolutePath();
                            SdCardUtils.saveToFile(bytes,root,imagePath.replaceAll("/",""));
                        }
                    }
                }).execute(imagePath);
            }
            mImgData.add(imageView);
            mPagerAdapter.notifyDataSetChanged();
        }
        mHeadText.setText(headStr.get(0));
    }

    private Bitmap getCache(String imgPath) {
        imgPath=imgPath.replaceAll("/","");
        Bitmap bitmap=mLruCache.get(imgPath);
        if (bitmap!=null){
//            Log.d("flag", "----------------->getCache: 缓存获取的数据" );
            return bitmap;
        }else {
            //没有缓存 从磁盘获取
            String root=getContext().getExternalCacheDir().getAbsolutePath();
            byte[] bytes = SdCardUtils.getBytes(root, imgPath);
            if (bytes!=null){
//                Log.d("flag", "----------------->getCache: " +bytes.length);
                Bitmap bmSd = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //存到缓存
                mLruCache.put(imgPath,bmSd);
//                Log.d("flag", "----------------->getCache: 磁盘获取的数据" );
                return bmSd;
            }
        }
        return null;
    }

    private void initListener() {
        mPullToReFreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                switch (mIndex) {
                    case 0:
                        page0 = 1;
                        mData0.clear();
                        getData(Urls.HEADLINE_URL + Urls.HEADLINE_TYPE + page0);
                        break;
                    case 1:
                        page1 = 1;
                        mData1.clear();
                        getData(Urls.BASE_URL + Urls.CYCLOPEDIA_TYPE + page1);
                        break;
                    case 2:
                        page2 = 1;
                        mData2.clear();
                        getData(Urls.BASE_URL + Urls.CONSULT_TYPE + page2);
                        break;
                    case 3:
                        page3 = 1;
                        mData3.clear();
                        getData(Urls.BASE_URL + Urls.OPERATE_TYPE + page3);
                        break;
                    case 4:
                        page4 = 1;
                        mData4.clear();
                        getData(Urls.BASE_URL + Urls.DATA_TYPE + page4);
                        break;
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                switch (mIndex) {
                    case 0:
                        page0++;
                        getData(Urls.HEADLINE_URL + Urls.HEADLINE_TYPE + page0);
                        break;
                    case 1:
                        page1++;
                        getData(Urls.BASE_URL + Urls.CYCLOPEDIA_TYPE + page1);
                        break;
                    case 2:
                        page2++;
                        getData(Urls.BASE_URL + Urls.CONSULT_TYPE + page2);
                        break;
                    case 3:
                        page3++;
                        getData(Urls.BASE_URL + Urls.OPERATE_TYPE + page3);
                        break;
                    case 4:
                        page4++;
                        getData(Urls.BASE_URL + Urls.DATA_TYPE + page4);
                        break;
                }
            }
        });

        mBtnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.smoothScrollToPosition(0);
            }
        });
    }

    private void getData(final String path) {
        final String root = getContext().getExternalCacheDir().getAbsolutePath();
        final String fileName = path.replaceAll("/", "").replaceAll(":", "");
        if (NetworkUtils.isConnected(getContext())) {
            //从网络获取数据
            new MyAsyncTask(new DataCallback() {
                @Override
                public void callback(byte[] bytes) {
                    SdCardUtils.saveToFile(bytes, root, fileName);
                    TeasMessage teasMessage = JSON.parseObject(new String(bytes), TeasMessage.class);
                    List<TeasMessage.DataBean> data = teasMessage.getData();

                    switch (mIndex) {
                        case 0:
                            mData0.addAll(data);
                            break;
                        case 1:
                            mData1.addAll(data);
                            break;
                        case 2:
                            mData2.addAll(data);
                            break;
                        case 3:
                            mData3.addAll(data);
                            break;
                        case 4:
                            mData4.addAll(data);
                            break;
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mPullToReFreshListView.isRefreshing()) {
                        mPullToReFreshListView.onRefreshComplete();
                    }
                }
            }).execute(path);
        } else {
            Toast.makeText(getContext(), "当前无网络连接", Toast.LENGTH_SHORT).show();
            byte[] bytes = SdCardUtils.getBytes(root, fileName);
            if (bytes != null) {
                TeasMessage teasMessage = JSON.parseObject(new String(bytes), TeasMessage.class);
                List<TeasMessage.DataBean> data = teasMessage.getData();

                switch (mIndex) {
                    case 0:
                        mData0.addAll(data);
                        break;
                    case 1:
                        mData1.addAll(data);
                        break;
                    case 2:
                        mData2.addAll(data);
                        break;
                    case 3:
                        mData3.addAll(data);
                        break;
                    case 4:
                        mData4.addAll(data);
                        break;
                }
                mAdapter.notifyDataSetChanged();
                if (mPullToReFreshListView.isRefreshing()) {
                    mPullToReFreshListView.onRefreshComplete();
                }
            }
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        ContentValues values = new ContentValues();
        switch (mIndex) {
            case 0:
                TeasMessage.DataBean dataBean = mData0.get(position - 2);
                String jsonString = JSON.toJSONString(dataBean);
                intent.putExtra("json", jsonString);
                String id0 = dataBean.getId();
                String contentPath = Urls.CONTENT_URL + id0;
                intent.putExtra("path", contentPath);
                db.delete("history", "id=" + id0, null);
                values.put("id", id0);
                values.put("title", dataBean.getTitle());
                values.put("source", dataBean.getSource());
                values.put("wap_thumb", dataBean.getWap_thumb());
                values.put("create_time", dataBean.getCreate_time());
                values.put("nickname", dataBean.getNickname());

                break;
            case 1:
                TeasMessage.DataBean dataBean1 = mData1.get(position - 1);
                String id1 = dataBean1.getId();
                String contentPath1 = Urls.CONTENT_URL + id1;
                String jsonString1 = JSON.toJSONString(dataBean1);
                intent.putExtra("json", jsonString1);
                intent.putExtra("path", contentPath1);
                db.delete("history", "id=" + id1, null);
                values.put("id", id1);
                values.put("title", dataBean1.getTitle());
                values.put("source", dataBean1.getSource());
                values.put("wap_thumb", dataBean1.getWap_thumb());
                values.put("create_time", dataBean1.getCreate_time());
                values.put("nickname", dataBean1.getNickname());

                break;
            case 2:
                TeasMessage.DataBean dataBean2 = mData2.get(position - 1);
                String id2 = dataBean2.getId();
                String contentPath2 = Urls.CONTENT_URL + id2;
                String jsonString2 = JSON.toJSONString(dataBean2);
                intent.putExtra("json", jsonString2);
                intent.putExtra("path", contentPath2);
                db.delete("history", "id=" + id2, null);
                values.put("id", id2);
                values.put("title", dataBean2.getTitle());
                values.put("source", dataBean2.getSource());
                values.put("wap_thumb", dataBean2.getWap_thumb());
                values.put("create_time", dataBean2.getCreate_time());
                values.put("nickname", dataBean2.getNickname());
                break;
            case 3:
                TeasMessage.DataBean dataBean3 = mData3.get(position - 1);
                String id3 = dataBean3.getId();
                String contentPath3 = Urls.CONTENT_URL + id3;
                String jsonString3 = JSON.toJSONString(dataBean3);
                intent.putExtra("json", jsonString3);
                intent.putExtra("path", contentPath3);
                db.delete("history", "id=" + id3, null);
                values.put("id", id3);
                values.put("title", dataBean3.getTitle());
                values.put("source", dataBean3.getSource());
                values.put("wap_thumb", dataBean3.getWap_thumb());
                values.put("create_time", dataBean3.getCreate_time());
                values.put("nickname", dataBean3.getNickname());
                break;
            case 4:
                TeasMessage.DataBean dataBean4 = mData4.get(position - 1);
                String id4 = dataBean4.getId();
                String contentPath4 = Urls.CONTENT_URL + id4;
                String jsonString4 = JSON.toJSONString(dataBean4);
                intent.putExtra("json", jsonString4);
                intent.putExtra("path", contentPath4);
                db.delete("history", "id=" + id4, null);
                values.put("id", id4);
                values.put("title", dataBean4.getTitle());
                values.put("source", dataBean4.getSource());
                values.put("wap_thumb", dataBean4.getWap_thumb());
                values.put("create_time", dataBean4.getCreate_time());
                values.put("nickname", dataBean4.getNickname());
                break;
        }
        db.insert("history", null, values);
        getContext().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
