package com.example.lkbwei.freeOrder.Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArraySet;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.DataBase.BasePreferences;
import com.example.lkbwei.freeOrder.DataBase.LoginTable;
import com.example.lkbwei.freeOrder.Tools.NetReceiver;
import com.example.lkbwei.freeOrder.Presenter.LoginPresenter;
import com.example.lkbwei.freeOrder.R;

import java.util.List;
import java.util.Set;


/**
 * 登录入口
 * Created by lkbwei on 2017/3/4.
 */

public class LoginActivity extends BaseLoginActivity{
    public static final int LOGIN = 4;
    public static final int REGISTERDIOLOG = 5;
    public static final int EDITRESTAURANT = 6;
    public static final int BOSSLOGIN = 7;
    public static final int CHOOSE_RESTAURANT = 8;
    public static final int GET_BOSS = 9;
    public static final int NET_ON = 10;

    private List<String> mRestaurantList;
    private LoginPresenter mLoginPresenter;
    private AlertDialog mDialog;
    private NetReceiver mNetReceiver;

    @Override
    public void onResume(){
        super.onResume();
        mLoginPresenter = new LoginPresenter();
        mLoginPresenter.attachView(this);
    }

    /**
     *初始化网络监听
     * 继承父类方法，当是免登录界面时开启监听，网络可用时进入主界面
     * @see NetReceiver
     * @since 1.0
     */
    @Override
    public void initReceiver(){
        mNetReceiver = new NetReceiver(mHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetReceiver,intentFilter);
    }

    /**
     * 登录
     * 继承父类方法，实现登录验证工作
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @see LoginPresenter
     * @since 1.0
     */
    @Override
    public void login(String user, final String pwd, final int identity) {
        mLoginPresenter.login(user,pwd,identity,mHandler,LOGIN);
    }

    /**
     * 注册
     * 继承父类方法，实现注册逻辑
     * @see LoginPresenter
     * @since 1.0
     */
    @Override
    public void register() {
        mLoginPresenter.register();
    }

    /**
     * 注册窗口
     * 继承父类方法，弹出注册窗口，用户输入后，继续注册操作
     * @param user 用户名
     * @param pwd 密码
     * @param identity 身份
     * @see LoginPresenter
     * @since 1.0
     */
    @Override
    public void doRegister(final String user, final String pwd, final int identity) {
        mLoginPresenter.doRegister(user,pwd,identity,mHandler,REGISTERDIOLOG);
    }

    /**
     * 进入主界面
     * 登录验证完成，进入主界面
     * @since 1.0
     */
    @Override
    public void success() {
        mHandler.sendEmptyMessage(LOGIN);
    }

    /**
     * 注册餐厅
     * 当商家第一次登录时，需要先注册餐厅
     * @since 1.0
     */
    @Override
    public void editRestaurant() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_restaurant,null);
        final EditText editText = (EditText)view.findViewById(R.id.edit_restaurant);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mLoginPresenter.editRestaurant(editText.getText().toString(),mHandler);
                            }
                        }).create().show();
    }

    /**
     * 选择餐厅
     * 当用户第一次登录时，需要先选择餐厅
     * @since 1.0
     */
    @Override
    public void chooseRestaurant() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.search_layout,null);
        ImageButton searchButton = (ImageButton)view.findViewById(R.id.search_restaurant);
        final AutoCompleteTextView auto = (AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,
                mRestaurantList);
        auto.setAdapter(adapter);
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setView(view)
                .setTitle("请选择餐厅");

        mDialog = builder.create();
        mDialog.show();

        auto.setDropDownBackgroundResource(android.R.drawable.screen_background_light_transparent);

        auto.setThreshold(1);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkRestaurant(auto.getText().toString())){
                    mLoginPresenter.getBoss(auto.getText().toString());
                }
            }
        });

        auto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH &&
                        checkRestaurant(auto.getText().toString())){
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    mLoginPresenter.getBoss(auto.getText().toString());
                }
                return false;
            }
        });
    }

    /**
     * 验证餐厅
     * 商家注册餐厅后，验证是否合法
     * @param restaurant 餐厅名
     * @return 是否符合
     * @since 1.0
     */
    @Override
    public boolean checkRestaurant(String restaurant) {
        if (mRestaurantList.contains(restaurant)){
            return true;
        }else {
            Toast.makeText(this,"所选餐厅暂时未开通",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void handleMsg(){
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message mg){
                super.handleMessage(mg);
                switch (mg.what){
                    case LOGIN:
                        if(BasePreferences.getIdentity(LoginActivity.this)
                                == BOSS) {
                           mLoginPresenter.checkHaveRestaurant(mHandler);

                        }else {
                            if (BasePreferences.getRecentRestaurant(
                                    LoginActivity.this) == null){
                                mLoginPresenter.getAllRestaurant();
                            }else {
                                mLoginPresenter.saveBeforeLogin(BasePreferences.getRecentRestaurant(
                                        LoginActivity.this),CUSTOMER);
                            }

                        }

                        break;
                    case REGISTERDIOLOG:
                        RegisterDialogFragment dialogFragment = new RegisterDialogFragment();
                        dialogFragment.setEnterTransition(new Explode());
                        dialogFragment.show(getSupportFragmentManager(), null);
                        break;
                    case EDITRESTAURANT:
                        editRestaurant();
                        break;
                    case BOSSLOGIN:
                        mLoginPresenter.saveBeforeLogin(null,BOSS);
                        break;
                    case CHOOSE_RESTAURANT:
                        mRestaurantList = (List<String>)mg.obj;
                        chooseRestaurant();
                        Set<String> set = new ArraySet<>(mRestaurantList);//存储餐厅列表
                        BasePreferences.setRestaurantList(LoginActivity.this,set);
                        break;
                    case GET_BOSS:
                        List<LoginTable> datas = (List<LoginTable>)mg.obj;
                        LoginTable data = datas.get(0);
                        mLoginPresenter.saveBeforeLogin(data.getUserName(),CUSTOMER);
                        mDialog.cancel();
                        break;
                    case NET_ON:
                        if (fragment instanceof WelcomeFragment){
                            ((WelcomeFragment) fragment).getUserImage();
                            ((WelcomeFragment) fragment).setHaveGetImage(true);
                        }
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onDestroy(){

        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        mLoginPresenter.detachView();

        if (mNetReceiver != null) {
            unregisterReceiver(mNetReceiver);
        }
        mNetReceiver = null;
        super.onDestroy();
    }
}
