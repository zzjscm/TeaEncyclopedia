package com.zz.teaencyclopedia.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zz.teaencyclopedia.R;
import com.zz.teaencyclopedia.beans.TeasMessage;
import com.zz.teaencyclopedia.cache.MyLruCache;
import com.zz.teaencyclopedia.interfaces.DataCallback;
import com.zz.teaencyclopedia.net.MyAsyncTask;
import com.zz.teaencyclopedia.utils.SdCardUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */
public class TabFragmentAdapter extends BaseAdapter {

    private Context mContext;
    private List<TeasMessage.DataBean> mData;
    private MyLruCache mLruCache;
    public TabFragmentAdapter(Context context, List<TeasMessage.DataBean> data) {
        this.mContext=context;
        this.mData=data;
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        mLruCache=new MyLruCache(maxSize);
    }

    @Override
    public int getCount() {
        return mData!=null?mData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret=null;
        ViewHolder holder=null;
        if (convertView!=null){
            ret=convertView;
            holder= (ViewHolder) ret.getTag();
        }else {
            ret= LayoutInflater.from(mContext).inflate(R.layout.list_item,parent,false);
            holder=new ViewHolder();
            holder.title= (TextView) ret.findViewById(R.id.title);
            holder.source= (TextView) ret.findViewById(R.id.source);
            holder.create_time= (TextView) ret.findViewById(R.id.create_time);
            holder.nickname= (TextView) ret.findViewById(R.id.nickname);
            holder.img= (ImageView) ret.findViewById(R.id.img);
            ret.setTag(holder);
        }

        TeasMessage.DataBean dataBean = mData.get(position);
        holder.title.setText(dataBean.getTitle());
        holder.source.setText(dataBean.getSource());
        holder.create_time.setText(dataBean.getCreate_time());
        holder.nickname.setText(dataBean.getNickname());
        holder.img.setImageBitmap(Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_4444));
        final ViewHolder finalHolder=holder;
        final String imgPath = dataBean.getWap_thumb();
        holder.img.setTag(imgPath);
        //三级缓存处理数据
        //先从缓存中加载
        Bitmap  cacheBitmap=getCache(imgPath);
        if (cacheBitmap!=null){
            holder.img.setImageBitmap(cacheBitmap);
        }else {
            //网络获取
            new MyAsyncTask(new DataCallback() {
                @Override
                public void callback(byte[] bytes) {
                    if (bytes != null && imgPath.equals((String) (finalHolder.img.getTag()))) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        finalHolder.img.setImageBitmap(bitmap);
                        //存入缓存和磁盘
                        mLruCache.put(imgPath.replaceAll("/",""),bitmap);
                        String root=mContext.getExternalCacheDir().getAbsolutePath();
                        SdCardUtils.saveToFile(bytes,root,imgPath.replaceAll("/",""));
                    }
                }
            }).execute(imgPath);
        }
        return ret;
    }

    private Bitmap getCache(String imgPath) {
        imgPath=imgPath.replaceAll("/","");
        Bitmap bitmap=mLruCache.get(imgPath);
        if (bitmap!=null){
            Log.d("flag", "----------------->getCache: 缓存获取的数据" );
            return bitmap;
        }else {
            //没有缓存 从磁盘获取
            String root=mContext.getExternalCacheDir().getAbsolutePath();
            byte[] bytes = SdCardUtils.getBytes(root, imgPath);
            if (bytes!=null){
                Log.d("flag", "----------------->getCache: " +bytes.length);
                Bitmap bmSd = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                //存到缓存
                mLruCache.put(imgPath,bmSd);
                Log.d("flag", "----------------->getCache: 磁盘获取的数据" );
                return bmSd;
            }
        }
        return null;
    }


    private static class ViewHolder{
        private TextView title,source,create_time,nickname;
        private ImageView img;
    }
}
