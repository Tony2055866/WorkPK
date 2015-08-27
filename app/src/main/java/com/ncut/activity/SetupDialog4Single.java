package com.ncut.activity;

import com.ncut.net.ClientThread;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

public class SetupDialog4Single extends Dialog implements
		android.view.View.OnClickListener {

	private Button okbtn, cancelbtn;
	private CheckBox isbgSoundCb, isoundCb;

	private Activity c;
	public SetupDialog4Single(Context context) {
		super(context);
		c = (Activity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setupdialog2);
		isbgSoundCb = (CheckBox) findViewById(R.id.isbgSoundCb2);
		isoundCb = (CheckBox) findViewById(R.id.isSoundCba2);
		
		isbgSoundCb.setChecked(NoNetActivity.isBgSound);
		isoundCb.setChecked(NoNetActivity.isSound);

		okbtn = (Button) findViewById(R.id.okbtn2);
		cancelbtn = (Button) findViewById(R.id.cancelbtn2);
		okbtn.setOnClickListener(this);
		cancelbtn.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.okbtn2:
			NoNetActivity.isBgSound = isbgSoundCb.isChecked();
			NoNetActivity.isSound = isoundCb.isChecked();
			if(!isbgSoundCb.isChecked()){
				NoNetActivity.mp.stop();
			}else{
				//NoNetActivity.mp.reset();
				if(!NoNetActivity.mp.isPlaying()){
					NoNetActivity.mp =MediaPlayer.create(c,R.raw.back2new);;
					NoNetActivity.mp.start();
				}
			}
				
		
			this.cancel();
			break;

		case R.id.cancelbtn2:
			this.dismiss();
			break;

		}
	}
}
