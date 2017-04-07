package com.example.lkbwei.freeOrder.Customer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArraySet;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lkbwei.freeOrder.Tools.BaseFragmentPagerAdapter;
import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.DbOperate;
import com.example.lkbwei.freeOrder.DataBase.GoodsData;
import com.example.lkbwei.freeOrder.DataBase.LoginTable;
import com.example.lkbwei.freeOrder.Boss.BaseMenuActivity;
import com.example.lkbwei.freeOrder.Boss.MenuListFragment;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.MenuRecyclerViewAdapter;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lkbwei on 2017/3/13.
 */

public class CustomerMenuListFragment extends MenuListFragment {

    public static String restaurant;
    public static Handler privateHandler;
    public static final int UP_OR_DOWN = 0;
    public static final int ADD_ANIMATION = 1;
    public static final int ADD = 2;
    public static final int GET_RESTAURANT = 3;
    public static final int GET_RESTAURANTlIST = 5;
    public static final int UPDATE_RESTAURANT = 4;
    public static final String PRICE_SELECT = "Price";
    public static final String VOLUME_SELECT = "SaleVolume";
    public static int PRICE_IS = 0;
    public static int VOLUME_IS = 0;

    private ToggleButton mPriceSelect;
    private ToggleButton mSaleSelect;
    private RecyclerView mOrderRecyclerView;
    private OrderRecyclerAdapter mAdapter;
    private TextView mOrderPrice;
    private List<OrderItem> mOrderList;
    private LinearLayout mLinearLayout;
    private AutoCompleteTextView mAutoCompleteTextView;
    private ArrayAdapter<String> mAutoAdapter;
    private List<String> restaurantList;
    private boolean hasOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.customer_list,container,false);
        mAutoCompleteTextView = (AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.setText(restaurant);
        restaurantList = new ArrayList<String>();

        currentRestaurant = BasePreferences.getRecentRestaurant(getActivity());
        if (getActivity() instanceof BaseMenuActivity){
            mOrderList = ((BaseMenuActivity) getActivity()).getOrderItemList();
        }

        initPrivateHandler();
        getRestaurantList();

        sLab = Lab.getLab(getActivity());
        initHandler();

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
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFab = (FloatingActionButton) view.findViewById(R.id.description_add_fab);
        mFab.requestFocusFromTouch();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasOpen){
                    closeOrderList();
                }else {
                    openOrderList();
                }
            }
        });

        mPriceSelect = (ToggleButton)view.findViewById(R.id.price_select);
        mSaleSelect = (ToggleButton)view.findViewById(R.id.volume_select);
        mOrderRecyclerView = (RecyclerView)view.findViewById(R.id.order_recyclerview);
        mOrderPrice = (TextView)view.findViewById(R.id.order_price);
        mLinearLayout = (LinearLayout)view.findViewById(R.id.linear);
        mLinearLayout.setBackgroundResource(
                android.support.v7.appcompat.R.drawable.abc_dialog_material_background);
        mLinearLayout.getBackground().setAlpha(0);

        mAdapter = new OrderRecyclerAdapter(getActivity(),mOrderList);
        mOrderRecyclerView.setAdapter(mAdapter);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mPriceSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    PRICE_IS = 1;//升序
                }else {
                    PRICE_IS = 0;//无序
                }
                upOrDownSelect();

            }
        });

        mSaleSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        VOLUME_IS = 2;//降序
                    }else {
                        VOLUME_IS = 0;//无序
                    }
                upOrDownSelect();

            }
        });

        getRestaurant();
        updateUI();

        return view;
    }


    /**
     * 获取餐厅列表
     * @since 1.0
     */
    public void getRestaurantList(){
        DbOperate.getAllRestaurant(privateHandler,GET_RESTAURANTlIST);
    }

    /**
     * 初始化AutoCompleteTextView，并设置监听
     * @since 1.0
     */
    public void initAutoCompleteTextView(){

        mAutoAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,
                restaurantList);
        mAutoCompleteTextView.setAdapter(mAutoAdapter);
        mAutoCompleteTextView.setDropDownBackgroundResource(
                android.R.drawable.screen_background_light_transparent);
        mAutoCompleteTextView.setThreshold(1);

        mAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH &&
                        checkRestaurant(restaurantList,mAutoCompleteTextView.getText().toString())){
                    InputMethodManager imm = (InputMethodManager)getActivity().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);

                    sLab.getBoss("restaurant",mAutoCompleteTextView.getText().
                            toString(),privateHandler,UPDATE_RESTAURANT);

                    BasePreferences.setRestaurant(getActivity(),mAutoCompleteTextView.getText().toString());
                }
                return false;
            }
        });

    }

    /**
     * 验证餐厅是否存在
     * @param list 所有餐厅列表
     * @param restaurant 当前选择餐厅
     * @return 是否存在
     * @since 1.0
     */
    public boolean checkRestaurant(List<String> list,String restaurant){
        if (list.contains(restaurant)){
            return true;
        }else {
            Toast.makeText(getActivity(),"所选餐厅暂时未开通",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * 获取当前餐厅
     * @since 1.0
     */
    public void getRestaurant(){
        DbOperate.getRestaurant(BasePreferences.getRecentRestaurant(getActivity()),privateHandler,GET_RESTAURANT);
    }

    /**
     * 动画
     * @since 1.0
     */
    public void addOneAnimation(){
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                mFab,
                "scaleX",
                1f,
                1.2F,
                1f
        );
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                mFab,
                "scaleY",
                1f,
                1.2f,
                1f
        );
        AnimatorSet set = new AnimatorSet();
        set.setDuration(1000);
        set.playTogether(animator1,animator2);

        set.start();
    }

    /**
     * 打开购物车按钮操作
     * @since 1.0
     */
    public void openOrderList(){
        hasOpen = true;
        float price = 0;
        for (int i = 0; i < mOrderList.size();i ++){
            OrderItem item = mOrderList.get(i);
            price = price + Float.valueOf(item.getPrice());
        }
        mLinearLayout.getBackground().setAlpha(180);
        mOrderRecyclerView.setPivotY(0);
        mOrderRecyclerView.setVisibility(View.VISIBLE);
        mOrderPrice.setVisibility(View.VISIBLE);
        mOrderPrice.setText("合计：" + price);

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mOrderRecyclerView,
                "scaleY",
                0,
                1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                mOrderPrice,
                "alpha",
                0,
                1f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(1000);
        set.playTogether(animator,animator1);
        set.start();
        mAdapter.notifyDataSetChanged();

    }

    /**
     * 关闭购物车按钮操作
     * @since 1.0
     */
    public void closeOrderList(){
        hasOpen = false;
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mOrderRecyclerView,
                "scaleY",
                1F,
                0);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                mOrderPrice,
                "alpha",
                1f,
                0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(1000);
        set.playTogether(animator,animator1);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOrderRecyclerView.setVisibility(View.INVISIBLE);
                mOrderPrice.setVisibility(View.INVISIBLE);
                mLinearLayout.getBackground().setAlpha(0);
            }
        });
        set.start();
    }

    /**
     * 添加订单操作
     * @param name 食品名
     * @param price 价格
     * @since 1.0
     */
    public void addOrderData(String name,String price){
        OrderItem item = new OrderItem();
        item.setName(name);
        item.setPrice(price);
        item.setTips(OrderMenuFragment.STATUS1);
        mOrderList.add(item);
        BaseMenuActivity.sHandler.obtainMessage(BaseMenuActivity.SAVE_ORDER_LIST,mOrderList).sendToTarget();
    }

    /**
     * 加载数据
     * @since 1.0
     */
    @Override
    public void loadData(){
        sLab.getGoodsList(new String[]{"User","classify"},new Object[]{currentRestaurant,mCurrentClassify},
                VOLUME_SELECT,VOLUME_IS,PRICE_SELECT,PRICE_IS,sHandler,LOADDATA);
    }

    /**
     * 点赞操作
     * @since 1.0
     */
    public void upOrDownSelect(){
        sLab.getGoodsList(new String[]{"User","classify"},new Object[]{currentRestaurant,mCurrentClassify},
                VOLUME_SELECT,VOLUME_IS,PRICE_SELECT,PRICE_IS, privateHandler,UP_OR_DOWN);
    }

    public void initPrivateHandler(){
        privateHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UP_OR_DOWN:
                        mCurrentClassifyDatas = (List<GoodsData>)msg.obj;
                        updateCurrentData();
                        break;
                    case ADD_ANIMATION:
                        addOneAnimation();
                        break;
                    case ADD:
                        List<String> list = (List<String>)msg.obj;
                        addOrderData(list.get(0),list.get(1));
                        break;
                    case GET_RESTAURANT:
                        restaurant = (String)msg.obj;
                        mAutoCompleteTextView.setText(restaurant);
                        BasePreferences.setRestaurant(getActivity(),restaurant);
                        break;
                    case GET_RESTAURANTlIST:
                        restaurantList = (List<String>)msg.obj;
                        initAutoCompleteTextView();
                        Set<String> set = new ArraySet<>(restaurantList);//存储餐厅列表
                        BasePreferences.setRestaurantList(getActivity(),set);
                        break;
                    case UPDATE_RESTAURANT:
                        List<LoginTable> tablelist = (List<LoginTable>)msg.obj;
                        String boss = tablelist.get(0).getUserName();
                        reSetRestaurant(boss);
                        break;
                }
            }
        };
    }

    /**
     * 重新选择餐厅
     * @param boss 商家名
     * @since 1.0
     */
    public void reSetRestaurant(String boss){
        BasePreferences.setRecentRestaurant(getActivity(),boss);
        currentRestaurant = boss;
        classify.clear();
        mClassifyArrayAdapter.clear();
        mClassifyArrayAdapter.notifyDataSetChanged();
        mCoverDatas.clear();
        mCurrentClassifyDatas.clear();
        updateUI();
    }


    private class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerHolder>{
        private List<OrderItem> mList;
        private Context mContext;

        public OrderRecyclerAdapter(Context context,List<OrderItem> list){
            mContext = context;
            mList = list;
        }

        @Override
        public OrderRecyclerHolder onCreateViewHolder(ViewGroup container,int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.order_item,null);
            return new OrderRecyclerHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderRecyclerHolder holder,int position){
            holder.foodName.setText(mList.get(position).getName());
            holder.foodPrice.setText(mList.get(position).getPrice());
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }
    }


    public class OrderRecyclerHolder extends RecyclerView.ViewHolder{
        private TextView foodName;
        private TextView foodPrice;

        public OrderRecyclerHolder(final View itemView){
            super(itemView);

            foodName = (TextView)itemView.findViewById(R.id.order_item_name);
            foodPrice = (TextView)itemView.findViewById(R.id.order_item_price);

        }

    }

}
