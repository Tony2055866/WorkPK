package com.ncut.activity;



import java.io.IOException;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ncut.net.ClientThread;
import com.ncut.util.MyUtil;
import com.ncut.util.SoundManage;
import com.nuct.model.Player;
/**
 * 待解决问题。服务器异常(网络)，出现卡顿
 * @author admin
 *
 */
public class LoginActivity extends MyAcitivity implements OnClickListener{
	private View view_more;
	private View menu_more;
	private ImageView img_more_up;//更多登陆项箭头图片
	private Button btn_login_regist, loginBtn;//注册按钮

	private boolean isShowMenu = false;
	private CheckBox isSoundCb, isSaveLogin;
	private EditText usernameInput,passwdInput;
	
	private  ProgressDialog prodialog;
	
	public static final int MENU_PWD_BACK = 1;
	public static final int MENU_HELP = 2;
	public static final int MENU_EXIT = 3;
	
	public static final String HOST ="127.0.0.1";
	
	public static final int PORT = 9985;
	
	public long start;
	public static boolean isSound = true, isSave = true,  isLoaclInfo=false;
	public static Player player;
	public static SoundManage sm;
	String inputpasswd;
	SharedPreferences spf;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		SysApplication.getInstance().addActivity(this);

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		//ClientThread.precess = this; //设置处理消息为当前类！
		initView();
		
		isSoundCb = (CheckBox) findViewById(R.id.isSoundCb);
		isSaveLogin = (CheckBox) findViewById(R.id.autoLoginCb);

		
		 spf = getPreferences(MODE_PRIVATE);
			isSave = spf.getBoolean("isautoLogin", true);
			String username = spf.getString("username", null);
			String passwd = spf.getString("passwd", null);
			isSound = spf.getBoolean("isSound", false);
			
			isSaveLogin.setSelected(isSave);
			isSoundCb.setSelected(isSound);
			
			//自动登录
			if(isSave && username !=null && passwd != null){
				//Log.d("tong","auto login");
				usernameInput.setText(username);
				passwdInput.setText(passwd);
				login(username,passwd);
				isLoaclInfo = true;
			}
		
	}
	
	//初始化视图
		private void initView(){

			btn_login_regist = (Button) findViewById(R.id.btn_login_regist);
			//System.out.println(btn_login_regist);
			usernameInput = (EditText) this.findViewById(R.id.usernameInput);
			passwdInput = (EditText) this.findViewById(R.id.passwdInput);
			btn_login_regist.setOnClickListener(this);
			
			loginBtn = (Button) findViewById(R.id.btn_login);
			loginBtn.setOnClickListener(this);
//			menu_more = findViewById(R.id.menu_more);
		}
	
	
	public void handleMsg(Message msg) {
		String str = (String) msg.obj;
		//Log.d("tong", "hander msg :" + str);
		String cmds[] = str.split("\\$");
		if(cmds[0].equals("login")){
			if(cmds[1].equals("true")){
				//此步存储登录用户的信息
				player = new Player(cmds, 3);
				//Log.d("tong", "用户登录成功：　" + player.getStr());
				//获取到游戏数据
				doLoginSuccess(cmds);
			}
			else{
				doLoginFail();
				prodialog.dismiss();
			}
		}
		//获取到服务信息
		else if(cmds[0].equals("server")){
			
		}
		if(cmds[0].equals("error")){
			//Log.d("tong", "显示对话框，并重新连接!");
			MyUtil.showConnDialog(LoginActivity.this);
		}
	}
	
	private void doLoginFail(){
		Builder b = new AlertDialog.Builder(LoginActivity.this).setTitle("登录失败").setMessage("请检查用户名和密码是否正确")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		b.create().show();
	}

	private void doLoginSuccess(final String[] cmds) {
		////Log.d("tong", "start:" + start+ "   end:" +end + "   " + System.currentTimeMillis());
		prodialog.dismiss();
		SharedPreferences.Editor edit = spf.edit();
		//Log.d("tong","test save spf: " + isLoaclInfo + "  ; " + inputpasswd);
		if(!isLoaclInfo && inputpasswd != null){
			edit.putBoolean("isautoLogin",true);
			edit.putString("username", cmds[3]);
			edit.putString("passwd", inputpasswd);
			edit.putBoolean("isSound", isSound);
			edit.commit();
		}
		//跳转到主Activity
		Intent intent = new Intent(LoginActivity.this, StadiumActivity.class);
		intent.putExtra("myInfo", cmds);
		startActivity(intent);

	};
	
	
	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.view_more:
//			showMoreMenu(isShowMenu);
//			break;
		case R.id.btn_login_regist:
			Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_login:
			isLoaclInfo = false;
			
			isSound = isSoundCb.isChecked();
			isSave = isSaveLogin.isChecked();
			doLogin();
			break;
		}
	}

	private void doLogin() {
		try {
			String username = usernameInput.getText().toString();
			if(username.length() < 4 || username.length() != username.trim().length() || username.contains("$")){
				Toast.makeText(this, "用户名输入有误！", Toast.LENGTH_LONG).show();
				return;
			}
			String passwd = passwdInput.getText().toString();
			if(passwd.length() < 4 || passwd.length() != passwd.trim().length() ||  passwd.contains("$")){
				Toast.makeText(this, "密码输入有误！", Toast.LENGTH_LONG).show();
				return;
			}
			login(username, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login(String username, String passwd) {
		inputpasswd = passwd;
		ClientThread.sendMsg("login" + "$" + username + "$" + passwd);
		prodialog = ProgressDialog.show(this, "登录中...", "正在努力登录中...");
		start = System.currentTimeMillis();
		
		new Thread(){
			public void run() {
				Looper.prepare();
				if(prodialog == null) return;
				while(prodialog.isShowing()){
					if(System.currentTimeMillis() - start > 6000){
						//Log.d("tong", "超时， 取消登录对话框");
						prodialog.dismiss();
						Builder b = new AlertDialog.Builder(LoginActivity.this).setTitle("登录失败").setMessage("连接服务器超时，重稍后重试")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								b.create().show();
						break;
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(ClientThread.socket != null && ClientThread.socket.isConnected())
		{
			try {
				ClientThread.socket.close();
				//Log.d("tong", "关闭socket连接!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void singleGame(View v){
		Intent intent = new Intent(this,NoNetActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		SysApplication.getInstance().exit();
	}
}
