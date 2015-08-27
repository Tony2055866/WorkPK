package com.ncut.activity;


import java.io.IOException;

import com.ncut.net.ClientThread;
import com.ncut.util.Constants;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
/**
 * 无网模式
 * @author gaotong
 *
 */
public class NoNetActivity extends Activity implements OnClickListener{
	
	private Button staBtns[] = new Button[Constants.ids.length];
	//分类的标题对象。
	private RelativeLayout catRLs[] = new RelativeLayout[Constants.catIds.length];
	//分类是否展开
	private boolean catUnfold[] = new boolean[Constants.catIds.length];
	
	public static int staId;
	private ImageView tipImgs[] = new ImageView[3];
	//private int imgids[] = new int[]{R.id.titleBtn1,R.id.titleBtn2, R.id.titleBtn3};
	public static MediaPlayer mp;
	
	public static boolean isBgSound = true,isSound = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.d("tong", "NoNetActivity on create");
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.no_net);
		
//		RelativeLayout rl = (RelativeLayout) findViewById(R.id.catLL01);
//		ImageView image = (ImageView) rl.getChildAt(0);
//		rl.setOnClickListener(this);
		
		/*
		 * 企图动态生成所有组件，dp 像素问题，该用xml
		 * LinearLayout llParent = (LinearLayout) findViewById(R.id.llParent);
		LinearLayout.LayoutParams llPram = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);
		llPram.setMargins(0, (int) (6 * WelcomeActivity.density), 0, (int) (6 * WelcomeActivity.density) );
		Drawable draw = getResources().getDrawable(R.drawable.cate_bg);
		RelativeLayout.LayoutParams rlPram = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		for(int i=0; i<=9; i++){
			RelativeLayout rl = new RelativeLayout(this);
			rl.setLayoutParams(llPram);
			rl.setBackgroundDrawable(draw);
			TextView tv = new TextView(this);
			tv.setLayoutParams(rlPram);
			tv.setTextColor(0xffffff);
			tv.settextSi
		}*/
		
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
		 
		mp = MediaPlayer.create(this,StadiumActivity.BG_SOUND1);
		mp.setLooping(true);
		if(isBgSound) mp.start();
	}
	
	@Override
	public void onBackPressed() {
		SysApplication.getInstance().exit();
	}

	public void onClick(View v) {
		if(v instanceof Button){
			for(int i=0; i<Constants.ids.length; i++){
				if(v.getId() == Constants.ids[i]){
					staId = i;
					Intent intent = new Intent(this, SingleGame.class);
					startActivity(intent);
					break;
				}
			}
		}else if(v instanceof RelativeLayout){
			//RelativeLayout rl = (RelativeLayout) findViewById(R.id.catLL01);
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
	
	private int getCatIndex(RelativeLayout rl) {
		for(int i=0; i<catRLs.length; i++){
			if(catRLs[i] == rl) return i;
		}
		return 0;
	}

	public void review(View v){
		Intent intent = new Intent(this, Review.class);
		startActivity(intent);
		//initReview();
	}
	
	//点击设置
	public void setupClick(View v){
		SetupDialog4Single sdia = new SetupDialog4Single(this);
		sdia.setTitle("设置");
		sdia.show();
		sdia.setCancelable(false);
		sdia.setOnCancelListener(new OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				if(isBgSound) mp.start();
				else mp.stop();
			}
		});
	}
}
