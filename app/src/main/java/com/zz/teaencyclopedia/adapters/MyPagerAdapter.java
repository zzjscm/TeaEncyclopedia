package com.zz.teaencyclopedia.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/13.
 */
public class MyPagerAdapter extends PagerAdapter {

    private List<ImageView> data;
    public MyPagerAdapter(List<ImageView> imgData) {
        data=imgData;
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View ret = data.get(position);
//        Log.d("flag", "----------------->instantiateItem: imgview" +data.size());
        if (ret.getParent()==null){
            container.addView(ret);
        }
        return ret;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
            container.removeView(data.get(position));
    }

}
