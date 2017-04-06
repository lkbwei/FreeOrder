package com.example.lkbwei.freeOrder.Boss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.widget.TabHost;

import com.example.lkbwei.freeOrder.Tools.BaseFragmentPagerAdapter;
import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Customer.AllOrderFragment;
import com.example.lkbwei.freeOrder.Customer.CustomerMenuListFragment;
import com.example.lkbwei.freeOrder.Customer.EvaluateFragment;
import com.example.lkbwei.freeOrder.Customer.MyMenuFragment;
import com.example.lkbwei.freeOrder.Customer.OrderItem;
import com.example.lkbwei.freeOrder.Customer.OrderMenuFragment;
import com.example.lkbwei.freeOrder.Tools.PollService;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/5.
 */

public class BaseMenuActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;
    private OrderTableLab mOrderTableLab;
    private List<OrderItem> mOrderItemList;

    public static Handler sHandler;
    public static String mCurrentRestaurant;
    public static int identity;

    public static final int BOSS = 1;
    public static final int CUSTOMER = 2;

    public static final int UPDATE = 1;
    public static final int STOP = 2;
    public static final int START = 3;
    public static final int SAVE_ORDER_LIST = 4;

    public static final String IDENTITY = "identity";

    public static Intent newIntent(Context context, int identity){
        Intent intent = new Intent(context,BaseMenuActivity.class);
        intent.putExtra(IDENTITY,identity);
        return intent;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);

        mOrderTableLab = OrderTableLab.getmOrderTableLab(this);
        mOrderItemList = new ArrayList<>();
        identity = getIntent().getIntExtra(IDENTITY,0);
        mCurrentRestaurant = BasePreferences.getRESTAURANT(this);

        mTabHost = (FragmentTabHost)findViewById(R.id.tab_host);
        mTabHost.setup(this,getSupportFragmentManager(), android.R.id.tabcontent);
        if (identity == BOSS) {
            mTabHost.addTab(mTabHost.newTabSpec("首页").setIndicator(null, getResources()
                    .getDrawable(R.drawable.home_tab)), MenuListFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("订单").setIndicator(null, getResources()
                    .getDrawable(R.drawable.order_tab)), OrderSuspendFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("我的").setIndicator(null, getResources()
                    .getDrawable(R.drawable.my_tab)), BossMyMenuFragment.class, null);
        }else if (identity == CUSTOMER){
            mTabHost.addTab(mTabHost.newTabSpec("首页").setIndicator(null, getResources()
                    .getDrawable(R.drawable.home_tab)), CustomerMenuListFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("订单").setIndicator(null, getResources()
                    .getDrawable(R.drawable.order_tab)), OrderMenuFragment.class, null);
            mTabHost.addTab(mTabHost.newTabSpec("我的").setIndicator(null, getResources()
                    .getDrawable(R.drawable.my_tab)), MyMenuFragment.class, null);
        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                getSupportFragmentManager().popBackStackImmediate(
                        ClassifyFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(
                        EvaluateFragment.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(
                        AllOrderFragment.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(
                        BossAllOrderFragment.TAG,FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        handleMsg();

        if (identity == BOSS) {
            Intent intent = PollService.newIntent(this);
            startService(intent);
        }

    }


    public void handleMsg(){
        sHandler = new Handler(){
            @Override
            public void handleMessage(Message mg){
                super.handleMessage(mg);
                switch (mg.what){
                    case UPDATE:
                        int postition = MenuListFragment.mViewPager.getCurrentItem();
                        postition = postition % BaseFragmentPagerAdapter.PAGER_NUMBER + 1;
                        if(postition == BaseFragmentPagerAdapter.PAGER_NUMBER){
                            postition = 0;
                        }
                        MenuListFragment.mViewPager.setCurrentItem(postition);
                        if(sHandler.hasMessages(UPDATE)){
                            sHandler.removeMessages(UPDATE);
                        }
                        //sHandler.sendEmptyMessageDelayed(MenuListFragment.UPDATE,MenuListFragment.TIME);
                        break;
                    case STOP:
                        if(sHandler.hasMessages(UPDATE)){
                            sHandler.removeMessages(UPDATE);
                        }
                        sHandler.sendEmptyMessageDelayed(UPDATE, MenuListFragment.TIME);
                        break;
                    case START:
                        sHandler.sendEmptyMessageDelayed(UPDATE, MenuListFragment.TIME);
                        break;
                    case SAVE_ORDER_LIST:
                        mOrderItemList = (List<OrderItem>)mg.obj;
                        break;
                }
            }
        };
    }

    public List<OrderItem> getOrderItemList(){
        return mOrderItemList;
    }

    @Override
    public void onDestroy(){

        super.onDestroy();
        Intent i = PollService.newIntent(this);
        this.stopService(i);

    }
}
