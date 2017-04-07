package com.example.lkbwei.freeOrder.Boss;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.GoodsData;
import com.example.lkbwei.freeOrder.DataBase.OrderTable;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Tools.CommentAdapter;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.LoadImage;
import com.example.lkbwei.freeOrder.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lkbwei on 2017/3/7.
 */

public class FoodDescriptionFragment extends Fragment {
    private ImageView mImageView;//食物图
    private Button editImage;//编辑图片按钮
    private EditText editFoodName;//编辑菜名
    private TextView mSaleVolume;
    private TextView mPositive;
    private TextView mNegative;
    private EditText price;//编辑价格
    private EditText description;//编辑描述
    private ToggleButton mStock;//是否有货
    private FloatingActionButton saveFab;//保存
    private Spinner mClassifySpinner;
    private String mClassifySelect;
    private int currentSelectNum;
    private boolean isCover;
    private boolean isViewPagerData;
    private boolean imageHasChanged = false;
    public Handler mHandler;
    private Lab mLab;
    private OrderTableLab mOrderTableLab;
    private boolean isNewGood = true;
    private String MarkFoodName = "";
    private ImageButton mGoodButton;
    private ImageButton mBadButton;
    public RecyclerView mCommentRecyclerView;
    public CommentAdapter mAdapter;
    public List<CommentAdapter.CommentItem> mCommentList;

    private ArrayAdapter<String> mArrayAdapter;
    private List<String> mClassifyList;

    public static final String ARG = "arg";
    public static final String TAG = "FoodDescriptionFragment";
    public static final String SEND_CODE = "sendFromFoodDescription";
    public static final String IS_COVER = "isCover";
    public static final String IS_VIEW_PAGER_DATA = "isviewpagerdata";

    public static final int FOOD_NAME_WHAT = 0;
    public static final int UPDATE_FOOD_NAME = 1;
    public static final int UPDATE_COMMENT = 2;

