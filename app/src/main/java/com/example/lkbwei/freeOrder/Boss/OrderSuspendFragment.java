package com.example.lkbwei.freeOrder.Boss;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTable;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Customer.OrderItem;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.OvalProgress;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OrderSuspendFragment extends Fragment {

    public static final int GET_DATA = 0;
    public static final int TAKE_ORDER = 1;
    public static final int HAVE_SOLVE = 2;
    public static final int ORDER_STATUS_DOWN = 3;//恢复订单状态时使用

    protected String mBoss;
    protected boolean showButton;
    protected TextView mTip;
    protected Handler mHandler;
    protected OrderTableLab mOrderTableLab;

    private List<SuspendItem> mList;
    private RecyclerView mRecyclerView;
    private OrderMenuAdapter mAdapter;
    private Lab mLab;
    private TextView emptyView;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.order_suspend,container,false);
        mTip = (TextView)view.findViewById(R.id.card_text);
        setTip();
        mBoss = BasePreferences.getUserName(getActivity());
        mOrderTableLab = OrderTableLab.getOrderTableLab(getActivity());
        mLab = Lab.getLab(getActivity());
        initHandler();

        getData();
        mList = new ArrayList<>();

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

        setShowSolvedButton();
        return view;
    }

    protected void setTip(){
        mTip.setText("今天的订单");
    }

    protected void setShowSolvedButton(){
        showButton = true;
    }

    public void getData(){
        OvalProgress.startAnimator(getActivity());
        mOrderTableLab.getBossData(mBoss,OrderTableLab.HAVE_SUBMIT,mHandler,GET_DATA);
    }

    public void handleData(List<OrderTable> list){
        if (list.size() == 0){
            OvalProgress.endAnimator();
            Toast.makeText(getActivity(),"暂无新订单",Toast.LENGTH_SHORT).show();
            emptyView.setVisibility(View.VISIBLE);
        }else {
            HashSet<String> set = new HashSet<>();
            for(int i = 0;i < list.size();i ++){
                set.add(list.get(i).getCustomer());
            }

            List<String> nameList = new ArrayList<>(set);
            for(int i = 0;i < nameList.size();i ++){
                SuspendItem item = new SuspendItem();
                item.setCustomer(nameList.get(i));
                List<OrderItem> orderItemList = new ArrayList<>();
                for (int j = 0;j < list.size();j ++){
                    OrderTable table = list.get(j);
                    OrderItem tmpItem = null;
                    if (table.getCustomer().equals(nameList.get(i))){
                        item.setDate(table.getCreatedAt());
                        tmpItem = new OrderItem();
                        tmpItem.setName(table.getFoodName());
                        tmpItem.setPrice(table.getPrice() + "");
                        orderItemList.add(tmpItem);
                    }

                }
                item.setList(orderItemList);
                mList.add(item);
                mOrderTableLab.updateData(mBoss,nameList.get(i),
                        OrderTableLab.HAVE_SUBMIT,mHandler,TAKE_ORDER,true);
            }

            mAdapter.setList(mList);
            mAdapter.notifyDataSetChanged();
            OvalProgress.endAnimator();
            emptyView.setVisibility(View.INVISIBLE);
        }

    }

    private class OrderMenuAdapter extends RecyclerView.Adapter<OrderMenuHolder>{
        private List<SuspendItem> mList;
        private Context mContext;

        public OrderMenuAdapter(Context context,List<SuspendItem> list){
            mList = list;
            mContext = context;
        }

        @Override
        public OrderMenuHolder onCreateViewHolder(ViewGroup parent, int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.boss_suspend,null);
            return new OrderMenuHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderMenuHolder holder, int position){
            SuspendItem item = mList.get(position);
            holder.mCustomer.setText(item.getCustomer());
            List<OrderItem> list = item.getList();
            double price = 0;
            for (int i = 0;i < list.size();i ++){
                OrderItem orderItem = list.get(i);
                price = price + Double.valueOf(orderItem.getPrice());
            }
            holder.mPrice.setText(price + "");
            holder.mAdapter.setList(item.getList());
            holder.mAdapter.notifyDataSetChanged();
            holder.mDate.setText(item.getDate());

        }

        public void setList(List<SuspendItem> list){
            mList = list;
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }
    }

    private class OrderMenuHolder extends RecyclerView.ViewHolder{
        private TextView mCustomer;
        private RecyclerView mRecyclerView;
        private TextView mPrice;
        private ToggleButton mSolve;
        private OrderListAdapter mAdapter;
        private List<OrderItem> mOrderItemList;
        private TextView mDate;

        public OrderMenuHolder(View itemView){
            super(itemView);
            mCustomer = (TextView)itemView.findViewById(R.id.card_customer_name);
            mPrice = (TextView)itemView.findViewById(R.id.card_price);
            mRecyclerView = (RecyclerView)itemView.findViewById(R.id.card_list);
            mSolve = (ToggleButton)itemView.findViewById(R.id.card_solve);
            mOrderItemList = new ArrayList<>();
            mDate = (TextView)itemView.findViewById(R.id.card_date);

            mAdapter = new OrderListAdapter(getActivity(),mOrderItemList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            if (showButton){
                mSolve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSolve.setEnabled(false);
                        mOrderTableLab.updateData(mBoss,mCustomer.getText().toString(),
                                OrderTableLab.TAKE_ORDER,mHandler,HAVE_SOLVE,true);
                    }
                });
            }else {
                mSolve.setVisibility(View.GONE);
            }

        }
    }

    private class OrderListAdapter extends  RecyclerView.Adapter<OrderListHolder>{
        private Context mContext;
        private List<OrderItem> mList;

        public OrderListAdapter(Context context,List<OrderItem> list){
            mContext = context;
            mList = list;
        }

        @Override
        public OrderListHolder onCreateViewHolder(ViewGroup parent,int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.order_item,null);
            return new OrderListHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderListHolder holder,int position){
            OrderItem item = mList.get(position);
            holder.mFoodName.setText(item.getName());
            holder.mPrice.setText(item.getPrice());
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }

        private void setList(List<OrderItem> list){
            mList = list;
        }

    }

    private class OrderListHolder extends RecyclerView.ViewHolder{
        private TextView mFoodName;
        private TextView mPrice;

        public OrderListHolder(View itemView) {
            super(itemView);

            mFoodName = (TextView)itemView.findViewById(R.id.order_item_name);
            mPrice = (TextView)itemView.findViewById(R.id.order_item_price);
        }
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case GET_DATA:
                        List<OrderTable> list = (List<OrderTable>)msg.obj;
                        handleData(list);
                        break;
                    case TAKE_ORDER:

                        break;
                    case HAVE_SOLVE:
                        Toast.makeText(getActivity(),"接单成功",Toast.LENGTH_SHORT).show();
                        break;
                    case ORDER_STATUS_DOWN:
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mOrderTableLab.updateData(mBoss, null,OrderTableLab.TAKE_ORDER,
                mHandler,ORDER_STATUS_DOWN,false);
    }
}
