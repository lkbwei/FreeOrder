package com.example.lkbwei.freeOrder.Login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.DbOperate;
import com.example.lkbwei.freeOrder.Tools.NetReceiver;
import com.example.lkbwei.freeOrder.R;
import com.example.lkbwei.freeOrder.Tools.UseProgressBar;
import com.squareup.picasso.Picasso;

/**
 * Created by lkbwei on 2017/3/4.
 */

public class WelcomeFragment extends Fragment {
    private static final String SUCCESS = "登录成功";

    private Handler mHandler;
    private ImageView mUserImageView;
    private TextView mUserName;
    private UseProgressBar progressBar;

    private static final int GET_IMAGE = 0;

    public interface WelcomeListener{
        void success();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public static Fragment newInstance(){
        return new WelcomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.first_view,container,false);
        mUserImageView = (ImageView)view.findViewById(R.id.head_image);
        mUserName = (TextView)view.findViewById(R.id.user_name);
        initHandler();
        initUserName();

        progressBar= new UseProgressBar(view);
        final boolean conn = NetReceiver.checkNetConn(getActivity());

        progressBar.getView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                v.removeOnLayoutChangeListener(this);

                if (conn){
                    getUserImage();
                }else {
                    Toast.makeText(getActivity(),"无可用网络",Toast.LENGTH_LONG).show();

                }

            }
        });

        return view;
    }

    public void getUserImage(){
        DbOperate.getUserImage(BasePreferences.getUserName(getActivity()),
                mHandler,GET_IMAGE);
    }

    public void updateImageFromNet(String url){
        Picasso.with(getActivity())
                .load(url)
                .into(mUserImageView);
        initSuccess();
    }

    public void initSuccess(){
        if (getActivity() instanceof WelcomeListener){
            progressBar.end(SUCCESS);
            ((WelcomeListener) getActivity()).success();
        }
    }

    public void initUserName(){
        String name = BasePreferences.getUserName(getActivity());
        mUserName.setText(name);
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case GET_IMAGE:
                        if (msg.obj != null){
                            String url = (String)msg.obj;
                            updateImageFromNet(url);
                        }else {
                            initSuccess();
                        }

                        break;
                }
            }
        };
    }
}
