package com.ncut.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ncut.net.ClientThread;
/**注册界面activity*/
public class RegisterActivity extends MyAcitivity implements android.view.View.OnClickListener{
	public static final int REGION_SELECT = 1;
	private TextView showMsgTV,tv_region_modify,tv_region_show,tv_top_title;
	private Button btn_title_left,btn_title_right,btn_send_code;
	private CheckBox chk_agree;
	private ProgressDialog proDialog;
	private EditText usernameInput,passwdInput,repasswdInput, schoolInput;
	RadioGroup rg;
	long start ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		initView();
	}
	
	private void initView(){
		tv_top_title = (TextView) findViewById(R.id.tv_top_title);
		btn_title_left = (Button) findViewById(R.id.btn_title_left);
		btn_title_left.setOnClickListener(this);
		showMsgTV = (TextView) this.findViewById(R.id.showMsgTV);
		usernameInput = (EditText) this.findViewById(R.id.usernameInput);
		passwdInput = (EditText) this.findViewById(R.id.passwdInput);
		repasswdInput = (EditText) this.findViewById(R.id.repasswdInput);
		schoolInput = (EditText) this.findViewById(R.id.shcool);
		rg = (RadioGroup) this.findViewById(R.id.gender);
		btn_send_code = (Button) findViewById(R.id.registerBtn);
		btn_send_code.setOnClickListener(this);
	
	}
	


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
//		case R.id.tv_region_modify:
//			showDialog(REGION_SELECT);//显示列表对话框的方法
//			break;
		case R.id.btn_title_left:
			RegisterActivity.this.finish();
			break;
		case R.id.registerBtn:
			String username = usernameInput.getText().toString();
			if(username.length() != username.trim().length() || username.contains("$")){
				showMsgTV.setText("用户名不能包含空格哦！");
				return;
			}
			String passwd = passwdInput.getText().toString();
			if(username.equals("") || passwd.equals("")){
				showMsgTV.setText("用户名或密码不能为空噢");
				return;
			}
			if(username.length() < 4 || passwd.length() < 4){
				showMsgTV.setText("用户名或密码不能太短了！");
				return;
			}
			if(passwd.length() != passwd.trim().length() ||  passwd.contains("$")){
				showMsgTV.setText("密码不能包含空格哦！");
				return;
			}
			if(!passwd.equals( repasswdInput.getText().toString() )){
				showMsgTV.setText("两次输入的密码不一致哦！");
				return;
			}
			
			String school = schoolInput.getText().toString();
			if(school.contains("$")){
				showMsgTV.setText("输入的学校名不要含特殊字符！");
				return;
			}
			View sv = findViewById(rg.getCheckedRadioButtonId());
			int index = rg.indexOfChild(sv);
			String email="";
			showMsgTV.setText("");
			ClientThread.sendMsg("register" + "$" + username + "$" + passwd + "$" + email +"$" + school + "$" + index);
			proDialog = ProgressDialog.show(this, "注册", "正在努力的注册您的信息...");
			start = System.currentTimeMillis();
			proDialog.show();
			
			 new Thread(){
				public void run() {
					if(proDialog == null) return;
					while(proDialog.isShowing()){
						if(System.currentTimeMillis() - start > 4000){
							//Log.d("tong", "超时， 取消登录对话框");
							proDialog.dismiss();
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
	}
	
	@Override
	public void handleMsg(Message msg){
		String str = msg.obj.toString();
		String cmds[] = str.split("\\$");
		if(cmds[0].equals("register")){
			proDialog.dismiss();
			if(cmds[1].equals("true")){
				//Log.d("tong", " 登录成功! ");
				
				Builder builder = new AlertDialog.Builder(RegisterActivity.this).setTitle("注册成功").setMessage("恭喜您注册成功！\n 现在转入到登录界面")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						onBackPressed();
					}
				});
				builder.create().show();
			}else if(cmds[1].equals("false")){
				//Log.d("tong", "用户名已经存在");
				showMsgTV.setText("用户名已经存在!");
			}else{
				showMsgTV.setText("出现未知错误，请重新编辑信息,或避免用中文!");
			}
		}
	}

	

}
