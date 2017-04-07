package com.example.lkbwei.freeOrder.Tools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.lkbwei.freeOrder.R;

/**
 * Created by lkbwei on 2017/3/14.
 */

public class OvalProgress {
    private static AlertDialog mAlertDialog;

    /**
     * 开启动画
     * @param context 上下文
     * @since 1.0
     */
    public static void startAnimator(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.oval_animator,null);
        ImageView imageView = (ImageView)view.findViewById(R.id.oval_animator);
        builder.setView(view);
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                imageView,
                "translationX",
                700);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                imageView,
                "scaleX",
                0,
                1.2F
        );
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                imageView,
                "scaleY",
                0,
                1.2f
        );
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(
                imageView,
                "alpha",
                0,
                1f
        );
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator1.setRepeatCount(Integer.MAX_VALUE);
        animator2.setRepeatCount(Integer.MAX_VALUE);
        animator3.setRepeatCount(Integer.MAX_VALUE);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(3000);
        set.playTogether(animator,animator1,animator2);
        mAlertDialog.show();
        set.start();
    }

    /**
     * 结束动画
     * @since 1.0
     */
    public static void endAnimator(){
        mAlertDialog.cancel();
    }
}
