package com.ncut.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ncut.util.DownLoad;
import com.ncut.util.FileUtil;

public class WordActivity extends Activity {
	private TextView tvWord,tvWordMean,tvSentens,tvSentensMean,tvEtyma,tvEnMean;
	private ImageView sentensImg,soundImg2;
	File f,soundFile,senSoundFile;
	boolean finished1,finished2;
	private MediaPlayer mediaPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordinfo);
		LinearLayout ll = (LinearLayout) findViewById(R.id.infolist);
		finished1 = false;
		finished2 = false;
		LinearLayout.LayoutParams llpara = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		llpara.setMargins(4, 4, 4, 4);
		
		f = soundFile = senSoundFile = null;
		
		tvWord = (TextView) findViewById(R.id.wordTV);
		tvWordMean = (TextView) findViewById(R.id.wordMeanTV);
		tvSentens = (TextView) findViewById(R.id.wordSentenceTV);
		tvSentensMean = (TextView) findViewById(R.id.wordSentenceMean);
		tvEtyma = (TextView) findViewById(R.id.wordMean_etyma);
		tvEnMean = (TextView) findViewById(R.id.wordenMean);

		sentensImg = (ImageView) findViewById(R.id.sentensImg);
		soundImg2 = (ImageView) findViewById(R.id.sound2);
		if(Review.word != null){
			tvWord.setText(Review.word.word);
			tvWordMean.setText("词义: " + Review.word.mean);
			
			String wordurlmp3 = "http://baicizhan0.qiniudn.com/word_audios/" + Review.word.word + ".mp3";
			String filenamew = Review.word.word + ".mp3";
			new MyDown(wordurlmp3, filenamew, 1).start();
			//Log.d("tong", wordurlmp3);

			if(Review.word.sentence != null){
				tvEnMean.setText("解释: " + Review.word.mean_en);
				tvSentens.setText("例句：\n"+ Review.word.sentence);
				tvSentensMean.setText( Review.word.sentens_mean);
				tvEtyma.setText("词根: "+Review.word.mean_etyma);
				
				if(Review.word.imgurl1 != null){
					//Log.d("tong", "http://baicizhan0.qiniudn.com" + Review.word.imgurl1);
					String url = "http://baicizhan0.qiniudn.com" + Review.word.imgurl1;
					String filename = url.substring(url.lastIndexOf('/'));
					new MyDown(url, filename, 0).start();
					//UrlImageViewHelper.setUrlDrawable(sentensImg, "http://baicizhan0.qiniudn.com" + Review.word.imgurl1);
				}else{
					sentensImg.setVisibility(View.GONE);
				}
			
				if(Review.word.sentensSoundUrl != null){
					String url2 =  "http://baicizhan1.qiniudn.com" + Review.word.sentensSoundUrl;
					//Log.d("tong",url2);
					int last = Review.word.sentensSoundUrl.lastIndexOf('/');
					String filename2 = Review.word.sentensSoundUrl.substring(last+1);
					new MyDown(url2, filename2, 2).start();
				}
			}else{
				soundImg2.setVisibility(View.INVISIBLE);
			}
			
		}else{
			soundImg2.setVisibility(View.INVISIBLE);
			//TextView tv1 = new TextView(this);
			tvWordMean.setText("抱歉，由于词库不足，为找到该单词的详细信息");
			//ll.addView(tv1,llpara);
		}
		//TextView;
	}
	
	Handler h = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 10){
				if(f!=null && f.exists()){
				    Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
				    //Log.d("tong", "get img:" + f.getAbsolutePath());
				    sentensImg.setImageBitmap(myBitmap);
				}
			}
		};
	};
	
	public void playeSentensSound(View v){
		if(finished2 && soundFile != null){
			if(mediaPlayer != null && mediaPlayer.isPlaying()) return;
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(senSoundFile.getAbsolutePath());
				mediaPlayer.prepare();   
				mediaPlayer.start();
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void wordSoundPlay(View v){
		if(finished1 && soundFile != null  ){
			if(mediaPlayer != null && mediaPlayer.isPlaying()) return;
			mediaPlayer = new MediaPlayer();
			try {
				mediaPlayer.setDataSource(soundFile.getAbsolutePath());
				mediaPlayer.prepare();   
				mediaPlayer.start();
			}  catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	class MyDown extends Thread{
		String url;
		int t;
		String filename;
		public MyDown(String url,String filename,int type){
			this.url = url;
			t = type;
			this.filename = filename;
		}
		
		@Override
		public void run() {
			DownLoad down = new DownLoad();
			FileUtil fileUtils = new FileUtil();
			if(t== 0){
				String path = "wordpk/imgs1/";
				f = new File(fileUtils.getSDPATH() + path+filename);
				int result=down.download(url,
						path,filename);
				
				if(result != -1){
					h.sendEmptyMessage(10);
				}
			}else if(t == 1){
				String path = "wordpk/audios/";
				soundFile = new File(fileUtils.getSDPATH() + path+filename);
				//Log.d("tong","word: " + soundFile.getAbsolutePath());
				int result=down.download(url,
						path,filename);
				finished1 = true;
				if(result != -1){
					h.sendEmptyMessage(20);
				}
			}else{ //sentence
				String path = "wordpk/sentence/";
				
				senSoundFile = new File(fileUtils.getSDPATH() + path+filename);
				//Log.d("tong","sentence : " + senSoundFile.getAbsolutePath());
				int result=down.download(url,
						path,filename);
				finished2 = true;
				if(result != -1){
					h.sendEmptyMessage(30);
				}
			}

		}
	}
	
}
