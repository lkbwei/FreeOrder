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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.OrderTable;
import com.example.lkbwei.freeOrder.DataBase.OrderTableLab;
import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.Tools.OvalProgress;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/15.
 */

public class EvaluateFragment extends Fragment {
    public static final String TAG = "evaluateFragment";

    private List<EvaluateItem> mList;
    private Handler mHandler;
    private EvaluateAdapter adapter;
    private RecyclerView mRecyclerView;
    private OrderTableLab mOrderTableLab;
    private String customer;
    private String boss;
    private Lab mLab;


    public static final int GET_DATA = 0;
    public static final int INSERT_COMMENT = 1;
    public static final int UPDATE_STATUS = 2;


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
        initHandler();
        mOrderTableLab = OrderTableLab.getmOrderTableLab(getActivity());
        mLab = Lab.getLab(getActivity());

        getData();

        adapter = new EvaluateAdapter(getActivity(),mList);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.evaluate_recycler_view);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));

        return view;
    }

    public void getData(){
        OvalProgress.startAnimator(getActivity());
        mOrderTableLab.getEvaluateData(customer,OrderTableLab.HAVE_SOLVE,mHandler,GET_DATA);
    }


    public void updateRecyclerView(List<OrderTable> list){

        if (list.size() == 0){
            OvalProgress.endAnimator();
            Toast.makeText(getActivity(),"没有待评论的订单",Toast.LENGTH_SHORT).show();
        }else {

            for (int i = 0;i < list.size();i ++){
                OrderTable data = list.get(i);
                EvaluateItem item = new EvaluateItem();
                item.setName(data.getFoodName());
                item.setPrice(data.getPrice() + "");
                mList.add(item);
            }
            adapter.setList(mList);
            adapter.notifyDataSetChanged();
            OvalProgress.endAnimator();
        }
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case GET_DATA:
                        updateRecyclerView((List<OrderTable>)msg.obj);

                        break;
                    case INSERT_COMMENT:
                        Toast.makeText(getActivity(),"评论提交成功",Toast.LENGTH_SHORT).show();

                        break;
                    case UPDATE_STATUS:
                        break;
                }
            }
        };
    }

    private class EvaluateAdapter extends RecyclerView.Adapter<EvaluateHolder>{
        private List<EvaluateItem> mList;
        private Context mContext;

        public EvaluateAdapter(Context context,List<EvaluateItem> list){
            mContext = context;
            mList = list;
        }

        @Override
        public EvaluateHolder onCreateViewHolder(ViewGroup parent,int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.evaluate_item,null);

            return new EvaluateHolder(view);
        }

        @Override
        public void onBindViewHolder(EvaluateHolder holder,int position){
            EvaluateItem item = mList.get(position);
            holder.mFoodName.setText(item.getName());
            holder.mPrice.setText(item.getPrice());
        }

        @Override
        public int getItemCount(){
            return mList.size();
        }

        public void setList(List<EvaluateItem> list){
            mList = list;
        }
    }

    private class EvaluateHolder extends RecyclerView.ViewHolder{
        private TextView mFoodName;
        private ToggleButton mSubmit;
        private EditText mEditText;
        private TextView mPrice;
        private String tips;
        private ToggleButton mPositive;
        private ToggleButton mNegative;

        public EvaluateHolder(View itemView){
            super(itemView);
            mFoodName = (TextView)itemView.findViewById(R.id.evaluate_food_name);
            mSubmit = (ToggleButton)itemView.findViewById(R.id.evaluate_submit);
            mEditText = (EditText)itemView.findViewById(R.id.evaluate_content);
            mPrice = (TextView)itemView.findViewById(R.id.card_price);
            tips = getActivity().getResources().getString(R.string.evaluate_tip);
            mPositive = (ToggleButton)itemView.findViewById(R.id.evaluate_positive);
            mNegative = (ToggleButton)itemView.findViewById(R.id.evaluate_negative);

            mPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPositive.setEnabled(false);
                    mNegative.setEnabled(false);
                    mLab.updateGoodOrBad(boss,mFoodName.getText().toString(),"Positive");
                }
            });

            mNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPositive.setEnabled(false);
                    mNegative.setEnabled(false);
                    mLab.updateGoodOrBad(boss,mFoodName.getText().toString(),"Negative");
                }
            });

            mSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mEditText.getText().equals(tips) && !mEditText.getText().equals("")){
                        mEditText.setEnabled(false);
                        mSubmit.setEnabled(false);
                        mOrderTableLab.insertComment(boss,customer,mFoodName.getText().toString(),
                                mEditText.getText().toString(),OrderTableLab.HAVE_SOLVE,mHandler,INSERT_COMMENT);

                    }else {
                        Toast.makeText(getActivity(),"评论内容不能为空",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }


    private class EvaluateItem{
        private String mName;
        private String mPrice;

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
