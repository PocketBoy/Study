package com.himi.mvpdemo.model;

import com.himi.mvpdemo.OnLoginFinishedListener;

/**
 * Created by hebao on 2016/07/24.
 * Class Note:ģ���½�Ĳ����Ľӿڣ�ʵ����ΪLoginModelImpl.�൱��MVPģʽ�е�Model��
 */
public interface LoginModel {
    void login(String username, String password, OnLoginFinishedListener listener);
}