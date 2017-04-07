package com.example.lkbwei.freeOrder.Customer;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTable;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.Tools.OvalProgress;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/16.
 */

public class AllOrderFragment extends Fragment {

    public static final String TAG = "AllOrderFragment";
    public static final int UPDATE_DATA = 0;

    private List<HistoryItem> mList;
    private String boss;
    private String customer;
    private Handler mHandler;
    private OrderTableLab mOrderTableLab;
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTip;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.evaluate,container,false);
        mList = new ArrayList<>();
        customer = BasePreferences.getUserName(getActivity());
        boss = BasePreferences.getRecentRestaurant(getActivity());
        mTip = (TextView)view.findViewById(R.id.text_tip);
        mTip.setText("我的足迹");
        initHandler();
        mOrderTableLab = OrderTableLab.getOrderTableLab(getActivity());

        getData();

        mHistoryAdapter = new HistoryAdapter(getActivity(),mList);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.evaluate_recycler_view);
        mRecyclerView.setAdapter(mHistoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        return view;
    }

    /**
     * 获取历史数据
     * @since 1.0
     */
    public void getData(){
        OvalProgress.startAnimator(getActivity());
        mOrderTableLab.getAllOrder(customer,mHandler,UPDATE_DATA);
    }

    /**
     * 更新界面
     * @param list 界面数据
     * @since 1.0
     */
    public void updateData(List<OrderTable> list){
        List<HistoryItem> bossList = new ArrayList<>();
        for (int i = 0;i < list.size();i ++){
            OrderTable table = list.get(i);
            HistoryItem item = new HistoryItem();
            item.setName(table.getFoodName());
            item.setPrice(table.getPrice() + "");
            item.setComment(table.getComment());
            item.setDate(table.getCreatedAt());
            item.setRestaurant(table.getRestaurant());
            bossList.add(item);
        }
        mHistoryAdapter.setList(bossList);
        mHistoryAdapter.notifyDataSetChanged();
        OvalProgress.endAnimator();
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case UPDATE_DATA:
                        updateData((List<OrderTable>)msg.obj);
                        break;
                }
            }
        };
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryHolder>{
        private List<HistoryItem> mList;
        private Context mContext;

        public HistoryAdapter(Context context,List<HistoryItem> list){
            mContext = context;
            mList = list;
        }

        @Override
        public HistoryHolder onCreateViewHolder(ViewGroup parent,int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.history_item,null);

            return new HistoryHolder(view);
        }

        @Override
        public void onBindViewHolder(HistoryHolder holder,int position){
            HistoryItem item = mList.get(position);
            holder.mFoodName.setText(item.getName());
            holder.mPrice.setText(item.getPrice());
            holder.mComment.setText(item.getComment());
            holder.date.setText(item.getDate());
            holder.restaurant.setText(item.getRestaurant());
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }

        public void setList(List<HistoryItem> list){
            mList = list;
        }
    }

    private class HistoryHolder extends RecyclerView.ViewHolder{
        private TextView mFoodName;
        private TextView mPrice;
        private TextView mComment;
        private TextView date;
        private TextView restaurant;

        public HistoryHolder(View itemView){
            super(itemView);
            mFoodName = (TextView)itemView.findViewById(R.id.evaluate_food_name);
            mPrice = (TextView)itemView.findViewById(R.id.card_price);
            mComment = (TextView)itemView.findViewById(R.id.evaluate_content);
            date = (TextView)itemView.findViewById(R.id.date);
            restaurant = (TextView)itemView.findViewById(R.id.restaurant);
        }
    }

    private class HistoryItem{
        private String mName;
        private String mPrice;
        private String comment;
        private String date;

        public String getRestaurant() {
            return restaurant;
        }

        public void setRestaurant(String restaurant) {
            this.restaurant = restaurant;
        }

        private String restaurant;

        public String getComment() {
            return comment;
        }



        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public void setComment(String comment) {
            this.comment = comment;

        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            mName = name;
        }

        public String getPrice() {
            return mPrice;
        }

        public void setPrice(String price) {
            mPrice = price;
        }
    }
}
