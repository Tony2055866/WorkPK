package com.ncut.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ncut.util.Constants;

public class SetupDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Button okbtn, cancelbtn;
	private CheckBox isbgSoundCb, isoundCb, isautologinCb;
	private EditText emailtx, schooltx;
	private RadioGroup rg;

	private Activity c;
	public SetupDialog(Context context) {
		super(context);
		c = (Activity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupdialog);
		isbgSoundCb = (CheckBox) findViewById(R.id.isbgSoundCb);
		isoundCb = (CheckBox) findViewById(R.id.isSoundCba);
		isautologinCb = (CheckBox) findViewById(R.id.issavecb);

		isbgSoundCb.setChecked(LoginActivity.isSound);
		isautologinCb.setChecked(LoginActivity.isSave);
		isoundCb.setChecked(Constants.isGameSound);

		emailtx = (EditText) findViewById(R.id.emailtx);
		schooltx = (EditText) findViewById(R.id.shcooltx);
		rg = (RadioGroup) findViewById(R.id.genderrg);
		if (Constants.player != null) {
			emailtx.setText(Constants.player.email);
			schooltx.setText(Constants.player.school);
			rg.check(rg.getChildAt(Constants.player.gender).getId());
		} else {
			emailtx.setVisibility(View.GONE);
			schooltx.setVisibility(View.GONE);
			rg.setVisibility(View.GONE);
		}
		
		okbtn = (Button) findViewById(R.id.okbtn);
		cancelbtn = (Button) findViewById(R.id.cancelbtn);
		okbtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okbtn:
			LoginActivity.isSound = isbgSoundCb.isChecked();
			//Log.d("tong", "isbgSoundCb 1 :" + LoginActivity.isSound);
			Constants.isGameSound = isoundCb.isChecked();
			LoginActivity.isSave = isautologinCb.isChecked();
			
			if(isoundCb.isChecked() != LoginActivity.isSave){
				SharedPreferences spf=	c.getPreferences(Activity.MODE_PRIVATE);
				SharedPreferences.Editor edit = spf.edit();
				edit.putBoolean("isautoLogin", isautologinCb.isChecked());
				edit.commit();
			}
			
			View sv = findViewById(rg.getCheckedRadioButtonId());
			int index = rg.indexOfChild(sv);
			//ClientThread.sendMsg("update$" + emailtx.getText().toString() + "$"
		//			+ schooltx.getText().toString() + "$" + index);
			this.cancel();
			break;

		case R.id.cancelbtn:
			this.dismiss();
			break;

		}
	}
}
