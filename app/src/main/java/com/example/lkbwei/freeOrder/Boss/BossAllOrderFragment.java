package com.example.lkbwei.freeOrder.Boss;

import android.util.Log;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Tools.OvalProgress;

/**
 * Created by lkbwei on 2017/3/16.
 */

public class BossAllOrderFragment extends OrderSuspendFragment {
    public static final String TAG = "BossAllOrderFragment";

    @Override
    public void getData(){
        Log.e("loadData","^^^^^^^^^^^^"  + BasePreferences.getUserName(getActivity()));
        OvalProgress.startAnimator(getActivity());
        mOrderTableLab.getBossHaveSolvedOrder(mBoss, OrderTableLab.HAVE_SOLVE,mHandler,GET_DATA);
    }

    @Override
    protected void setTip(){
        mTip.setText("所有订单");
    }

    @Override
    protected void setShowSolvedButton(){
        showButton = false;
    }
}
