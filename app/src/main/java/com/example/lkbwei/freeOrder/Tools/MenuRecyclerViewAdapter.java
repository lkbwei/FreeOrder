package com.example.lkbwei.freeOrder.Tools;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.GoodsData;
import com.example.lkbwei.freeOrder.Boss.BaseMenuActivity;
import com.example.lkbwei.freeOrder.Boss.MenuListFragment;
import com.example.lkbwei.freeOrder.Customer.CustomerMenuListFragment;
import com.example.lkbwei.freeOrder.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lkbwei on 2017/3/10.
 */

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.MenuViewHolder> {
    private List<GoodsData> mGoodsDatas;
    private LayoutInflater mLayoutInflater;
    private Context mContext;

    public MenuRecyclerViewAdapter(Context context,List<GoodsData> datas){
        mLayoutInflater = LayoutInflater.from(context);
        mGoodsDatas = datas;
        mContext = context;
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.menu_view,parent,false);

        return new MenuViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        if (holder.itemView instanceof MenuItemView){
            if(((MenuItemView) holder.itemView).hasMoved == true){
                MenuItemView view = (MenuItemView)holder.itemView;
                view.mScroller.startScroll(view.getScrollX(),view.getScrollY(),-holder.getButtonWidth(),0,1);
                view.hasMoved = false;
            }
        }
        GoodsData data = mGoodsDatas.get(position);
        holder.mName.setText(data.getFoodName());
        holder.mPrice.setText(data.getPrice().toString());
        holder.mSaleNum.setText(data.getSaleVolume().toString());
        holder.mGOODNum.setText(data.getPositive().toString());
        if (!data.getStock()){
            holder.mStock.setText("缺货");
            holder.mButton.setEnabled(false);
        }else {
            holder.mStock.setText("");
            holder.mButton.setEnabled(true);
        }

        Picasso.with(mContext)
                .load(data.getImageUrl())
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mGoodsDatas.size();
    }

    public void setGoodsDatas(List<GoodsData> datas){
        mGoodsDatas = datas;
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mName;
        private ImageView mImageView;
        private TextView mPrice;
        private TextView mSaleNum;
        private TextView mGOODNum;
        private Button mButton;
        private TextView mStock;
        private Button mDeleteButton;

        private int screenWidth;
        private int allWidth;


        public MenuViewHolder(final View itemView) {
            super(itemView);
            final MenuItemView view = (MenuItemView)itemView;
            itemView.setOnClickListener(this);

            if (BaseMenuActivity.identity == BaseMenuActivity.BOSS){
                itemView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int x = (int)event.getX();
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                view.lastX = x;
                                return false;
                            case MotionEvent.ACTION_MOVE:
                                return false;
                            case MotionEvent.ACTION_UP:
                                int offsetX = x - view.lastX;
                                if (offsetX < 0 && !view.hasMoved){
                                    view.mScroller.startScroll(view.getScrollX(),view.getScrollY(),getButtonWidth(),0,800);
                                    view.hasMoved = true;
                                }else if(offsetX > 0 && view.hasMoved){
                                    view.mScroller.startScroll(view.getScrollX(),view.getScrollY(),-getButtonWidth(),0,800);
                                    view.hasMoved = false;
                                }
                                if (x - view.lastX == 0) {
                                    return false;
                                }else {
                                    view.invalidate();
                                    return true;
                                }
                        }
                        return false;
                    }
                });
            }

            mName = (TextView)itemView.findViewById(R.id.name);
            mImageView = (ImageView)itemView.findViewById(R.id.imageView);
            mPrice = (TextView)itemView.findViewById(R.id.price);
            mGOODNum = (TextView)itemView.findViewById(R.id.good_num);
            mSaleNum = (TextView)itemView.findViewById(R.id.num);
            mButton = (Button)itemView.findViewById(R.id.add_button);
            mStock = (TextView)itemView.findViewById(R.id.stock);
            mDeleteButton = (Button)itemView.findViewById(R.id.menu_delete);
            if(BasePreferences.getIdentity(mContext) == BaseMenuActivity.BOSS){
                mButton.setVisibility(View.INVISIBLE);
            }

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerMenuListFragment.privateHandler.obtainMessage(CustomerMenuListFragment.ADD_ANIMATION).
                            sendToTarget();
                    addFood();
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MenuListFragment.sLab.deleteGoodsData(mContext,new String[]{"User","FoodName"},
                            new Object[]{MenuListFragment.currentRestaurant,mName.getText().toString()},
                            MenuListFragment.sHandler,MenuListFragment.DELETE,true);
                }
            });

            WindowManager manager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
            screenWidth = manager.getDefaultDisplay().getWidth();
            allWidth = screenWidth + getButtonWidth();
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            params.width = allWidth;
            itemView.setLayoutParams(params);

        }

        public void addFood(){
            List<String> list = new ArrayList<>();
            list.add(mName.getText().toString());
            list.add(mPrice.getText().toString());

            CustomerMenuListFragment.privateHandler.obtainMessage(
                    CustomerMenuListFragment.ADD,list).sendToTarget();
        }

        public int getButtonWidth(){
            float scale = mContext.getResources().getDisplayMetrics().density;
            int width = mContext.getResources().getDimensionPixelSize(R.dimen.delete_button_width);
            return width;
        }

        @Override
        public void onClick(View view){
            int position = getPosition();
            MenuListFragment.sHandler.obtainMessage(MenuListFragment.OPEN_NOT_COVER_DESCRIPTION,position).sendToTarget();
        }

    }
}
