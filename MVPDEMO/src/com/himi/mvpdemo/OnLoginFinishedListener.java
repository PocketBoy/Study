package com.himi.mvpdemo;

/**
 * Created by hebao on 2016/7/24.
 * Class Note:��½�¼�����
 */
public interface OnLoginFinishedListener {

    void onUsernameError();

    void onPasswordError();

    void onSuccess();
}