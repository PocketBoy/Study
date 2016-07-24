package com.himi.mvpdemo.model;

import com.himi.mvpdemo.OnLoginFinishedListener;

import android.os.Handler;
import android.text.TextUtils;
/**
 * Created by hebao on 2016/7/24.
 * Class Note:��ʱģ���½��2s����������ֻ�������Ϊ�����½ʧ�ܣ������½�ɹ�
 */
public class LoginModelImpl implements LoginModel {
	
	@Override
	public void login(final String username, final String password, final OnLoginFinishedListener listener) {

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                boolean error = false;
                if (TextUtils.isEmpty(username)){
                    listener.onUsernameError();//model������ص�listener
                    error = true;
                }
                if (TextUtils.isEmpty(password)){
                    listener.onPasswordError();
                    error = true;
                }
                if (!error){
                    listener.onSuccess();
                }
            }
        }, 2000);
    }
}

