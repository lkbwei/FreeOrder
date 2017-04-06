package com.example.lkbwei.freeOrder.Tools;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lkbwei.freeOrder.Boss.ViewPagerFragment;

/**
 * Created by lkbwei on 2017/3/3.
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private  Context mContext;
    public static final int PAGER_NUMBER = 5;


    public BaseFragmentPagerAdapter(FragmentManager manager){
        super(manager);
    }


    @Override
    public Fragment getItem(int position) {
        return ViewPagerFragment.newInstance(position);

    }

    @Override
    public int getCount() {
            return PAGER_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return null;
    }



}
