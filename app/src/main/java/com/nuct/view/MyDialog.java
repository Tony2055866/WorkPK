package com.nuct.view;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ncut.activity.GameActivity;
import com.ncut.activity.R;
import com.ncut.util.Constants;


public class MyDialog extends Dialog {
	Activity context;
	String[] strArr;
	private TextView resultTv,tv1,tv2,tv3,tv4;
	private int con;
	public MyDialog(Context context) {
		super(context);
	}
	public MyDialog(Context context, int theme, String cmds[]) {
		super(context,theme);
		this.context = (Activity) context;
		strArr = cmds;
	}
	
	public MyDialog(Context context, int theme, int continues) {
		super(context,theme);
		this.context = (Activity) context;
		con = continues;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.mydialog);
		resultTv = (TextView) findViewById(R.id.restv);
		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);
		tv3 = (TextView) findViewById(R.id.tv3);
		tv4 = (TextView) findViewById(R.id.tv4);
		
		if(strArr != null){
			if(strArr.length <= 6) return;
			
			if(strArr[0].equals("oppexit")){
				resultTv.setText("对方逃跑!");
				tv1.setText("我的最大连击：" + strArr[3]);
				tv3.setText("我的得分：+" +strArr[2] );
				tv2.setText("对方最大连击：" + strArr[6]);
				tv4.setText("对方得分：-" + strArr[5]);
			}else{
				
				//胜利
				if(strArr[1].equals(Constants.player.name)){
					resultTv.setText("胜利!");
					tv1.setText("我的最大连击：" + strArr[3]);
					tv3.setText("我的得分：+" +strArr[2] );
					tv2.setText("对方最大连击：" + strArr[6]);
					tv4.setText("对方得分：-" + strArr[5]);
					if(Constants.isGameSound){
						GameActivity.sp.play(GameActivity.spMap.get(5) ,1, 1, 0, 0, 1);
					}
				}else{
					tv1.setText("我的最大连击：" + strArr[6]);
					tv3.setText("我的得分：-" +strArr[5]);
					tv2.setText("对方最大连击：" + strArr[3]);
					tv4.setText("对方得分：+" + strArr[2]);
					resultTv.setText("失败!");
					if(Constants.isGameSound){
						GameActivity.sp.play(GameActivity.spMap.get(6) ,1, 1, 0, 0, 1);
					}
				}
			}
			
		}else{
			resultTv.setText("完成!");
			//单机模式
			tv1.setText("我的最大连击： " + con);
			tv2.setVisibility(View.GONE);
			tv3.setText("我的得分: " + 0);
			tv4.setVisibility(View.GONE);
		}
		
		
	}
	

}
