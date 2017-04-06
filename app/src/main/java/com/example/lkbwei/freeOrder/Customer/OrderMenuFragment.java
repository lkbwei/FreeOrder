package com.example.lkbwei.freeOrder.Customer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Boss.BaseMenuActivity;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.OvalProgress;
import com.example.lkbwei.freeOrder.R;

import java.util.List;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderMenuFragment extends Fragment {
    private List<OrderItem> mList;
    private RecyclerView mRecyclerView;
    private OrderMenuAdapter mAdapter;
    private Button mSubmitButton;
    private Handler mHandler;
    private OrderTableLab mOrderTableLab;
    private Lab mLab;
    private TextView emptyView;

    public static final String STATUS1 = "未提交";
    public static final String STUTUS2 = "商家未处理";
    public static final String STATUS3 = "商家已接单";

    public static final int SUBMIT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.order_suspend,container,false);
        initHandler();
        mOrderTableLab = OrderTableLab.getmOrderTableLab(getActivity());
        mLab = Lab.getLab(getActivity());

        if (getActivity() instanceof BaseMenuActivity){
            mList = ((BaseMenuActivity) getActivity()).getOrderItemList();
        }

        mRecyclerView = (RecyclerView)view.findViewById(R.id.order_recyclerview);
        mAdapter = new OrderMenuAdapter(getActivity(),mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        emptyView = (TextView)view.findViewById(R.id.empty_view);
        if (mList.size() == 0){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.INVISIBLE);
        }

        mSubmitButton = (Button)view.findViewById(R.id.card_submit);
        if (mList.size() != 0){
            mSubmitButton.setVisibility(View.VISIBLE);
        }
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation(v);
                BaseMenuActivity.sHandler.obtainMessage(BaseMenuActivity.SAVE_ORDER_LIST,mList).sendToTarget();
                startDialog();
                submitOrder();
            }
        });

        return view;
    }

    public void submitOrder(){
        String[] boss = new String[mList.size()];
        String[] restarant = new String[mList.size()];
        String[] customer = new String[mList.size()];
        String[] foodName = new String[mList.size()];
        Double[] price = new Double[mList.size()];
        String[] comment  = new String[mList.size()];
        String[] url1 = new String[mList.size()];
        String[] url2 = new String[mList.size()];
        String[] url3 = new String[mList.size()];
        Integer[] status = new Integer[mList.size()];
        for (int i = 0;i < mList.size();i++){
            OrderItem item = mList.get(i);
            boss[i] = BasePreferences.getRecentRestaurant(getActivity());
            restarant[i] = BasePreferences.getRESTAURANT(getActivity());
            customer[i] = BasePreferences.getUserName(getActivity());
            foodName[i] = item.getName();
            price[i] = Double.valueOf(item.getPrice());
            comment[i] = "";
            url1[i] = "";
            url2[i] = "";
            url3[i] = "";
            status[i] = OrderTableLab.HAVE_SUBMIT;
        }
        mOrderTableLab.insertData(boss,restarant,customer,foodName,price,comment,url1,url2,url3,status,
                mHandler,SUBMIT);
        for (int i = 0;i < foodName.length;i ++){
            mLab.updateVolume(boss[0],foodName[i]);
        }

    }

    public void submitSuccess(){
        for (int i = 0;i < mAdapter.getItemCount();i ++){
            mAdapter.notifyItemRemoved(0);
        }
        mList.clear();
        OvalProgress.endAnimator();
        emptyView.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(),"提交订单成功",Toast.LENGTH_SHORT).show();
    }

    public void startDialog(){
        OvalProgress.startAnimator(getActivity());
    }

    public void startAnimation(View v){
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                v,
                "scaleX",
                1f,
                0);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                v,
                "alpha",
                1f,
                0);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(1200);
        set.playTogether(animator,animator1);
        set.setInterpolator(new AccelerateInterpolator());
        set.start();
    }

    private class OrderMenuAdapter extends RecyclerView.Adapter<OrderMenuHolder>{
        private List<OrderItem> mList;
        private Context mContext;

        public OrderMenuAdapter(Context context,List<OrderItem> list){
            mList = list;
            mContext = context;
        }

        @Override
        public OrderMenuHolder onCreateViewHolder(ViewGroup parent,int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.order_card_view,null);
            return new OrderMenuHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderMenuHolder holder,int position){
            OrderItem item = mList.get(position);
            holder.mFoodName.setText(item.getName());
            holder.mFoodPrice.setText(item.getPrice());
            holder.mTips.setText(item.getTips());
        }

        public void setList(List<OrderItem> list){
            mList = list;
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }
    }

    private class OrderMenuHolder extends RecyclerView.ViewHolder{
        private TextView mFoodName;
        private TextView mFoodPrice;
        private TextView mTips;
        private ImageButton mDelet;

        public OrderMenuHolder(View itemView){
            super(itemView);
            mFoodName = (TextView)itemView.findViewById(R.id.card_food_name);
            mFoodPrice = (TextView)itemView.findViewById(R.id.card_price);
            mTips = (TextView)itemView.findViewById(R.id.card_tips);
            mDelet = (ImageButton)itemView.findViewById(R.id.card_solve);

            mDelet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.remove(getPosition());
                    BaseMenuActivity.sHandler.obtainMessage(BaseMenuActivity.SAVE_ORDER_LIST,mList).sendToTarget();
                    mAdapter.notifyItemRemoved(getPosition());
                    //mAdapter.setList(mList);
                    //mAdapter.notifyDataSetChanged();
                    if (mList.size() == 0){
                        startAnimation(mSubmitButton);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case SUBMIT:
                        submitSuccess();
                        break;
                }
            }
        };
    }

}
