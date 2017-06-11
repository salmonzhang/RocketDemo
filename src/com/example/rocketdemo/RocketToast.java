package com.example.rocketdemo;



import android.R.integer;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class RocketToast implements OnTouchListener {
	private View mView;
	private WindowManager mWM;
	private Context mContext;
	private WindowManager.LayoutParams mParams;
	private int startX;
	private int startY;
	private android.view.WindowManager.LayoutParams mTipParams;
	private ImageView tipView;
	private boolean shouldSend = false;

	public RocketToast(Context context) {
		this.mContext = context;
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		// 1.在xml中部分属性是以layout_开头，这些属性是控件所决定不了，必须与父控件进行“商量”
		// 2.在xml中部分属性是不以layout_开头，这些属性是控件所能够决定的，不需要与父控件进行“商量”
		// 3.在xml中部分属性是不以layout_开头属性可以直接通过控件的set等方法直接修改
		// 4.在xml中部分属性是以layout_开头,需要通过布局参数（LayoutParams）来设置
		// 5.在创建布局参数（LayoutParams）时，必须是当前控件的父容器类型的布局参数
		// 火箭的属性设置
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 设置不可获取焦点
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 设置不可触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 设置始终亮屏
		mParams.format = PixelFormat.TRANSLUCENT;
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// 设置窗口类型
		mParams.gravity = Gravity.LEFT | Gravity.TOP;//设置在窗口中的位置
		
		//提示框的属性设置
		mTipParams = new WindowManager.LayoutParams();
		mTipParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mTipParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mTipParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE// 设置不可获取焦点
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE// 设置不可触摸
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;// 设置始终亮屏
		mTipParams.format = PixelFormat.TRANSLUCENT;
		mTipParams.type = WindowManager.LayoutParams.TYPE_TOAST;// 设置窗口类型
		mTipParams.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;//设置在窗口中的位置
	}

	// 显示位置
	public void showRocketToast() {

		mView = View.inflate(mContext, R.layout.view_rocket, null);
		//获取背景转化为动画
		ImageView imageView = (ImageView) mView.findViewById(R.id.iv_rocket);
		AnimationDrawable rocketAnimation = (AnimationDrawable) imageView.getBackground();
		rocketAnimation.start();

		// 给view设置触摸监听
		mView.setOnTouchListener(this);
		// 将view对象添加到窗口对象中
		mWM.addView(mView, mParams);

	}

	// 隐藏Toast方式显示归属地
	public void hideRocketToast() {
		if (mView != null) {
			// note: checking parent() just to make sure the view has
			// been added... i have seen cases where we get here when
			// the view isn't yet added, so let's try not to crash.
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mView = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int action = event.getAction();
		System.out.println("action="+action);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) event.getRawX();
			startY = (int) event.getRawY();
			//按下时显示提示框
			showTipView();
			
			break;
		case MotionEvent.ACTION_MOVE:
			// 记下鼠标移动的位置
			int endX = (int) event.getRawX();
			int endY = (int) event.getRawY();

			// 计算出间距
			int diffX = endX - startX;
			int diffY = endY - startY;

			// 将间距赋给窗口参数
			mParams.x += diffX;
			mParams.y += diffY;
			mWM.updateViewLayout(mView, mParams);

			// 将此时的鼠标位置设定为起始位置
			startX = endX;
			startY = endY;
			
			//鼠标移动时，提示框的帧动画启动
			startTipAnimation();
			//在移动的过程中，实时的判断火箭的位置，看火箭是否进入了提示框
			//获取火箭的位置
			int [] rocketlocation = new int[2];
			mView.getLocationOnScreen(rocketlocation);//赋值函数
			int rocketX = rocketlocation[0];
			int rocketY = rocketlocation[1];
			
			//获取提示框的位置
			int [] tiplocation = new int[2];
			tipView.getLocationOnScreen(tiplocation);
			int tipX = tiplocation[0];
			int tipY = tiplocation[1];
			
			//判断火箭是否进入的提示框
			if((rocketY + mView.getHeight()/2 > tipY)&&(rocketX>tipX)&&((rocketX+mView.getWidth())<(tipX+tipView.getWidth()))){
				System.out.println("火箭已进入提示框");
				//进入提示框后，提示框的帧动画停止，背景改后一张图片
				stopTipAnimation();
				//给火箭的发送定义一个标记
				shouldSend  = true;
			}
			
			break;
		case MotionEvent.ACTION_UP:
			//当鼠标抬起时，隐藏提示框
			hideTipView();
			
			//鼠标弹开时，如果火箭的标记为true，则发射火箭
			if(shouldSend){
				sendRocket();
				//执行冒烟动画
				startSmoke();
			}
			
			break;

		default:
			break;
		}
		return true;//一定要返回true，一定要让窗口自己处理当前的触摸事件
	}

	//使用透明度动画设置延迟执行实现冒烟动画
	private void startSmoke() {
		//在服务中启动Activity
		Intent intent = new Intent(mContext, SmokeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	//发射火箭
	private void sendRocket() {
		//使用值动画
		ValueAnimator va = ValueAnimator.ofInt(mParams.y,0);
		//给值动画设置一个监听器
		va.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				//动态获取模拟演示的值
				Integer valueInteger = (Integer)animation.getAnimatedValue();
				//将值赋给mParams.y
				mParams.y = valueInteger;
				//更新火箭的位置
				mWM.updateViewLayout(mView, mParams);
			}
		});
		
		va.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				mParams.x = mContext.getResources().getDisplayMetrics().widthPixels/2 - mView.getWidth()/2; 
				mWM.updateViewLayout(mView, mParams);
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				// TODO Auto-generated method stub
				mParams.x = 0;
				mWM.updateViewLayout(mView, mParams);
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				
			}
		});
		
		va.setDuration(800);
		va.start();
		
	}

	//停止提示框
	private void stopTipAnimation() {
		AnimationDrawable tipAnimation = (AnimationDrawable)tipView.getBackground();
		tipAnimation.stop();
		tipView.setBackgroundResource(R.drawable.desktop_bg_tips_3);
	}

	private void showTipView() {
		tipView = new ImageView(mContext);
		tipView.setBackgroundResource(R.anim.tip);
		//将tipView添加到窗口中
		mWM.addView(tipView, mTipParams);
	}

	private void hideTipView() {
		if (tipView != null) {
			if (tipView.getParent() != null) {
				mWM.removeView(tipView);
			}
			tipView = null;
		}
	}

	private void startTipAnimation() {
		tipView.setBackgroundResource(R.anim.tip);
		AnimationDrawable tipAnimation = (AnimationDrawable)tipView.getBackground();
		tipAnimation.start();
	}

}
