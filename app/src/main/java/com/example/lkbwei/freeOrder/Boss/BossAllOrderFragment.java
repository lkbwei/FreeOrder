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

    /**
     * 获取历史订单
     * @since 1.0
     */
    @Override
    public void getData(){
        OvalProgress.startAnimator(getActivity());
        mOrderTableLab.getBossHaveSolvedOrder(mBoss, OrderTableLab.HAVE_SOLVE,mHandler,GET_DATA);
    }

    /**
     * 设置标题
     * @since 1.0
     */
    @Override
    protected void setTip(){
        mTip.setText("所有订单");
    }

    /**
     * 是否显示按钮
     * @since 1.0
     */
    @Override
    protected void setShowSolvedButton(){
        showButton = false;
    }
}
