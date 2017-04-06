package com.example.lkbwei.freeOrder.Boss;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.Tools.BaseFragmentPagerAdapter;
import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.GoodsData;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.LoadImage;
import com.example.lkbwei.freeOrder.Tools.MenuRecyclerViewAdapter;
import com.example.lkbwei.freeOrder.R;
import com.kogitune.activity_transition.ActivityTransitionLauncher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by lkbwei on 2017/3/2.
 */

public class MenuListFragment extends Fragment {
    private static final String TAG = "MenuListFragment";

    public static List<GoodsData> mCoverDatas;//封面数据
    public static List<GoodsData> mCurrentClassifyDatas;//当前页面所选类型的数据
    public static Lab sLab;
    public static String currentRestaurant;//保存的是商家的名字，不是餐厅名
    protected ProgressDialog mDialog;
    protected String mCurrentClassify;

    public static final int TIME = 2000;
    public static ViewPager mViewPager;
    private static int pagerNum;

    public static final int IMAGE_CUT_CODE = 0;
    public static final int CAMERA_CODE = 1;
    public static final int IMAGE_CUT_CAMERA_CODE = 2;
    public static final int REQUEST_CODE = 3;
    public static final int CLASSIFY_REQUEST_CODE = 4;
    public static final int LOADCLASSIGY = 5;
    public static final int LOADDATA = 6;
    public static final int COVERDATA = 7;
    public static final int SAVE_SUCCESS = 8;
    public static final int DELETE = 9;
    public static final int OPEN_NOT_COVER_DESCRIPTION = 10;
    public static final int UPDATE_DATA = 11;
    public static final int CLICK_COVER = 12;

