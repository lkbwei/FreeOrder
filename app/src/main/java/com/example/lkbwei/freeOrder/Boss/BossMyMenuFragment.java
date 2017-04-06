package com.example.lkbwei.freeOrder.Boss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.Customer.MyMenuFragment;
import com.example.lkbwei.freeOrder.Login.LoginActivity;
import com.example.lkbwei.freeOrder.R;

/**
 * Created by lkbwei on 2017/3/16.
 */

public class BossMyMenuFragment extends MyMenuFragment {
    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.third_view,container,false);
        mEvaluate = (RelativeLayout)view.findViewById(R.id.not_evaluate);
        mAllOrder = (RelativeLayout)view.findViewById(R.id.all_order);
        mQuit = (RelativeLayout)view.findViewById(R.id.quit);
        editImage = (ImageView)view.findViewById(R.id.third_image);
        mUserName = (TextView)view.findViewById(R.id.third_name);
        mTip = (TextView)view.findViewById(R.id.third_tip);
        mTextView = (TextView)view.findViewById(R.id.first_text);
        mTextView.setText("所有订单");
        mTip.setText("大大BOSS");

        initUserName();
        doEditImage();
        initHandler();
        getUserImage();

        mAllOrder.setVisibility(View.GONE);

        mEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BossAllOrderFragment fragment = new BossAllOrderFragment();
                getFragmentManager().beginTransaction()
                        .hide(BossMyMenuFragment.this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(android.R.id.tabcontent,fragment)
                        .addToBackStack(BossAllOrderFragment.TAG)
                        .commit();
            }
        });

        mQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BasePreferences.setLoginStatus(getActivity(),false);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
