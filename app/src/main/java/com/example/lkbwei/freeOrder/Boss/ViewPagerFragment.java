package com.example.lkbwei.freeOrder.Boss;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lkbwei.freeOrder.DataBase.GoodsData;
import com.example.lkbwei.freeOrder.R;
import com.squareup.picasso.Picasso;

/**
 * Created by lkbwei on 2017/3/2.
 */

public class ViewPagerFragment extends Fragment {
    private ImageView mViewPagerImage = null;
    private static final String ARG = "arg";
    private String mUrl;


    public static Fragment newInstance(int position){
        Fragment fragment = new ViewPagerFragment();
        Bundle argument = new Bundle();
        argument.putInt(ARG,position);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        final int position = getArguments().getInt(ARG);
        View view = inflater.inflate(R.layout.viewpager_image,container,false);
        mViewPagerImage = (ImageView) view.findViewById(R.id.image_viewpager);
        if (MenuListFragment.mCoverDatas != null){
            boolean hasMatched = false;
            for (int i = 0;i < MenuListFragment.mCoverDatas.size();i++){
                GoodsData data = MenuListFragment.mCoverDatas.get(i);
                if (data.getCoverNum() == position){
                    mUrl = data.getImageUrl();
                    hasMatched = true;
                    break;
                }
            }
            if (!hasMatched){
                mUrl = null;
            }
        }

        if (mUrl != null){
            Picasso.with(getActivity())
                    .load(mUrl)
                    .into(mViewPagerImage);
        }else {
            Log.i("VIEW", position + "");
            mViewPagerImage.setImageResource(R.drawable.empty_image);
        }

        mViewPagerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuListFragment.sHandler.obtainMessage(MenuListFragment.CLICK_COVER).sendToTarget();
            }
        });

        return view;
    }


}
