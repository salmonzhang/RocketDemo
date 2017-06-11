package com.example.rocketdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SmokeActivity extends Activity {

	private ImageView iv_bottom;
	private ImageView iv_top;
	private AlphaAnimation aa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smoke);
		System.out.println("jinru");
		initview();
	}

	private void initview() {
		iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
		iv_top = (ImageView) findViewById(R.id.iv_top);
		
		aa = new AlphaAnimation(0.2f, 1.0f);
		
		aa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				//动画结束时，自动销毁
				finish();
			}
		});
		
		aa.setDuration(400);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				iv_bottom.setVisibility(View.VISIBLE);
				iv_top.setVisibility(View.VISIBLE);
				iv_bottom.setAnimation(aa);
				iv_top.setAnimation(aa);
			}
		}, 400);
		
	}
}