    public static FoodDescriptionFragment newInstance(int CurrentSelect,boolean isCover,boolean isViewPagerData){
        FoodDescriptionFragment fragment = new FoodDescriptionFragment();
        Bundle argument = new Bundle();
        argument.putInt(ARG,CurrentSelect);
        argument.putBoolean(IS_COVER,isCover);
        argument.putBoolean(IS_VIEW_PAGER_DATA,isViewPagerData);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        currentSelectNum = getArguments().getInt(ARG);
        isCover = getArguments().getBoolean(IS_COVER);
        isViewPagerData = getArguments().getBoolean(IS_VIEW_PAGER_DATA);
        mLab = Lab.getLab(getActivity());
        if (MenuListFragment.classify == null){
            mClassifyList = FoodPagerActivity.sList;
        }else {
            mClassifyList = MenuListFragment.classify;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.food_description,container,false);

        mImageView = (ImageView)view.findViewById(R.id.description_image);
        editImage = (Button)view.findViewById(R.id.description_edit_image);
        editFoodName = (EditText)view.findViewById(R.id.description_name);
        price = (EditText)view.findViewById(R.id.description_price);
        description = (EditText)view.findViewById(R.id.description_explain);
        saveFab = (FloatingActionButton)view.findViewById(R.id.description_save_fab);
        mStock = (ToggleButton) view.findViewById(R.id.description_switch_button);
        mClassifySpinner = (Spinner)view.findViewById(R.id.description_spinner);
        mSaleVolume = (TextView)view.findViewById(R.id.description_sale_num);
        mPositive = (TextView)view.findViewById(R.id.description_good_num);
        mNegative = (TextView)view.findViewById(R.id.description_bad_num);
        mGoodButton = (ImageButton) view.findViewById(R.id.description_good);
        mBadButton = (ImageButton)view.findViewById(R.id.description_bad);

        mOrderTableLab = OrderTableLab.getmOrderTableLab(getActivity());
        mCommentRecyclerView = (RecyclerView)view.findViewById(R.id.description_evaluate);
        initRecycler();

        initHandler();

        if (BaseMenuActivity.identity == BaseMenuActivity.CUSTOMER){
            mStock.setClickable(false);
            editFoodName.setFocusable(false);
            editFoodName.setFocusableInTouchMode(false);
            editFoodName.setBackground(null);
            price.setFocusable(false);
            price.setFocusableInTouchMode(false);
            price.setBackground(null);
            description.setFocusable(false);
            description.setFocusableInTouchMode(false);
            description.setBackground(null);
            description.setTextColor(Color.GRAY);
            description.setTextSize(15);
            mClassifySpinner.setEnabled(false);
            editImage.setVisibility(View.GONE);
            saveFab.setVisibility(View.GONE);
        }

        doEditImage();
        doClassifySpinner();


            if (isCover){
                if (MenuListFragment.mCoverDatas != null && MenuListFragment.mCoverDatas.size() != 0){
                    for (int i = 0;i < MenuListFragment.mCoverDatas.size();i++){
                        GoodsData data = MenuListFragment.mCoverDatas.get(i);
                        if (currentSelectNum == data.getCoverNum()){
                            initData(data);
                        }
                    }
                }

            }else if(isViewPagerData){
                if (MenuListFragment.mCurrentClassifyDatas != null &&
                        MenuListFragment.mCurrentClassifyDatas.size() != 0){
                    GoodsData data = MenuListFragment.mCurrentClassifyDatas.get(currentSelectNum);
                    initData(data);
                }

            }



        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewGood){
                    doSaveVerify();
                }
                else {
                    doUpdateVerify();
                }

            }
        });

        return view;
    }

    public void initRecycler(){
        mCommentList = new ArrayList<>();
        mAdapter = new CommentAdapter(getActivity(),mCommentList);
        mCommentRecyclerView.setAdapter(mAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCommentRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
    }

    public void getComment(String food){
        String boss = null;
        if (BaseMenuActivity.identity == BaseMenuActivity.BOSS){
            boss = BasePreferences.getUserName(getActivity());
        }else if(BaseMenuActivity.identity == BaseMenuActivity.CUSTOMER){
            boss = BasePreferences.getRecentRestaurant(getActivity());
        }
        mOrderTableLab.getComment(boss,food,mHandler,UPDATE_COMMENT);
    }

    public void updateComment(List<OrderTable> list){
        List<CommentAdapter.CommentItem> itemList = new ArrayList<>();
        for (int i = 0;i < list.size();i++){
            OrderTable data = list.get(i);
            CommentAdapter.CommentItem item = new CommentAdapter.CommentItem();
            item.setComment(data.getComment());
            item.setCustomer(data.getCustomer());
            itemList.add(item);
        }
        mAdapter.setList(itemList);
        mAdapter.notifyDataSetChanged();
    }

    public void initData(GoodsData data){
        Picasso.with(getActivity())
                .load(data.getImageUrl())
                .into(mImageView);
        editFoodName.setText(data.getFoodName());
        mSaleVolume.setText(data.getSaleVolume().toString());
        mPositive.setText(data.getPositive().toString());
        mNegative.setText(data.getNegative().toString());
        price.setText(data.getPrice().toString());
        description.setText(data.getDescription());
        mStock.setChecked(data.getStock());
        mClassifySpinner.setSelection(mClassifyList.indexOf(data.getClassify()));
        isNewGood = false;
        MarkFoodName = data.getFoodName();

        getComment(MarkFoodName);
    }

    public void doEditImage(){
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //currentSelectNum = mViewPager.getCurrentItem();
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.load_image,null);
                TextView local = (TextView)view.findViewById(R.id.local);
                TextView camera = (TextView)view.findViewById(R.id.camera);
                ImageView tempView = (ImageView)view.findViewById(R.id.get_image);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final AlertDialog dialog = builder.setView(view).create();
                dialog.show();
                local.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Uri uri = LoadImage.getTempFileDir(getActivity());
                        Intent intent = LoadImage.getImage(getActivity(),mImageView,uri);
                        startActivityForResult(intent, MenuListFragment.IMAGE_CUT_CODE);
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        Intent intent = LoadImage.openCamera(getActivity());
                        startActivityForResult(intent,MenuListFragment.CAMERA_CODE);
                    }
                });
            }
        });

    }


    public void doClassifySpinner(){

        mArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,
                mClassifyList);
        mArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mClassifySpinner.setAdapter(mArrayAdapter);
        mClassifySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mClassifySelect = mClassifyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void doSaveVerify(){
        //正则表达式判断价格是否为数字
        Pattern pattern = Pattern.compile("\\d{1,8}(\\.\\d{1,2})?$");
        Matcher isNum = pattern.matcher(price.getText().toString());

        if (!imageHasChanged){
            if (isNewGood) {
                Toast.makeText(getActivity(), "图片不能为空", Toast.LENGTH_SHORT).show();
            }
        }else if(!isNum.matches()){
            Toast.makeText(getActivity(),"价格格式不正确",Toast.LENGTH_SHORT).show();
        }else {
            mLab.getGoodsList(new String[]{"User","FoodName"},new Object[]{
                    BasePreferences.getUserName(getActivity()),editFoodName.getText().toString()},
                    null,0,null,0,mHandler,FOOD_NAME_WHAT);
        }
    }

    public void doUpdateVerify(){
        Pattern pattern = Pattern.compile("\\d{1,8}(\\.\\d{1,2})?$");
        Matcher isNum = pattern.matcher(price.getText().toString());
        if(!isNum.matches()){
            Toast.makeText(getActivity(),"价格格式不正确",Toast.LENGTH_SHORT).show();
        }else if (!editFoodName.getText().toString().equals(MarkFoodName)){
            mLab.getGoodsList(new String[]{"User","FoodName"},new Object[]{
                            BasePreferences.getUserName(getActivity()),editFoodName.getText().toString()},
                    null,0,null,0,mHandler,UPDATE_FOOD_NAME);
        }
        else {
            update();
        }
    }

    public void save(){
        String user = BasePreferences.getUserName(getActivity());
        String foodName = editFoodName.getText().toString();
        String imageUrl = LoadImage.getImageFileString(getActivity());
        Integer saleVolume = Integer.valueOf(mSaleVolume.getText().toString());
        Double goodPrice = Double.valueOf(price.getText().toString());
        Integer positive = Integer.valueOf(mPositive.getText().toString());
        Integer negative = Integer.valueOf(mNegative.getText().toString());
        String goodDescription = description.getText().toString();
        Boolean stock = mStock.isChecked();
        String classify = mClassifySelect;
        Integer coverNum = -1;
        if (isCover) {
            coverNum = currentSelectNum;
        } else {
            coverNum = -1;
        }
        mLab.insertGoodsData(getActivity(), user, foodName, imageUrl, saleVolume, goodPrice, positive, negative,
                goodDescription, stock, classify, coverNum);
    }

    public void update(){
        String user = BasePreferences.getUserName(getActivity());
        String foodName = editFoodName.getText().toString();
        Double goodPrice = Double.valueOf(price.getText().toString());
        String goodDescription = description.getText().toString();
        Boolean stock = mStock.isChecked();
        String classify = mClassifySelect;
        Integer coverNum = -1;
        if (isCover) {
            coverNum = currentSelectNum;
        } else {
            coverNum = -1;
        }
        if (imageHasChanged){
            String imageUrl = LoadImage.getImageFileString(getActivity());
            if (foodName.equals(MarkFoodName)) {
                mLab.updateGoodsData(getActivity(),MarkFoodName,null, new String[]{"User", "Price", "Description",
                        "Stock", "classify", "CoverNum", "ImageUrl"}, new Object[]{user, goodPrice, goodDescription,
                        stock, classify, coverNum, imageUrl});
            }else {
                mLab.updateGoodsData(getActivity(),MarkFoodName,foodName, new String[]{"User", "Price", "Description",
                        "Stock", "classify", "CoverNum", "ImageUrl"}, new Object[]{user, goodPrice, goodDescription,
                        stock, classify, coverNum, imageUrl});
            }
        }else {
            if (foodName.equals(MarkFoodName)){
                mLab.updateGoodsData(getActivity(),MarkFoodName,null,new String[]{"User","Price","Description",
                        "Stock","classify","CoverNum"},new Object[]{user,goodPrice,goodDescription,
                        stock,classify,coverNum});
            }else {
                mLab.updateGoodsData(getActivity(),MarkFoodName,foodName,new String[]{"User","Price","Description",
                        "Stock","classify","CoverNum"},new Object[]{user,goodPrice,goodDescription,
                        stock,classify,coverNum});
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode != RESULT_OK){
            Log.e(TAG,"ActivityResultError");
        }else {
            if (requestCode == MenuListFragment.IMAGE_CUT_CODE){
                if (isCover) {
                    sendResult(RESULT_OK, MenuListFragment.IMAGE_CUT_CODE);
                }
                updateImage();

            }else if (requestCode == MenuListFragment.CAMERA_CODE){
                Uri uri = LoadImage.getTempFileDir(getActivity());
                Intent cutIntent = LoadImage.getFromCamera(getActivity(),mImageView,uri);
                startActivityForResult(cutIntent,MenuListFragment.IMAGE_CUT_CAMERA_CODE);

            }else if (requestCode == MenuListFragment.IMAGE_CUT_CAMERA_CODE){
                if (isCover) {
                    sendResult(RESULT_OK, MenuListFragment.IMAGE_CUT_CAMERA_CODE);
                }
                updateImage();
            }
        }
    }

    public void updateImage(){
        Bitmap bitmap = null;
        Uri uri = LoadImage.getTempFileDir(getActivity());
        try {
            bitmap = BitmapFactory.decodeStream(
                    getActivity().getContentResolver().openInputStream(uri));
            mImageView.setImageBitmap(bitmap);
            imageHasChanged = true;

        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"图片没找到");
        }
    }

    //更新MenuListFragment
    public void sendResult(int resultCode, int code){

        Intent intent = new Intent();
        intent.putExtra(SEND_CODE,code);

        getActivity().setResult(resultCode,intent);
    }


    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case FOOD_NAME_WHAT:
                        List<GoodsData> list = (List<GoodsData>)msg.obj;
                        if (list != null && list.size() != 0){
                            Toast.makeText(getActivity(),"商品名已存在",Toast.LENGTH_LONG).show();
                        }else {
                            save();
                        }
                        break;
                    case UPDATE_FOOD_NAME:
                        List<GoodsData> list1 = (List<GoodsData>)msg.obj;
                        if (list1 != null && list1.size() != 0){
                            Toast.makeText(getActivity(),"商品名已存在",Toast.LENGTH_LONG).show();
                        }else {
                            update();
                        }
                        break;
                    case UPDATE_COMMENT:
                        updateComment((List<OrderTable>)msg.obj);
                        break;

                }
            }
        };
    }
}
