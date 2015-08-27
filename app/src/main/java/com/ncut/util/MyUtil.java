package com.ncut.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ncut.activity.GameActivity;
import com.ncut.activity.LoginActivity;
import com.ncut.activity.NoNetActivity;
import com.ncut.activity.StadiumActivity;
import com.ncut.activity.WelcomeActivity;
import com.ncut.net.ClientThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.widget.Toast;

public class MyUtil {
	 	/**
	 	 * 手机号码的验证，严格验证
	 	 * @param mobiles 要验证的手机号码
	 	 * @return
	 	 */
	    public static boolean isMobileNO(String mobiles){     
	        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");     
	        Matcher m = p.matcher(mobiles);            
	        return m.matches();     
	    } 
	   
	    /**
	     * E_mail的验证
	     * @param email 要验证的email
	     * @return
	     */
	    public static boolean isEmail(String email){     
	     String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
	        Pattern p = Pattern.compile(str);     
	        Matcher m = p.matcher(email);            
	        return m.matches();     
	    }
	    
	    /**
		 * 传进来一个数组，将其顺序随机打乱
		 * @param arr
		 */
		public static void randomSort(int arr[]){
			Random random = new Random();
			for(int i=0; i<arr.length; i++){
				int r = random.nextInt(arr.length);
				int tmp = arr[i];
				arr[i] = arr[r];
				arr[r] = tmp;
			}
		}
	    
		/**
		 * 返回长度为len 的随机下标 eg: len=4 (2 4 3 1)
		 * @param len
		 * @return
		 */
	    public static int[] getRandomSort(int len){
			int arr[] = new int[len];
			for(int i=0; i<len; i++) arr[i] = i;
			randomSort(arr);
			return arr;
		}
	    
	    /**
	     * 处理所有的网络端口连接的情况。弹出重新连接的对话框
	     */
	    public static void showConnDialog(final Activity activity){
	    	//Log.d("tong", "showConnDialog");
	    	if(activity.isFinishing()) return;
	    	boolean isfinish = false;
	    	AlertDialog dialog = new AlertDialog.Builder(activity).setTitle("网络不稳定！").setMessage("非常抱歉,网络连接已经断开,点击重新连接,或进入无网模式").setPositiveButton("重新连接", 
	    			new DialogInterface.OnClickListener() {
	    		 ProgressDialog prodia = ProgressDialog.show(activity, "重新连接", "正在努力重新连接中...");
	    		
	    		
	    		public void onClick(DialogInterface dialog, int which) {
					try {
						//ProgressDialog dia = ProgressDialog.show(activity, "重新连接", "正在努力重新连接中...");
						if(activity instanceof StadiumActivity || activity instanceof GameActivity){
							//如果玩家是在游戏中，则在重连时发送自己的游戏信息
							if(Constants.player != null)
								ClientThread.cthread = new ClientThread(Constants.player);
							else if(Constants.player != null){
								ClientThread.cthread = new ClientThread(Constants.player);
							}else
								ClientThread.cthread = new ClientThread();
						}else{
							ClientThread.cthread = new ClientThread();
						}
						prodia.setCancelable(true);
						
						ClientThread.cthread.start();
						Toast.makeText(activity, "已经重新连接", Toast.LENGTH_SHORT)
					       .show();
						prodia.dismiss();
						
					} catch (Exception e) {
//						e.printStackTrace();
						Toast.makeText(activity, "重新连接失败", Toast.LENGTH_LONG)
					       .show();
//						Intent intent = new Intent(activity, NoNetActivity.class);
//						activity.startActivity(intent);
						prodia.dismiss();
						showConnDialog(activity);
					}
				}
				
			}).setNegativeButton("无网模式", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(activity, NoNetActivity.class);
					activity.startActivity(intent);
				}
			}).create();
	    	
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.setCancelable(false);
	    	dialog.show();
	    	dialog.setOnCancelListener(new OnCancelListener() {
				public void onCancel(DialogInterface arg0) {
					
//					Intent intent = new Intent(activity, NoNetActivity.class);
//					activity.startActivity(intent);
				}
			});
	    }
	    
	   
	     
}
