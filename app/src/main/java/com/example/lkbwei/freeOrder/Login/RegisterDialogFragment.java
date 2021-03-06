package com.example.lkbwei.freeOrder.Login;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.lkbwei.freeOrder.R;

/**
 * 注册界面实现
 * Created by lkbwei on 2017/3/4.
 */

public class RegisterDialogFragment extends DialogFragment {

    private EditText user;
    private EditText pwd;
    private static final int BOSS = 1;
    private static final int CUSTOMER = 2;

    /**
     * 注册窗口弹出监听器，具体由Activity实现
     * @since 1.0
     */
    public RegisterDialogFragment(){

    }

    /**
     * 注册验证监听器，具体由Activity实现
     * @since 1.0
     */
    public interface DoRegisterListener{
        void doRegister(String user, String pwd, int identity);
    }

    /**
     * 注册窗口
     * 创建注册窗口
     * @param savedInstanceState 数据记录
     * @return 窗口
     * @since 1.0
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.register_dialog,null);
        user = (EditText)view.findViewById(R.id.register_name);
        pwd = (EditText)view.findViewById(R.id.register_password);

        builder.setView(view)
                .setPositiveButton("客户注册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = user.getText().toString();
                        String password = pwd.getText().toString();

                        if (getActivity() instanceof DoRegisterListener){
                            ((DoRegisterListener) getActivity()).doRegister(name,password,CUSTOMER);
                        }
                    }
                })
                .setNegativeButton("商家注册", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = user.getText().toString();
                        String password = pwd.getText().toString();

                        if (getActivity() instanceof DoRegisterListener){
                           ((DoRegisterListener) getActivity()).doRegister(name,password,BOSS);
                        }
                   }
        });

        return builder.create();
    }

}
