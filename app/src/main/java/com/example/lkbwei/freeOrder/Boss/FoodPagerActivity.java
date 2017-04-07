package com.example.lkbwei.freeOrder.Boss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.lkbwei.freeOrder.Tools.BaseFragmentPagerAdapter;
import com.example.lkbwei.freeOrder.R;
import com.kogitune.activity_transition.ActivityTransition;

import java.util.List;

/**
 * Created by lkbwei on 2017/3/7.
 */

public class FoodPagerActivity extends FragmentActivity {

    public ViewPager mViewPager;
    public static List<String> sList;
    public static Handler sHandler;
    public static final String CURRENTNUM = "currentFood";
    public static final String CLASSIFY = "classify";
    public static final String IS_COVER = "cover";
    public static final String IS_VIEW_PAGER_DATA = "viewpagerdata";

    private boolean isCover;
    private boolean isViewPagerData;

    //记录是否为封面
    public static Intent newIntent(Context context,int currentNum,
                                   boolean isCover,boolean isViewPagerData){
        Intent intent = new Intent(context,FoodPagerActivity.class);
        intent.putExtra(IS_COVER,isCover);
        intent.putExtra(IS_VIEW_PAGER_DATA,isViewPagerData);
        intent.putExtra(CURRENTNUM,currentNum);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        ActivityTransition.with(getIntent()).to(findViewById(
                R.id.activity_crime_pager_view_pager)).start(savedInstanceState);

        isCover = getIntent().getBooleanExtra(IS_COVER,false);

        isViewPagerData = getIntent().getBooleanExtra(IS_VIEW_PAGER_DATA,false);

        int currentNum = getIntent().getIntExtra(CURRENTNUM,0);

        sList = getIntent().getStringArrayListExtra(CLASSIFY);

        mViewPager = (ViewPager)findViewById(R.id.activity_crime_pager_view_pager);

        FragmentManager manager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(manager) {
            @Override
            public Fragment getItem(int position) {
                return FoodDescriptionFragment.newInstance(position, isCover, isViewPagerData);
            }

            @Override
            public int getCount() {
                if (isCover) {
                    return BaseFragmentPagerAdapter.PAGER_NUMBER;
                }else if (isViewPagerData){
                    return MenuListFragment.mCurrentClassifyDatas.size();
                }
                else return 1;
            }
        });

        mViewPager.setCurrentItem(currentNum);

    }
}
