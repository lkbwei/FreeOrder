package com.example.lkbwei.freeOrder.Tools;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lkbwei.freeOrder.R;

/**
 * Created by lkbwei on 2017/3/5.
 */

public class UseProgressBar {
    private android.widget.ProgressBar mProgressBar;
    private TextView mTextView;
    private View mView;
    private Button mButton;
    private ImageView mImage;
    private LayoutInflater inflater;

    public UseProgressBar(View view){
        //inflater = LayoutInflater.from(context);
        mView = view.findViewById(R.id.include);
        mProgressBar = (android.widget.ProgressBar)mView.findViewById(R.id.progress_bar);
        mTextView = (TextView)mView.findViewById(R.id.progress_bar_text);
        mImage = (ImageView)mView.findViewById(R.id.oval);

        start();
    }

    public void start(){
        mTextView.setAlpha(0);
        mImage.setVisibility(View.INVISIBLE);
    }

    public void end(String result){
        mProgressBar.setVisibility(android.widget.ProgressBar.GONE);
        mImage.setVisibility(View.VISIBLE);
        Animator oval = ViewAnimationUtils.createCircularReveal(
                mImage
                ,mImage.getWidth()/2
                ,mImage.getHeight()/2
                ,0
                ,(float)mImage.getWidth()
        );

        oval.setInterpolator(new AccelerateInterpolator());
        oval.setDuration(600);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mView,"translationX",-160);
        ObjectAnimator textAnimator = ObjectAnimator.ofFloat(mTextView,"alpha",0,1);
        animator.setDuration(400);
        textAnimator.setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.play(oval).before(animator).before(textAnimator);
        set.start();
    }

    public View getView(){
        return mView;
    }
}
