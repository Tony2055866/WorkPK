package com.ncut.activity;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import com.ncut.dao.DbUtil;
import com.ncut.net.ClientThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
/** 欢迎动画activity */
public class WelcomeActivity extends Activity {
	/** Called when the activity is first created. */
	//public static ClientThread thread;
	Intent intent = null;
	public  boolean conn = false, iscontinue = true;
	long start;
	public static float density;
	AlertDialog dialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		start = System.currentTimeMillis();
		SysApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		
		density = dm.density;
		
		new getConnThread().start();
		String path = "/data/data/" + getPackageName() + "/database";
		DbUtil.db = DbUtil.openDatabase(path, "word.db", this);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				if(!conn){
					iscontinue = false;
					Message msg = new Message();
					handler.sendMessage(msg);
				}
			}
		}, 5000);
		
	}

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			 dialog = new AlertDialog.Builder(WelcomeActivity.this).setTitle("没有网络")
			.setMessage("连接服务器起失败！")
			.setPositiveButton("单人练习", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(WelcomeActivity.this, NoNetActivity.class);
					startActivity(intent);
					finish();
				}
			} ).setNegativeButton("退出游戏", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						onBackPressed();
					}
			}).create();
			dialog.setCancelable(false);
			if(!isFinishing())
				dialog.show();
		}
	};
	
	class getConnThread extends Thread {
		@Override
		public void run() {
			
			 conn = false;
			try {
				ClientThread.cthread  = new ClientThread();
				ClientThread.cthread.start();
				conn = true;
			} catch (Exception e1) {
				e1.printStackTrace();
				conn = false;
			}
			if(iscontinue == false) return;
			if(conn == false){
				Message msg = new Message();
				handler.sendMessage(msg);
				return;
			}
			
			//Log.d("tong", "connction result " + conn);
			
			 intent = new Intent(WelcomeActivity.this, LoginActivity.class);
			 // 系统会为需要启动的activity寻找与当前activity不同的task;
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 if(conn){
//				 intent = new Intent(WelcomeActivity.this, LoginActivity.class);
//				 // 系统会为需要启动的activity寻找与当前activity不同的task;
//				 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 // intent.putExtra("isConn", false);
			 }else{
				 intent.putExtra("isconn", false); 
//				setContentView(R.layout.no_net);
			 }
			 long dur = System.currentTimeMillis() - start;
			 if(dur < 1000){
				 try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			 }
			if (intent == null)
				return;
			startActivity(intent);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(dialog!=null && dialog.isShowing())
		dialog.dismiss();
	}
	
}
