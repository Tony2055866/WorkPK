package com.ncut.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nuct.model.Word;

public class DbUtil {
	static int cet4cnt = 4614, cet6cnt = 2089, allcnt=3071;
	public static SQLiteDatabase db;
	
	public static Word getWordInfo(String english){
		Word word = null;
		String sql="select TOPIC,ZWORD,ZWORDMEAN,ZIMAGEPATH,ZTHUMBIMAGEPATH,ZSENTENCE,ZSENTENCEVIDEO,ZATTROPTIONS from word where zword='" + english + "'";
		Cursor c = db.rawQuery(sql, null);
		int topic=0;
		if(c.moveToNext()){
			word = new Word();
			topic = c.getInt(0);
			word.word = c.getString(1);
			word.mean = c.getString(2);
			word.imgurl1 = c.getString(3);
			word.imgurl2 = c.getString(4);
			word.sentence = c.getString(5);
			word.sentensSoundUrl = c.getString(6);
			word.json = c.getString(7);
			
			sql = "select ZWORDMEAN_EN,ZWORD_ETYMA,ZWORD_DEFORMATION_IMG,ZDEFORMATION_DESC,ZSENTENCE_TRANS,ZKEYWORD_VARIANTS from word_mean where TOPIC="+topic;
			c = db.rawQuery(sql, null);
			if(c.moveToNext()){
				word.mean_en = c.getString(0);
				word.mean_etyma = c.getString(1);
				word.imgurl3 = c.getString(2);
				word.other_desc = c.getString(3);
				word.sentens_mean = c.getString(4);
				word.variants = c.getString(5);
			}
		}else{
			sql = "select * from cet4 where english='" + english + "'";
			 c = db.rawQuery(sql, null);
			if(c.moveToNext()){
				word = new Word();
				word.word = english;
				word.mean = c.getString(2);
			}
		}
		return word;
	}
	
	public static boolean loadWords(int nums, String tableName,String[] enwords, String[] zhwords){
		//Log.d("tong", "get table:" + tableName + " ,  nums:" + nums);
		if(db == null){
			return false;
		}
		String sql = null;
		Random random = new Random();
		if(!tableName.equals("word")){
			sql  = "select * from "+ tableName +" where id in(";
			Set<Integer> set = new HashSet<Integer>();
			String words ="";
			
			while(set.size() < nums){
				if(tableName.equals("cet4"))
					set.add(random.nextInt(cet4cnt)+1);
				else
					set.add(random.nextInt(cet6cnt)+1);
			}
			int cnt = 0;
			for(int index:set){
				if(cnt == 0) sql+=index;
				else sql += ","+index;
				cnt++;
			}
			sql +=")";
		}else{
		
			int start = random.nextInt(allcnt-nums); 
			sql = "select TOPIC,ZWORD,ZWORDMEAN from word limit " + start + "," + nums;
			//Log.d("tong", "sql:" + sql);
		}
		//db.update(table, values, whereClause, whereArgs)
		Cursor cursor = db.rawQuery(sql, null);
		int cnt=0;
		while(cursor.moveToNext()){
			String chinese = cursor.getString(2);
			chinese = chinese.replaceAll("\n", ";").replaceAll("\r", ";").replaceAll("；", ";")
					.replaceAll("，", ",").replaceAll(";;", ";");
			if(chinese.length() > 11){
				//chinese.indexOf(ch, fromIndex)
				int j = chinese.indexOf(';',11);
				int	k = chinese.indexOf(',',11);
				if( j != -1 && j<k) k=j;
				if(k == -1) k = j;
				if(k==-1)
						k = chinese.indexOf('.',12);
				if(k!= -1){
					chinese = chinese.substring(0,k);
				}
				//System.out.println(k);
			}
			//Log.d("tong", "test db:" + cursor.getString(1) + "  == " + chinese);
			enwords[cnt] = cursor.getString(1);
			zhwords[cnt] = chinese;
			cnt++;
		}
		return true;
	}
	

	public static List<String> getNoPassedWords(int start) {
		List<String> list = new LinkedList<String>();
		Cursor curosr = db.rawQuery("select english from notpass limit ?,20", new String[]{start+""});
		while(curosr.moveToNext()){
			list.add(curosr.getString(0));
		}
		return list;
	}
	
	public static List<String> getPassedWords(int start) {
		List<String> list = new LinkedList<String>();
		Cursor curosr = db.rawQuery("select english from learned limit ?,20", new String[]{start+""});
		while(curosr.moveToNext()){
			list.add(curosr.getString(0));
		}
		return list;
	}
	
	public static void saveGameMath(final String cmds[],int result){
		new Thread(){
			public void run() {
				
			};
		}.start();
	}
	
	public static void saveLearned(final List<String> list, int cat){
		new Thread(){
			public void run() {
				//String date;
				//for(int i=0; i<list.size(); i++){
				for(String word:list){
					//SimpleDateFormat  sDateFormat  =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					 Cursor c = db.rawQuery("select * from learned where english=?", new String[]{word});
					if(c.moveToNext()){
					}else{
						db.execSQL("insert into learned(english,timeLong) values(?,?)",new Object[]{word,System.currentTimeMillis()});
					}
					db.execSQL("delete from notpass where english='"+word+"'");
					//Log.d("tong", "pass word: " + word);
				}
			};
		}.start();
	}
	
	public static void saveNoPassed(final List<String> list, int cat){
		new Thread(){
			public void run() {
				//String date;
				//for(int i=0; i<list.size(); i++){
				for(String word:list){
					//SimpleDateFormat  sDateFormat  =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					 Cursor c = db.rawQuery("select * from notpass where english=?", new String[]{word});
					if(c.moveToNext()){
					}else{
						db.execSQL("insert into notpass(english,timeLong) values(?,?)",new Object[]{word,System.currentTimeMillis()});
					}
					db.execSQL("delete from learned where english='"+word+"'");
					//Log.d("tong", "wrong word: " + word);
				}
			};
		}.start();
	}
	
	public static SQLiteDatabase openDatabase(String path,String daFileName,Context context)
	{
		try
		{
			String databaseFilename = path + "/" + daFileName;
			File dir = new File(path);
			//if(new File(databaseFilename).exists())
			//	new File(databaseFilename).delete();
			if (!dir.exists())
				dir.mkdir();
			if (!(new File(databaseFilename)).exists())
			{
				InputStream is = context.getAssets().open(daFileName);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}

				fos.close();
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);  
			return database;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	

	public static int getTotal1() {
		Cursor c = db.rawQuery("select count(*)  from learned", null);
		if(c.moveToNext())
			return c.getInt(0);
		else
			 return 0;
	}
	
	public static int getTotal2() {
		Cursor c = db.rawQuery("select count(*)  from notpass", null);
		if(c.moveToNext())
			return c.getInt(0);
		else
			 return 0;
	}

}
