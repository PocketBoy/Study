package com.himi.mvpdemo.presenter;

/**
 * Created by hebao on 2016/7/24. Class Note:��½��Presenter
 * �Ľӿڣ�ʵ����ΪLoginPresenterImpl����ɵ�½����֤���Լ����ٵ�ǰview
 */
public interface LoginPresenter {
	void validateCredentials(String username, String password);

	void onDestroy();
}