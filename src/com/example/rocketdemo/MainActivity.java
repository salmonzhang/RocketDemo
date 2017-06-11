package com.example.rocketdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	// 开启火箭
	public void startRocket(View view) {
		//开启火箭服务
		startService(new Intent(MainActivity.this, RocketService.class));
		//开启服务后Activity自动消失
		finish();
	}

	// 关闭火箭
	public void closeRocket(View view) {
		//关闭火箭服务
		stopService(new Intent(MainActivity.this, RocketService.class));
	}
}
