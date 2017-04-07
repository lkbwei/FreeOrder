package com.example.lkbwei.freeOrder.Customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.DbOperate;
import com.example.lkbwei.freeOrder.Tools.LoadImage;
import com.example.lkbwei.freeOrder.Login.LoginActivity;
import com.example.lkbwei.freeOrder.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lkbwei on 2017/3/15.
 */

public class MyMenuFragment extends Fragment {

    public static final int IMAGE_CUT_CODE = 0;
    public static final int CAMERA_CODE = 1;
    public static final int IMAGE_CUT_CAMERA_CODE = 2;
    public static final int UPLOAD_IMAGE = 3;
    public static final int GET_IMAGE = 4;

    protected TextView mTip;
    protected RelativeLayout mEvaluate;
    protected RelativeLayout mAllOrder;
    protected RelativeLayout mQuit;
    protected ImageView editImage;
    protected TextView mUserName;
    protected Handler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.third_view,container,false);
        mEvaluate = (RelativeLayout)view.findViewById(R.id.not_evaluate);
        mAllOrder = (RelativeLayout)view.findViewById(R.id.all_order);
        mQuit = (RelativeLayout)view.findViewById(R.id.quit);
        editImage = (ImageView)view.findViewById(R.id.third_image);
        mUserName = (TextView)view.findViewById(R.id.third_name);
        mTip = (TextView)view.findViewById(R.id.third_tip);
        initUserName();
        doEditImage();
        initHandler();
        getUserImage();

        mEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvaluateFragment fragment = new EvaluateFragment();
                getFragmentManager().beginTransaction()
                        .hide(MyMenuFragment.this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(android.R.id.tabcontent,fragment)
                        .addToBackStack(EvaluateFragment.TAG)
                        .commit();
            }
        });

        mAllOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllOrderFragment fragment = new AllOrderFragment();
                getFragmentManager().beginTransaction()
                        .hide(MyMenuFragment.this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(android.R.id.tabcontent,fragment)
                        .addToBackStack(AllOrderFragment.TAG)
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

    /**
     * 获取用户头像
     * @since 1.0
     */
    public void getUserImage(){
        DbOperate.getUserImage(BasePreferences.getUserName(getActivity()),
                mHandler,GET_IMAGE);
    }

    /**
     * 更新用户头像
     * @param url Url
     * @since 1.0
     */
    public void updateImageFromNet(String url){
        Picasso.with(getActivity())
                .load(url)
                .into(editImage);
    }

    /**
     * 设置用户名
     * @since 1.0
     */
    public void initUserName(){
        String name = BasePreferences.getUserName(getActivity());
        mUserName.setText(name);
    }

    /**
     * 上传头像
     * @param file 本地图片地址
     * @since 1.0
     */
    public void uploadImage(File file){
        String name = BasePreferences.getUserName(getActivity());
        DbOperate.setUserImage(name,file,mHandler,UPLOAD_IMAGE);
    }

    /**
     * 设置头像操作
     * @since 1.0
     */
    public void doEditImage(){
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.load_image,null);
                TextView local = (TextView)view.findViewById(R.id.local);
                TextView camera = (TextView)view.findViewById(R.id.camera);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder.setView(view).create();
                dialog.show();
                local.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Uri uri = LoadImage.getUserImage(getActivity());
                        Intent intent = LoadImage.getImage(getActivity(),editImage,uri);
                        startActivityForResult(intent, IMAGE_CUT_CODE);
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Intent intent = LoadImage.openCamera(getActivity());
                        startActivityForResult(intent,CAMERA_CODE);
                    }
                });
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode != RESULT_OK){

        }else {
            if (requestCode == IMAGE_CUT_CODE){
                updateImage();

            }else if (requestCode == CAMERA_CODE){
                Uri uri = LoadImage.getUserImage(getActivity());
                Intent cutIntent = LoadImage.getFromCamera(getActivity(),editImage,uri);
                startActivityForResult(cutIntent,IMAGE_CUT_CAMERA_CODE);
            }else if (requestCode == IMAGE_CUT_CAMERA_CODE){
                updateImage();
            }
        }
    }

    /**
     * 更新头像
     * @since 1.0
     */
    public void updateImage(){
        File file = LoadImage.getUserImageFile(getActivity());
        uploadImage(file);
        Picasso.with(getActivity())
                .load(file)
                .into(editImage);
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UPLOAD_IMAGE:
                        Toast.makeText(getActivity(),"上传头像成功",Toast.LENGTH_SHORT).show();
                        break;
                    case GET_IMAGE:
                        if (msg.obj != null){
                            String url = (String)msg.obj;
                            updateImageFromNet(url);
                        }

                        break;
                }
            }
        };
    }
}
