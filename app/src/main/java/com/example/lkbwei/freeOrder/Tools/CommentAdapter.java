package com.example.lkbwei.freeOrder.Tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.R;

import java.util.List;

/**
 * Created by lkbwei on 2017/3/16.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.EvaluateHolder> {
        private List<CommentItem> mList;
        private Context mContext;

        public CommentAdapter(Context context,List<CommentItem> list){
            mContext = context;
            mList = list;
        }

        @Override
        public EvaluateHolder onCreateViewHolder(ViewGroup parent, int type){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.comment_view,null);

            return new EvaluateHolder(view);
        }

        @Override
        public void onBindViewHolder(EvaluateHolder holder, int position){
            CommentItem item = mList.get(position);
            holder.mCustomer.setText(item.getCustomer());
            holder.mComment.setText(item.getComment());

        }

        @Override
        public int getItemCount(){
            return mList.size();
        }

        public void setList(List<CommentItem> list){
            mList = list;
        }

    public class EvaluateHolder extends RecyclerView.ViewHolder{
        private TextView mCustomer;
        private TextView mComment;

        public EvaluateHolder(View itemView){
            super(itemView);
            mCustomer = (TextView)itemView.findViewById(R.id.comment_name);
            mComment = (TextView)itemView.findViewById(R.id.comment_content);

        }
    }

    public static class CommentItem{
        private String customer;
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }
    }
}
