package com.ncut.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ncut.dao.DbUtil;
import com.ncut.net.ClientThread;
import com.ncut.util.Constants;
import com.ncut.util.MyUtil;
import com.nuct.model.Player;

public class StadiumActivity extends MyAcitivity implements OnClickListener{
	public static final int BG_SOUND1 = R.raw.gameplay;
	//private Button normal_btn1,normal_btn2,normal_btn3,normal_btn4,normal_btn5,normal_btn6;
	ProgressDialog dia;
	long start;
	public static int currentView = 0;
	public List<Button> listRoomsBtn = new ArrayList<Button>(10);
	String[] getCmds;
	
	private Button staBtns[] = new Button[Constants.ids.length];
	//分类的标题对象。
	private RelativeLayout catRLs[] = new RelativeLayout[Constants.catIds.length];
	//分类是否展开
	private boolean catUnfold[] = new boolean[Constants.catIds.length];
	
	MediaPlayer sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.stadium2);
		 sp = MediaPlayer.create(this,BG_SOUND1);
		 sp.setLooping(true);
		if(LoginActivity.isSound){
			sp.start();
		}
		
		SysApplication.getInstance().addActivity(this);
		//ClientThread.precess = this;
		Intent intent = getIntent();
		//Log.d("tong", "statdium start!!, test  statdium getintent: "  + intent);
		String nums = null;
		//获得从Login 传过来的信息
		if(intent != null ){
			getCmds = intent.getStringArrayExtra("myInfo");
			if(getCmds != null){
				 nums = getCmds[2];
				 try {
					Constants.player = new Player(getCmds, 3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				 
				//Log.d("tong", "stadium get info success!  "  + player.name );
			}
		}
		

		for(int i=0; i<staBtns.length; i++){
			staBtns[i] = (Button) findViewById(Constants.ids[i]);
			staBtns[i].getBackground().setAlpha(210); // 0~ 255
			staBtns[i].setOnClickListener(this);
			staBtns[i].setVisibility(View.GONE);
		}
		for(int i=0; i<Constants.catIds.length; i++){
			catRLs[i] = (RelativeLayout) findViewById(Constants.catIds[i]);
			catRLs[i].setOnClickListener(this);
			catRLs[i].getBackground().setAlpha(230);
		}
		
		
		//重新设置游戏房间的人数
		if(nums != null){
			//Log.d("tong", "server cnts: " + nums);
			resetNums(nums);
		}
	}
	
	public void review(View v){
		Intent intent = new Intent(this, Review.class);
		startActivity(intent);
	}
	
	public void setup(View v){
		SetupDialog sdia = new SetupDialog(this);
		sdia.setTitle("设置");
		sdia.show();
		sdia.setCancelable(false);
		sdia.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				//Log.d("tong", "isbgSoundCb 2:" + LoginActivity.isSound);
				if(!LoginActivity.isSound) sp.stop();
				else{
					if(!sp.isPlaying()){
						sp = MediaPlayer.create(StadiumActivity.this,BG_SOUND1);
						sp.setLooping(true);
						sp.start();
					}
				}
			}
		});
	}
	
	public void showMyinfo(View v){
		//Log.d("tong", "show info " + player);
		if(Constants.player == null) return;
		int gameCnt = 0, passCnt = 0, notPassCnt = 0;
		passCnt = DbUtil.getTotal1();
		notPassCnt = DbUtil.getTotal2();
		String info = "用户名:" + Constants.player.name + "\n" +
		"邮箱 :" + Constants.player.email + "\n" +
		"学校:" + Constants.player.school + "\n" +
		"积分:" + Constants.player.points + "\n" +
		"游戏次数:"+ 0 +" 次\n" +
		"已通过单词:" + passCnt + " 个\n"+
		"未通过单词："+ notPassCnt + " 个\n";
		
		new AlertDialog.Builder(this).setTitle("个人信息").setMessage(info)
		.create().show();
	}
	
	private void resetNums(String nums){
		String numArr[] = nums.split(",");
		for(int i=0; i<staBtns.length && i < numArr.length; i++){
			Button btn = staBtns[i];
			String newStr = btn.getText().toString()+ "    "+ numArr[i] + "人";
			btn.setText(newStr);
		}
	}
	
	public void handleMsg(Message msg){
		final String str = msg.obj.toString();
		final String cmds[] = str.split("\\$");
		if(cmds[0].equals("error")){
			MyUtil.showConnDialog(StadiumActivity.this);
		}else if(cmds[0].equals("stadium")){
			new Thread(){
				public void run() {
//					if(System.currentTimeMillis() - start < 1000){
//						try {
//							Thread.sleep(1000 - (System.currentTimeMillis() - start) );
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
					sp.stop();
					dia.dismiss();
					Intent intent = new Intent(StadiumActivity.this, GameActivity.class);
					//if(cmds.length > 1){
					//Log.d("tong", "to game oppInfo: " + str);
						intent.putExtra("oppInfo", cmds); //玩家的信息
					//}
						//intent.putExtra("myInfo", getCmds); //我的信息
					startActivity(intent);
				};
			}.start();

			
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(LoginActivity.isSound){
			if(!sp.isPlaying()){
				sp = MediaPlayer.create(this,BG_SOUND1);
				sp.setLooping(true);
				sp.start();
			}
			
		}
	}

	@Override
	public void onClick(View v) {
		
		if(v instanceof Button){
			if(!ClientThread.isConn){
				return;
			}
			int stadumIndex=-1;
			for(int i=0; i< Constants.ids.length; i++){
				if(v.getId() == Constants.ids[i]){
					stadumIndex = i;
//					Intent intent = new Intent(this, SingleGame.class);
//					startActivity(intent);
					break;
				}
			}
			doStadiumIn(stadumIndex); //处理用户进出
		}else if(v instanceof RelativeLayout){
			RelativeLayout rl = (RelativeLayout) v;
			ImageView image = (ImageView) rl.getChildAt(0);
			Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
			Matrix matrix = new Matrix();
			int index = getCatIndex(rl);
			if(catUnfold[index]){
				catUnfold[index] = !catUnfold[index];
				matrix.setRotate(-90);
				staBtns[index*3].setVisibility(View.GONE) ;
				staBtns[index*3+1].setVisibility(View.GONE) ;
				staBtns[index*3+2].setVisibility(View.GONE) ;
			}else{
				catUnfold[index] = !catUnfold[index];
				matrix.setRotate(90);
				staBtns[index*3].setVisibility(View.VISIBLE) ;
				staBtns[index*3+1].setVisibility(View.VISIBLE) ;
				staBtns[index*3+2].setVisibility(View.VISIBLE) ;
			}
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			image.setImageBitmap(bitmap);
		}
	}

	private void doStadiumIn(int stadumIndex) {
		if(stadumIndex >= 0 && stadumIndex < 6){
			//Log.d("tong", "goin stadium: " +stadumIndex );
			Constants.staId = stadumIndex;
			ClientThread.sendMsg("stadium" + "$" + stadumIndex);
			dia = ProgressDialog.show(StadiumActivity.this, "进入大厅", "正在读取游戏数据...");
			//this.setContentView(R.layout.loading);
			dia.show();
			dia.setCancelable(true);
			start = System.currentTimeMillis();

		}
	}
	

	
	@Override
	public void onBackPressed() {
		if(dia != null && dia.isShowing()){
			dia.dismiss();
		}else{
			new AlertDialog.Builder(this).setMessage("确认要退出吗?").setPositiveButton("退出游戏", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sp.stop();
					SysApplication.getInstance().exit();
					return;
				}
			}).setNegativeButton("注销登录", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ClientThread.sendMsg("logout");
					sp.stop();
					finish();
				}
			}).create().show();
		}
		
	}
	private int getCatIndex(RelativeLayout rl) {
		for(int i=0; i<catRLs.length; i++){
			if(catRLs[i] == rl) return i;
		}
		return 0;
	}
	
	
}
