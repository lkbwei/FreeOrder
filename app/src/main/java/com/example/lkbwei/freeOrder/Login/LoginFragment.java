package com.example.lkbwei.freeOrder.Login;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lkbwei.freeOrder.R;


/**
 * 登录界面实现
 * Created by lkbwei on 2017/3/4.
 */

public class LoginFragment extends Fragment {

    public static Handler sHandler;
    public static final int LOGIN_NOT_SUCCESS = 1;
    public static final int NET_STATUS = 2;

    private EditText user;
    private EditText pwd;
    private Button bossLogin;
    private Button customerLogin;
    private TextView register;
    private ImageView mProgress;
    private AnimatorSet set;

    /**
     * 注册监听器，具体由Activity实现
     * @since 1.0
     */
    public interface RegisterListener{
        void register();
    }

    /**
     * 登录监听器，具体由Activity实现
     * @since 1.0
     */
    public interface LoginListener{
        void login(String user,String pwd, int identity);
    }

    /**
     * 新建LoginFragment实例
     * @return LoginFragment实例
     * @since 1.0
     */
    public static Fragment newInstance(){
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.login_fragment,container,false);
        initHandler();
        set = new AnimatorSet();

        user = (EditText)view.findViewById(R.id.login_name);
        pwd = (EditText)view.findViewById(R.id.login_password);
        bossLogin = (Button)view.findViewById(R.id.boss_login);
        customerLogin = (Button)view.findViewById(R.id.customer_login);
        register = (TextView)view.findViewById(R.id.register);
        mProgress = (ImageView)view.findViewById(R.id.oval_animator);

        bossLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = user.getText().toString();
                String password = pwd.getText().toString();

                if (getActivity() instanceof LoginListener){
                    ((LoginListener) getActivity()).login(userName,password,BaseLoginActivity.BOSS);
                }
                login();
                bossLogin.setText("登录中...");
                bossLogin.setEnabled(false);
                customerLogin.setEnabled(false);
                register.setEnabled(false);
            }
        });

        customerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = user.getText().toString();
                String password = pwd.getText().toString();

                if(getActivity() instanceof LoginListener){
                    ((LoginListener) getActivity()).login(userName,password,BaseLoginActivity.CUSTOMER);
                }
                login();
                customerLogin.setText("登录中...");
                bossLogin.setEnabled(false);
                customerLogin.setEnabled(false);
                register.setEnabled(false);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof RegisterListener){
                    ((RegisterListener) getActivity()).register();
                }
            }
        });

        return view;
    }

    public void initHandler(){
        sHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case LOGIN_NOT_SUCCESS:
                        loginFailed();
                        break;
                    case NET_STATUS:
                        loginFailed();
                        Toast.makeText(getActivity(),"没有可用网络哦",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    /**
     * 开启动画
     * 开启登录动画
     * @since 1.0
     */
    public void login(){
        startAnimator();
    }

    /**
     * 登录失败
     * 登录失败的处理
     * @since 1.0
     */
    public void loginFailed(){
        set.end();
        mProgress.setVisibility(View.INVISIBLE);
        bossLogin.setEnabled(true);
        customerLogin.setEnabled(true);
        bossLogin.setText("商家登录");
        customerLogin.setText("客户登录");
        register.setEnabled(true);
    }

    /**
     * 登录动画
     * 登录动画实现
     * @since 1.0
     */
    public void startAnimator(){
        mProgress.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                mProgress,
                "translationX",
                400);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                mProgress,
                "scaleX",
                0,
                0.5F
        );
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                mProgress,
                "scaleY",
                0,
                0.5f
        );
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(
                mProgress,
                "alpha",
                0,
                1f
        );
        animator.setRepeatCount(Integer.MAX_VALUE);
        animator1.setRepeatCount(Integer.MAX_VALUE);
        animator2.setRepeatCount(Integer.MAX_VALUE);
        animator3.setRepeatCount(Integer.MAX_VALUE);

        set.setDuration(3000);
        set.playTogether(animator,animator1,animator2);
        set.start();
    }
}
