package com.example.lkbwei.freeOrder.Tools;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.example.lkbwei.freeOrder.R;

/**
 * Created by lkbwei on 2017/3/11.
 */

public class MenuItemView extends RelativeLayout {
    public Scroller mScroller;
    public int lastX;
    private Context mContext;
    public boolean hasMoved = false;

    public MenuItemView(Context context) {
        super(context,null);
    }

    public MenuItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mContext = context;
    }

    public MenuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
    }

    @Override
    public void computeScroll(){
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),
                    mScroller.getCurrY());
            invalidate();
        }
    }


    public int getButtonWidth(){
        int width = mContext.getResources().getDimensionPixelSize(R.dimen.delete_button_width);
        return width;
    }


}
