package com.example.lkbwei.freeOrder.Customer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lkbwei.freeOrder.R;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderMenuContentFragment extends Fragment {
    private int mPage;

    public static final String PAGE_NUM = "pageNum";

    public static OrderMenuContentFragment newInstance(int page){
        OrderMenuContentFragment fragment = new OrderMenuContentFragment();
        Bundle argument = new Bundle();
        argument.putInt(PAGE_NUM,page);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(PAGE_NUM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.third_view,container,false);

        return view;
    }
}
