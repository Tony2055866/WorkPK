package com.ncut.activity;

import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ncut.dao.DbUtil;
import com.nuct.model.Word;

public class Review extends TabActivity implements OnItemClickListener{

	
	private List<String> passwords;
	private List<String> nopasswords ;
	int start1=0,start2=0,limit=20;
	int total1,total2;
	TabHost tab;
	ListView listView1 , listView2;
	public static Word word;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.review);
		 tab = getTabHost();
		 
		 tab.setBackgroundColor(0x0000ff);
		 
		 total1 = DbUtil.getTotal1();
		 total2 = DbUtil.getTotal2();
		LayoutInflater.from(this).inflate(R.layout.review, tab.getTabContentView(),true);
		
		tab.addTab(tab.newTabSpec("tab1").setIndicator("已通过单词("+total1+")").setContent(R.id.enLayout));
		tab.addTab(tab.newTabSpec("tab2").setIndicator("未通过单词(" + total2+")").setContent(R.id.zhLayout));
		listView1= (ListView) findViewById(R.id.enList);
		 listView2 = (ListView) findViewById(R.id.zhList);
		 loadData1(true);
		 loadData2(true);
		
		tab.setCurrentTab(0);
		listView1.setOnItemClickListener(this);
		listView2.setOnItemClickListener(this);
	}
	
	public void loadData1(boolean first){
		passwords = DbUtil.getPassedWords(start1);
		if(passwords.size() == 0 && !first){
			Toast.makeText(this, "已到最后", Toast.LENGTH_SHORT).show();
		}else{
			ArrayAdapter<String> data1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, passwords);
			listView1.setAdapter(data1);
		}
	}
	
	public void loadDataUp(View v){
		if(tab.getCurrentTab() == 0){
			if(start1 < 20 )return;
			start1 -= 20;
			loadData1(false);
		}
		else{
			if(start2 < 20 )return;
			start2 -= 20;
			loadData2(false);
		}
	}
	
	private void loadData2(boolean first) {
		
		nopasswords = DbUtil.getNoPassedWords(start2);
		if(nopasswords.size() == 0 && !first){
			Toast.makeText(this, "已到最后", Toast.LENGTH_SHORT).show();
		}
			else{
				ArrayAdapter<String> data2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nopasswords);
				listView2.setAdapter(data2);
			}
	}
	
	public void loadDataDown(View v){
		if(tab.getCurrentTab() == 0){
			if(passwords.size() < limit) return;
			start1 += 20;
			loadData1(false);
		}
		else{
			if(nopasswords.size() < limit) return;
			start2 += 20;
			loadData2(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
		TextView tv = (TextView) v;
		word = DbUtil.getWordInfo(tv.getText().toString());
		Intent intent = new Intent(this,WordActivity.class);
		startActivity(intent);
		
	}
	
	
}
