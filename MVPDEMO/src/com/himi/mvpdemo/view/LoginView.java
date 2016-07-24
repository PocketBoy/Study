package com.himi.mvpdemo.view;

/**
 * Created by hebao on 2016/7/24.
 * Class Note:登陆View的接口，实现类也就是登陆的activity
 */
public interface LoginView {
    void showProgress();

    void hideProgress();

    void setUsernameError();

    void setPasswordError();

    void navigateToHome();
}