    public static Handler sHandler;
    protected Spinner mSpinner;
    protected FloatingActionButton mFab;
    private Button mCoverButton;
    public static List<String> classify = new ArrayList<>();
    protected ArrayAdapter<String> mClassifyArrayAdapter;
    protected RecyclerView mRecyclerView;
    protected MenuRecyclerViewAdapter mRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.boss_list,container,false);
        sLab = Lab.getLab(getActivity());
        initHandler();
        currentRestaurant = BasePreferences.getUserName(getActivity());

        mCurrentClassifyDatas = new ArrayList<>();
        mCoverDatas = new ArrayList<>();

        classify.clear();

        mRecyclerView = (RecyclerView)view.findViewById(R.id.menu_recycler_view);
        mRecyclerAdapter = new MenuRecyclerViewAdapter(
                getActivity(),mCurrentClassifyDatas);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        mViewPager.setAdapter(new BaseFragmentPagerAdapter(getFragmentManager()));
        viewPagerChange();

        mClassifyArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, classify);
        mClassifyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = (Spinner)view.findViewById(R.id.spinner);
        mSpinner.setAdapter(mClassifyArrayAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentClassify = classify.get(position);
                Log.e("loadData","**************" + mCurrentClassify);
                Log.e("loadData","^^^^^^^^^^^^"  + currentRestaurant);
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.description_add_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodDescriptionFragment fragment = FoodDescriptionFragment.newInstance(0,false,false);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .hide(MenuListFragment.this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.third_id,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        final Button edit_button = (Button)view.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassifyFragment fragment = ClassifyFragment.newInstance(classify);
                fragment.setTargetFragment(MenuListFragment.this,CLASSIFY_REQUEST_CODE);
                getFragmentManager().beginTransaction()
                        .hide(MenuListFragment.this)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.second_id,fragment,ClassifyFragment.TAG)
                        .addToBackStack(fragment.getTag())
                        .commit();
            }
        });


        mCoverButton = (Button) view.findViewById(R.id.spinner_image);
        mCoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCover();
            }
        });


        updateUI();

        return view;
    }

    public void updateUI(){
        mDialog = showProgress(getActivity(),"数据拼命加载中...");
        mDialog.show();
        loadClassify(currentRestaurant);

    }

    public void initMenuFragment(){
        updateClassify();
        if (mCurrentClassify == null || !classify.contains(mCurrentClassify)) {
            if (classify.size() != 0)
            mCurrentClassify = classify.get(mSpinner.getFirstVisiblePosition());
        }
        loadData();
        getCoverData();
    }

    public void loadData(){
       sLab.getGoodsList(new String[]{"User","classify"},new Object[]{currentRestaurant,mCurrentClassify},
               null,0,null,0, sHandler,LOADDATA);
    }

    public void updateCurrentData(){
        mRecyclerAdapter.setGoodsDatas(mCurrentClassifyDatas);
        mRecyclerAdapter.notifyDataSetChanged();
        mDialog.cancel();
    }

    public void getCoverData(){
        sLab.doConditionQuery(new String[]{"User","CoverNum"},new Object[]{currentRestaurant,-1},
                true, sHandler,COVERDATA);
    }

    public void updateCover(){

    }

    public void loadClassify(String user){
        sLab.getGoodsList(new String[]{"User"},new Object[]{user},
                null,0,null,0, sHandler,LOADCLASSIGY);
    }


    public void updateClassify(){
        mClassifyArrayAdapter.notifyDataSetChanged();
        //getCoverData();
    }

    public void openNotCoverDescription(int position){
        Intent intent = FoodPagerActivity.newIntent(getActivity(),position,false,true);
        ActivityTransitionLauncher.with(getActivity()).from(mFab).launch(intent);
        //startActivity(intent);
    }

    public void clickCover(){
        pagerNum = mViewPager.getCurrentItem();
        Intent intent = FoodPagerActivity.newIntent(getActivity(),pagerNum,true,false);
        startActivityForResult(intent,REQUEST_CODE);
    }


    public static ProgressDialog showProgress(Context context, String tip){

        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(tip);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    public void viewPagerChange(){
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BaseMenuActivity.sHandler.sendEmptyMessage(BaseMenuActivity.START);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                boolean changed = false;
                switch (state) {
                    case ViewPager.SCROLL_STATE_SETTLING:
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:

                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        BaseMenuActivity.sHandler.sendEmptyMessage(BaseMenuActivity.STOP);
                    default:
                        break;
                }

            }
        });

        mViewPager.setCurrentItem(0);
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                int position = mViewPager.getCurrentItem();
                mViewPager.setCurrentItem(position % BaseFragmentPagerAdapter.PAGER_NUMBER + 1);
            }
        },TIME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode != RESULT_OK){
            Log.e(TAG,"ActivityResultError");
        }else {
            if (requestCode == REQUEST_CODE) {
                int code = intent.getIntExtra(FoodDescriptionFragment.SEND_CODE,-1);
                if (code == IMAGE_CUT_CODE) {
                    UpdateViewpager();
                } else if (code == CAMERA_CODE) {
                    Uri uri = LoadImage.getTempFileDir(getActivity());
                    Intent cutIntent = LoadImage.getFromCamera(getActivity(), mViewPager,uri);
                    startActivityForResult(cutIntent, IMAGE_CUT_CAMERA_CODE);
                } else if (code == IMAGE_CUT_CAMERA_CODE) {
                    UpdateViewpager();
                }
            }else if (requestCode == CLASSIFY_REQUEST_CODE){
                classify = intent.getStringArrayListExtra(ClassifyFragment.CLASSIFY_DATA);
                mClassifyArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    public void UpdateViewpager(){
            BaseFragmentPagerAdapter adapter =  (BaseFragmentPagerAdapter) mViewPager.getAdapter();
            adapter.notifyDataSetChanged();
    }

    public void initHandler(){
        sHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case LOADCLASSIGY:
                        List<GoodsData> tempList = (List<GoodsData>)msg.obj;

                        if (tempList != null) {
                            for (int i = 0; i < tempList.size();i ++){
                                GoodsData data = tempList.get(i);
                                classify.add(data.getClassify());
                            }
                            HashSet<String> set = new HashSet<>(classify);
                            classify.clear();
                            classify.addAll(set);
                            initMenuFragment();
                        }else {
                            Toast.makeText(getActivity(),"无数据",Toast.LENGTH_LONG).show();
                            mDialog.cancel();
                        }
                        break;
                    case LOADDATA:
                        mCurrentClassifyDatas = (List<GoodsData>)msg.obj;
                        Log.i("BMOB",mCurrentClassifyDatas.toString());
                        updateCurrentData();
                        break;
                    case COVERDATA:
                        mCoverDatas = (List<GoodsData>)msg.obj;
                        updateCover();
                        break;
                    case SAVE_SUCCESS:
                        if (getFragmentManager().findFragmentById(R.id.third_id) != null) {
                            getFragmentManager().popBackStack(null, 0);
                        }
                        updateUI();
                        break;
                    case DELETE:
                        Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                        updateUI();
                        break;
                    case OPEN_NOT_COVER_DESCRIPTION:
                        int position = (int)msg.obj;
                        openNotCoverDescription(position);
                        break;
                    case UPDATE_DATA:
                        updateUI();
                        break;
                    case CLICK_COVER:
                        clickCover();
                        break;

                }
            }
        };
    }

}
