package com.himi.mvpdemo.view;

/**
 * Created by hebao on 2016/7/24.
 * Class Note:��½View�Ľӿڣ�ʵ����Ҳ���ǵ�½��activity
 */
public interface LoginView {
    void showProgress();

    void hideProgress();

    void setUsernameError();

    void setPasswordError();

    void navigateToHome();
}