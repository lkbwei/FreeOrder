package com.example.lkbwei.freeOrder.Boss;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.DataBase.Lab;
import com.example.lkbwei.freeOrder.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/9.
 */

public class ClassifyFragment extends Fragment {
    private List<String> mList;
    private List<String> markList;
    private BaseRecyclerAdapter mAdapter;
    private Button addButton;
    private RecyclerView mRecyclerView;
    private TextView mSure;
    private Lab mLab;
    private Handler mHandler;
    private ProgressDialog mdialog;
    private int count = 0;
    private int allCount ;

    public static final String TAG = "ClassifyFragment";
    public static final String KEY = "com.example.lkbwei.ClassifyFragment";
    public static final String CLASSIFY_DATA = "com.example.lkbwei.ClassifyFragment.List";
    public static final int DELETE = 0;
    public static final int SAVE_SUCCESS = 1;

    public static ClassifyFragment newInstance(List<String> list){
        ClassifyFragment fragment = new ClassifyFragment();
        Bundle argument = new Bundle();
        argument.putStringArrayList(KEY,(ArrayList<String>)list);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mList = getArguments().getStringArrayList(KEY);
        markList = new ArrayList<>(getArguments().getStringArrayList(KEY));
        //markList = getArguments().getStringArrayList(KEY);
        mLab = Lab.getLab(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view= inflater.inflate(R.layout.classify_list,null);
        initHandler();
        mdialog = createHorizontalDialog(getActivity());
        mRecyclerView = (RecyclerView)view.findViewById(R.id.classify_recycler_view);
        addButton = (Button) view.findViewById(R.id.classify_add_button);
        mSure = (TextView)view.findViewById(R.id.classify_sure);

        mAdapter = new BaseRecyclerAdapter(mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> oldList = new ArrayList<String>();
                List<Object> newList = new ArrayList<Object>();
                oldList.add("User");
                newList.add(MenuListFragment.currentRestaurant);
                for (int i = 0;i < markList.size();i++){
                    if (!markList.get(i).equals(mList.get(i))){
                        oldList.add(markList.get(i));
                        newList.add(mList.get(i));
                    }
                }
                if (oldList.size() > 1){
                    allCount = oldList.size() - 1;
                    String[] where = oldList.toArray(new String[] {});
                    Object[] values = newList.toArray();
                    mLab.updateBatchGoodsData(getActivity(), where,values,mHandler,SAVE_SUCCESS);
                    mdialog.setMessage("更新中，请稍后...");
                    mdialog.show();
                }else {
                    quit();
                }
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.add("");
                mAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void quit(){
        //sendResult(Activity.RESULT_OK, mList);

        MenuListFragment.sHandler.obtainMessage(MenuListFragment.UPDATE_DATA).sendToTarget();
        getFragmentManager().popBackStack(null, 0);
    }

    public void sendResult(int resultCode,List<String> list){
        if (getTargetFragment() == null){
            Log.e(TAG,"getTargetFragment is null");
        }else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(CLASSIFY_DATA, (ArrayList<String>) list);

            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        }

    }

    private class BaseRecyclerAdapter extends RecyclerView.Adapter<Holder>{
        List<String> mStrings;

        public BaseRecyclerAdapter(List<String> s){
            mStrings = s;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.types_view,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            holder.mEditText.setText(mStrings.get(position));

        }

        @Override
        public int getItemCount() {
            return mStrings.size();
        }

    }

    private class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private EditText mEditText;
        private Button mDeleteButton;

        public Holder(View itemView) {
            super(itemView);
            mEditText = (EditText)itemView.findViewById(R.id.edit_type);
            mEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    mList.set(getAdapterPosition(),s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            mDeleteButton = (Button)itemView.findViewById(R.id.delete_button);
            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("目录下的商品将会删除，是否继续？");
            builder.setCancelable(false);
            builder.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mList.remove(mEditText.getText().toString());
                    markList.remove(mEditText.getText().toString());
                    mLab.deleteGoodsData(getActivity(),new String[]{"User","classify"},
                            new  Object[]{MenuListFragment.currentRestaurant,mEditText.getText().toString()},
                            mHandler,DELETE,false);
                    mdialog.setMessage("删除中，请稍后...");
                    mdialog.show();
                    mAdapter.notifyDataSetChanged();
                }
            }).setNegativeButton("不了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).create().show();
        }
    }

    public void initHandler(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                switch (msg.what){
                    case DELETE:
                        mAdapter.notifyDataSetChanged();
                        mdialog.cancel();
                        break;
                    case SAVE_SUCCESS:
                        count++;
                        if (count == allCount){
                            mdialog.setMessage("更新完成");
                            mdialog.cancel();
                            allCount = 0;
                            count = 0;
                            quit();
                        }
                        break;
                }
            }
        };
    }

    public ProgressDialog createHorizontalDialog(Context context){
        ProgressDialog dialog;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
