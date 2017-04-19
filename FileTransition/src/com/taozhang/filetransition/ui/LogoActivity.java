package com.taozhang.filetransition.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.taozhang.filetransition.R;


public class LogoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		
		init();
		
//		int screenDPI = MyViewUtil.getScreenDPI(this);
//		Toast.makeText(getApplicationContext(), screenDPI+"", Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logo, menu);
		return true;
	}
	
	 /**
     * ��ʼ��
     */
    public void init(){
//        SMSSDK.initSDK(this, "ea8331bec160", "b108e3dba9bbf7a46c8a7a4a1b5f7685");

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);//alpha ͸����
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                System.out.println("��������---");
                //������¼activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        
        TextView tv_logoAct_logo = (TextView) findViewById(R.id.launch_logoAct_logo);//΢�����
        tv_logoAct_logo.startAnimation(alphaAnimation);//�Ѷ�����ӵ������
        alphaAnimation.setDuration(3000);//������ʱ
        alphaAnimation.start();//��������
    }

}
