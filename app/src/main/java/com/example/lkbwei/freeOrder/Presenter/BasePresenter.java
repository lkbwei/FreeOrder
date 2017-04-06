package com.example.lkbwei.freeOrder.Presenter;

/**
 * Created by lkbwei on 2017/3/29.
 */

public abstract class BasePresenter<T> {
    protected T mView;

    public void attachView(T view){
        mView = view;
    }

    public void detachView(){
        mView = null;
    }
}
