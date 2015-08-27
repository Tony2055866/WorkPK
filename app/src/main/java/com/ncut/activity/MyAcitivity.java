package com.ncut.activity;

import java.io.IOException;

import com.ncut.net.ClientThread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * 我的基类，完成对handler的赋值
 * @author GaoTong
 *
 */
public abstract class MyAcitivity extends Activity {
	protected Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			handleMsg(msg);
		};
	};
	
	public abstract void handleMsg(android.os.Message msg);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ClientThread.handler = this.handler;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		ClientThread.handler = this.handler;
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		ClientThread.handler = this.handler;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		ClientThread.handler = this.handler;
	}
	

}
