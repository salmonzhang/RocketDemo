package com.example.rocketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RocketService extends Service {

	private RocketToast rocketToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		rocketToast = new RocketToast(this);
		System.out.println("火箭已经启动了");
		rocketToast.showRocketToast();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//服务关闭时，关闭动画
		rocketToast.hideRocketToast();
		System.out.println("火箭已经关闭了");
	}

}
