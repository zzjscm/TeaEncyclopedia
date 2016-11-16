package com.zz.teaencyclopedia.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/11/12.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> data;
    public MyFragmentPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> data) {
        super(supportFragmentManager);
        this.data=data;
    }

    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

}
