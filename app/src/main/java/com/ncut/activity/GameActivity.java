package com.ncut.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncut.dao.DbUtil;
import com.ncut.net.ClientThread;
import com.ncut.util.Constants;
import com.ncut.util.MyUtil;
import com.nuct.model.Player;
import com.nuct.view.MyDialog;

public class GameActivity extends MyAcitivity {

	String enwords[] = new String[]{
			"ago","agony","agree","agreement","agriculture","ahead","aid","aim","air","aircraft","airline","airplane","airport","alarm","alcohol","alike","alive","all","allow","alloy","almost"	
	};
	String zhwords[] = new String[]{
			"ad.以前","n.极度痛苦","vi.同意；持相同意见","n.协定，协议；同意","n.农业，农艺；农学","ad.在前；向前；提前","n.帮助，救护；助手","vi.瞄准，针对；致力","n.空气；空中；外观","n.飞机，飞行器","n.航空公司；航线","n.飞机","n.机场，航空站","n.惊恐，忧虑；警报","n.酒精，乙醇","a.同样的，相同的","a.活着的；活跃的","a.全部的 prep.全部","vt.允许，准许；任","n.合金；(金属的)成色","ad.几乎，差不多"
	};
	public static int btnSize = 45;
	public static float density;
	public LinearLayout enListLayout,zhListLayout;
	
	public static final int STADIUM = 0;
	public static final int WAITING = 1,READY=2,GAMING =3;
	public static int state;
	
	List<Button> enBtnList,zhBtnList;
	public AbsoluteLayout mainLayout;
	public Animation disAnimation, numsAnimation, disAnimationLong;
	public Queue<View> deadList;
	public ImageView startBtn,pkImg,ready01Img , ready02Img;
	public static Player opp;
	
	//int[] wordsNum4Sta = new int[]{10,15,20};
	public int W,H;
	public TextView tvOppLast,tvMyLast;
	Chronometer tvTimer;
//	tvMycur,tvOppcur;
	public Button mynameBtn, oppBtn, tipBtn;
	public static int oppLast,myLast, len;
	public static boolean first;
	private  ImageView hitImg,num10img;
	private  int continus, maxContinus , mistakes;
	public static long lastMatch;
	LinearLayout.LayoutParams commllp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	boolean award;
	
	ImageView refreshImg,simleImg;
	private int alpha;
	private ImageButton[] propBtns = new ImageButton[4];
	private boolean proBntsClickable[] = new boolean[4];
	
	public Button lastBtn;
	public int lastIndex,lastType;
	public ImageView numImg;
	
	private ArrayList<String> passedList,noPassedList;
	
	public static MediaPlayer mp;
	public static SoundPool sp;
	public static Map<Integer,Integer> spMap;
	public void onCreate(Bundle savedInstanceState)
	{
		//Log.d("tong", "GameActivity on create");
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		state = WAITING;
		setContentView(R.layout.game);
		
		passedList = new ArrayList<String>();
		noPassedList =  new ArrayList<String>();
			mp = MediaPlayer.create(this, R.raw.bgmusic);
		if(LoginActivity.isSound){
			mp.start();
			mp.setLooping(true);
		}
		sp = new SoundPool(3,  AudioManager.STREAM_MUSIC, 0);
		spMap = new HashMap<Integer, Integer>();   
		spMap.put(1, sp.load(this, R.raw.right01, 1)); 
		spMap.put(2, sp.load(this, R.raw.error, 1)); 
		spMap.put(3, sp.load(this, R.raw.item1, 1)); 
		spMap.put(4, sp.load(this, R.raw.item2, 1)); 
		spMap.put(5, sp.load(this, R.raw.win2, 1)); 
		spMap.put(6, sp.load(this, R.raw.lose, 1)); 
		//player = Constants.player;
		//staId = Constants.staId;
		myLast = oppLast = Constants.wordsNum[Constants.staId];
		initViews();
		mynameBtn = (Button) findViewById(R.id.myBnt);
		mynameBtn.setText(Constants.player.name);
		//oppPanel = (LinearLayout) this.findViewById(R.id.oppPanel);
		oppBtn = (Button) this.findViewById(R.id.oppBtn);
		
		enBtnList = new ArrayList<Button>();
		zhBtnList = new ArrayList<Button>();
		commllp.setMargins(10, 2, 10, 2); //单词的布局
		refreshImg = new ImageView(this);
		refreshImg.setBackgroundResource(R.drawable.refresh);
		AbsoluteLayout.LayoutParams abrefll = new AbsoluteLayout.LayoutParams( (int)(W*0.9),(int)(W*0.8), (int)(W*0.05) ,H/2 -(int)(W*0.5));
		refreshImg.setVisibility(View.GONE);
		mainLayout.addView(refreshImg, abrefll);
		tvTimer = (Chronometer) findViewById(R.id.tvTimer);
		tvTimer.setBase(SystemClock.elapsedRealtime());
		tvTimer.setFormat("%s");
		
		//initWords(enwords,zhwords);
		disAnimation = AnimationUtils.loadAnimation(this, R.anim.btnami);
		disAnimationLong = AnimationUtils.loadAnimation(this, R.anim.btnamilong);

		//disAnimation.setAnimationListener(aniListener);
		deadList = new LinkedList<View>();
		numsAnimation = AnimationUtils.loadAnimation(this, R.anim.nums);
		propStopClick();
		initInfo();
		
		numsAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
					hitImg.setVisibility(View.INVISIBLE);
					numImg.setVisibility(View.INVISIBLE);
					num10img.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void initInfo() {
		Intent intent = getIntent();
		if(intent == null) return;
		
		//String myinfo[] = intent.getStringArrayExtra("myInfo"); //
		String oppInfo[] = intent.getStringArrayExtra("oppInfo");
		if(oppInfo.length > 1){
			opp = new Player(oppInfo,1);
			//oppPanel.setVisibility(View.VISIBLE);
			tvOppLast.setVisibility(View.VISIBLE);
			oppBtn.setVisibility(View.VISIBLE);
			oppBtn.setText(opp.name);
			if(opp.status == READY){
				ready02Img.setVisibility(View.VISIBLE);
			}
		}
	}

	private void initViews() {
		mainLayout = (AbsoluteLayout) this.findViewById(R.id.mainLayout);
		RelativeLayout top = (RelativeLayout) this.findViewById(R.id.top);
		LinearLayout bottomLayout = (LinearLayout) this.findViewById(R.id.bottom);
		//ScrollView sv = (ScrollView) this.findViewById(R.id.scrollView);
		LinearLayout svLayout = (LinearLayout) this.findViewById(R.id.scrollViewLL);

		enListLayout = (LinearLayout) this.findViewById(R.id.enwordsLL);
		zhListLayout = (LinearLayout) this.findViewById(R.id.zhwordsLL);
		startBtn = (ImageView) this.findViewById(R.id.startBtn);
		
		tipBtn = (Button) findViewById(R.id.tipBtn);
		tipBtn.setText(Constants.tips[new Random().nextInt(Constants.tips.length)]);
		
		pkImg = (ImageView) this.findViewById(R.id.pkImg);
		propBtns[0] = (ImageButton) this.findViewById(R.id.ibtn_tip);
		propBtns[1] = (ImageButton) this.findViewById(R.id.ibtn_smile);
		propBtns[2] = (ImageButton) this.findViewById(R.id.ibtn_add);
		propBtns[3] = (ImageButton) this.findViewById(R.id.ibtn_refresh);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		 W = dm.widthPixels;
		 H = dm.heightPixels;
		density = dm.density;
		
		LinearLayout.LayoutParams enp = new LinearLayout.LayoutParams((int) (W*0.45),LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams zhp = new LinearLayout.LayoutParams((int) (W*0.55),LayoutParams.WRAP_CONTENT);
		//maingameLayout.setLayoutParams(mnp);
		
		//enListLayout.setLayoutParams(enp);
		//zhListLayout.setLayoutParams(zhp);
		ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);
		ScrollView sv2 = (ScrollView) findViewById(R.id.scrollView2);
		sv1.setVerticalScrollBarEnabled(false);
		sv2.setVerticalScrollBarEnabled(false);
		// 上中下，分布占不同比例
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(W,(int) (H * 0.12));
		LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(W,(int) (H * 0.78));
		LinearLayout.LayoutParams llp3 = new LinearLayout.LayoutParams(W,(int) (H * 0.1));
		
		sv1.setLayoutParams(enp);
		sv2.setLayoutParams(zhp);
		
		svLayout.setLayoutParams(llp2);
		top.setLayoutParams(llp);
		bottomLayout.setLayoutParams(llp3);
		
		//设置PK 图片的大小
		//RelativeLayout.LayoutParams llpimg = new RelativeLayout.LayoutParams((int)(llp.height * 0.6), (int) (llp.height * 0.6) );
		RelativeLayout.LayoutParams llpimg = (android.widget.RelativeLayout.LayoutParams) pkImg.getLayoutParams();
		llpimg.width =llpimg.height =(int) (H * 0.08);
		//llpimg.gravity = Gravity.CENTER;
		pkImg.setLayoutParams(llpimg);
		
		ready02Img = new ImageView(this);
		ready01Img = new ImageView(this);
		ready01Img.setBackgroundResource(R.drawable.ready03);
		ready02Img.setBackgroundResource(R.drawable.ready03);
		
		int readyW = (int) (2.2*(llpimg.width));
		int readyH = (int)(llpimg.width);
		AbsoluteLayout.LayoutParams al1 = new AbsoluteLayout.LayoutParams( readyW, readyH, (int)(W/2 - 100 - readyW), llp.height - llpimg.width +20);
		AbsoluteLayout.LayoutParams al2 = new AbsoluteLayout.LayoutParams(readyW ,readyH, (int)(W/2 + 100), llp.height - llpimg.width +20);
		ready01Img.setLayoutParams(al1);
		ready01Img.setVisibility(View.INVISIBLE);
		ready02Img.setLayoutParams(al2);
		ready02Img.setVisibility(View.INVISIBLE);
		mainLayout.addView(ready01Img);
		mainLayout.addView(ready02Img);
		
		tvOppLast = (TextView) this.findViewById(R.id.tvOppLast);
		tvMyLast = (TextView) this.findViewById(R.id.tvMyLast);
		
		tvMyLast.setText(myLast+"");
		tvOppLast.setText(oppLast+"");
		AbsoluteLayout.LayoutParams imglp = new AbsoluteLayout.LayoutParams( (int) (40*density), (int) (55*density),(int) (W/2 - 60*density), (int) (H * 0.125) );
		numImg = new ImageView(this);
		numImg.setLayoutParams(imglp);
		mainLayout.addView(numImg);
		numImg.setVisibility(View.INVISIBLE);
		if(hitImg == null){
			AbsoluteLayout.LayoutParams imglp2 = new AbsoluteLayout.LayoutParams( (int) (100*density), (int) (55*density), imglp.x+imglp.width, imglp.y );
			 hitImg = new ImageView(this);
			hitImg.setImageResource(R.drawable.hit);
			hitImg.setLayoutParams(imglp2);
			mainLayout.addView(hitImg);
			hitImg.setVisibility(View.INVISIBLE);
			
			AbsoluteLayout.LayoutParams num10al = new AbsoluteLayout.LayoutParams( (int) (240*density) ,  (int) (60*density), (int) (40*density) , imglp.y);
			num10img = new ImageView(this);
			num10img.setImageResource(R.drawable.num10);
			num10img.setLayoutParams(num10al);
			num10img.setVisibility(View.INVISIBLE);
			mainLayout.addView(num10img);
		}
		//hitImg.setAlpha(0);
	}
	

	
	private void initWords(String[] enwords2, String[] zhwords2) {
//		len = Math.min(enwords2.length, zhwords2.length);
		len = myLast;
		//int enIndex[] = MyUtil.getRandomSort(len);
		int zhIndex[] = MyUtil.getRandomSort(len);
		for(int i=0; i<len; i++){
			Button enBtn = createWordButton(enwords2[i]);
			enBtnList.add(enBtn);
			Button zhBtn = createWordButton(zhwords2[i]);
			zhBtnList.add(zhBtn);
		}
		for(int i=0; i<len; i++){
			zhListLayout.addView(zhBtnList.get(zhIndex[i]));
			enListLayout.addView(enBtnList.get(i));
		}
	}

	public void readyGame(View v){
		startBtn.setVisibility(View.INVISIBLE);
		ready01Img.setVisibility(View.VISIBLE);
		ClientThread.sendMsg("readygame");
		state = READY;
	}
	
	public void doStartGame(String[] cmds){
		if(mp.isPlaying()){
			mp.stop();
			if(Constants.isGameSound){
				mp = MediaPlayer.create(this, R.raw.bg);
				mp.setLooping(true);
				mp.start();
			}
		}
		findViewById(R.id.startLayout).setVisibility(View.GONE);
		continus = 0;
		maxContinus = 0;
		mistakes = 0;
		award = true;
		enwords = new String[cmds.length-1];
		zhwords = new String[cmds.length-1];
		for(int i=1; i<cmds.length; i++){
			String str[] = cmds[i].split("#");
			enwords[i-1] = str[0];
			zhwords[i-1] = str[1];
		}
		//startBtn.setVisibility(View.GONE);
		enListLayout.setVisibility(View.VISIBLE);
		zhListLayout.setVisibility(View.VISIBLE);
		this.ready01Img.setVisibility(View.INVISIBLE);
		this.ready02Img.setVisibility(View.INVISIBLE);
		state = GAMING;
		initWords(enwords,zhwords);
		propStartClick();
		
		tvTimer.setBase(SystemClock.elapsedRealtime());
		tvTimer.start();
	}
	
	@Override
	public void handleMsg(Message msg) {
		if(msg.what == 10){
			//删除所有已经 匹配的单词
			while(deadList.size() > 0){
				Button mybtn =  (Button) deadList.peek();
				mybtn.setVisibility(View.GONE);
				deadList.remove();
				//Log.d("tong", "remove Btn"  + mybtn.getText().toString());
			}
		}else if(msg.what == 50){
			 long sysTime = System.currentTimeMillis();
			  CharSequence sysTimeStr = DateFormat.format("hh:mm:ss", sysTime);
			  
		}
		else{
			String str = msg.obj.toString();
			String cmds[] = str.split("\\$");
			process(cmds);
		}
	}
	
	private void process(String[] cmds) {
		if(cmds[0].equals("error")){
			MyUtil.showConnDialog(this);
		}else if(cmds[0].equals("oppexit")){
			//处理玩家退出
			doOppExit(cmds);
		}else if(cmds[0].equals("oppin")){
			doOppIn(cmds);
		}else if(cmds[0].equals("reconn")){
			if(cmds[1].equals("success")){
				Toast.makeText(this, "重新获取游戏信息成功，您可以继续游戏~", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(this, "重新获取游戏信息失败，需要退出游戏", Toast.LENGTH_LONG).show();
				this.finish();
			}
		}else if(cmds[0].equals("allready")){
			doStartGame(cmds);
		}else if(cmds[0].equals("readygame")){
			ready02Img.setVisibility(View.VISIBLE);
		}else if(cmds[0].equals("select")){
			//tvOppcur.setText(cmds[0]);
		}else if(cmds[0].equals("match")){
			//tvOppcur.setText("");
			oppLast = Integer.parseInt(cmds[1]);
			if(oppLast < 10)
				tvOppLast.setText("0" + oppLast);
			else
				tvOppLast.setText("" + oppLast);
		}else if(cmds[0].equals("gameover")){
			doGameOver(cmds);
		}else if(cmds[0].equals("smile")){
			addSimle();
		}else if(cmds[0].equals("refresh")){
			massyGame();
		}else if(cmds[0].equals("addword")){
			addNewWord();
		}
	}

	private void doGameOver(String[] cmds) {
		for(Button btn:enBtnList)
			noPassedList.add(btn.getText().toString());
		DbUtil.saveNoPassed(noPassedList, Constants.staId);
		DbUtil.saveLearned(passedList, Constants.staId);
		tvTimer.stop();
		state=WAITING;
		gameoverDia(cmds);
	}

	private void gameoverDia(String[] cmds) {
		if(mp.isPlaying()){
			mp.stop();
		}
		
		//我赢了
		MyDialog dialog = new MyDialog(this, R.style.MyDialog, cmds);
		dialog.show();
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				resetGame();
			}
		});
	}
	
	public void resetGame(){
		tvTimer.setText("00:00");
		if(mp.isPlaying()){
			mp.stop();
			if(Constants.isGameSound){
				mp = MediaPlayer.create(this, R.raw.bgmusic);
				mp.setLooping(true);
				mp.start();
			}
		}
		award = false;
		findViewById(R.id.startLayout).setVisibility(View.VISIBLE);
		tipBtn.setText(Constants.tips[new Random().nextInt(Constants.tips.length)]);
		enListLayout.removeAllViews();
		zhListLayout.removeAllViews();
		enListLayout.setVisibility(View.GONE);
		zhListLayout.setVisibility(View.GONE);
		startBtn.setVisibility(View.VISIBLE);
		myLast = oppLast = Constants.wordsNum[Constants.staId];
		tvMyLast.setText(myLast+"");
		tvOppLast.setText(oppLast+"");
		resetDaoju();
		propStopClick();
	}
	
	private void resetDaoju() {
		int ids[] = new int[]{R.drawable.i_tip, R.drawable.i_smile, R.drawable.i_add, R.drawable.i_refresh};
		for(int i=0; i<propBtns.length; i++){
			propBtns[i].setImageResource(ids[i]);
			propBtns[i].setClickable(true);
		}
	}

	private void doOppIn(String[] cmds) {
		opp = new Player(cmds,1);//更新对手的信息
		//oppPanel.setVisibility(View.VISIBLE);
		tvOppLast.setVisibility(View.VISIBLE);
		oppBtn.setVisibility(View.VISIBLE);
		oppBtn.setText(opp.name);
		Toast.makeText(this, "玩家 " + opp.name + " 进入游戏", Toast.LENGTH_LONG).show();
	}

	private void doOppExit(String cmds[]) {
		tvOppLast.setVisibility(View.INVISIBLE);
		oppBtn.setVisibility(View.INVISIBLE);
		if(state == GAMING){
			state = WAITING;
		}
		if(cmds.length > 1){
			gameoverDia(cmds);
		}
		//oppPanel.setVisibility(View.INVISIBLE);
		Toast.makeText(this, "玩家 " + opp.name + " 退出游戏", Toast.LENGTH_LONG).show();
	
	
	}
	//
	public void massyGame(){
		if(Constants.isGameSound){
			sp.play(spMap.get(4) ,1, 1, 0, 0, 1);
		}
		stopClick();
		clearLastBtn();
		Toast.makeText(this, "对方打乱了你的单词！", Toast.LENGTH_LONG).show();
		Animation rotateAni = AnimationUtils.loadAnimation(this, R.anim.refresh);
		refreshImg.setVisibility(View.VISIBLE);
		refreshImg.startAnimation(rotateAni);
		//massyGame();
		rotateAni.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation arg0) {}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationEnd(Animation arg0) {
				refreshImg.setVisibility(View.GONE);
				startClick();
			}
		});
		enListLayout.removeAllViews();
		zhListLayout.removeAllViews();
		int enIndex[] = MyUtil.getRandomSort(enBtnList.size());
		int zhIndex[] = MyUtil.getRandomSort(enBtnList.size());
		for(int i=0; i<enIndex.length; i++){
			enListLayout.addView(enBtnList.get(enIndex[i]));
			zhListLayout.addView(zhBtnList.get(zhIndex[i]));
		}
		
	}
	
	
	public void refresh(View v){
		if(Constants.isGameSound){
			sp.play(spMap.get(3) ,1, 1, 0, 0, 1);
		}
		
//		refreshImg = (ImageView) findViewById(R.id.ibtn_refresh);
		//Log.d("tong", "tset refresh animation!");
		ClientThread.sendMsg("dispatch$refresh");
		v.setClickable(false);
		proBntsClickable[3] = false;
		addSmallImage(R.drawable.i_refresh);
		((ImageButton)v).setImageResource(R.drawable.i_refresh0);
	}
	
	public void tipClick(View view){
		////Log.d("tong", lastBtn.toString() + " " + lastIndex);
		if(lastBtn == null || lastIndex == -1){
			Toast.makeText(this, "你还没有选择一个单词", Toast.LENGTH_LONG).show();
			return;
		}else{
			//选择的英语
			if(lastType == 1 ){
				
				int index = enBtnList.indexOf(lastBtn);
				deadList.add(lastBtn);
				deadList.add(zhBtnList.get(index));
				//开始消失动画
				lastBtn.startAnimation(disAnimation);
				zhBtnList.get(index).startAnimation(disAnimation);
				
			//	enListLayout.removeView(lastBtn);
			//	zhListLayout.removeView(zhBtnList.get(index));
				
				enBtnList.remove(lastBtn);
				zhBtnList.remove((int)index);
			}else{
				int index = zhBtnList.indexOf(lastBtn);
				deadList.add(lastBtn);
				deadList.add(enBtnList.get(index));
			//	enListLayout.removeView(enBtnList.get(index));
			//	zhListLayout.removeView(lastBtn);
				
				lastBtn.startAnimation(disAnimation);
				enBtnList.get(index).startAnimation(disAnimation);
				
				zhBtnList.remove(lastBtn);
				enBtnList.remove((int)index);
			}
			doMatch(1500);
			lastBtn = null;
			lastIndex = -1;
			lastType = 0;
			//当前道具不可用
			((ImageButton)view).setImageResource(R.drawable.i_tip0);
			view.setClickable(false);
			proBntsClickable[0] = false;
		}
	}
	
	public void smileClick(View v){
		if(Constants.isGameSound){
			sp.play(spMap.get(3) ,1, 1, 0, 0, 1);
		}
		//发送
		//addSimle();
		ClientThread.sendMsg("dispatch$smile");
		addSmallImage(R.drawable.i_smile);
		v.setClickable(false);
		proBntsClickable[1] = false;
		((ImageButton)v).setImageResource(R.drawable.i_smile0);
	}
	
	public void addSmallImage(int id){
		final ImageView img = new ImageView(this);
		AbsoluteLayout.LayoutParams al = new AbsoluteLayout.LayoutParams(H/10,H/10, oppBtn.getLeft()+ 20 ,0+10);
		img.setImageResource(id);
		mainLayout.addView(img,al);
		Animation ani = AnimationUtils.loadAnimation(this, R.anim.alpha);
		img.startAnimation(ani);
		ani.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mainLayout.removeView(img);
			}
		});
	}
	
	
	public void addNewWord(){
		if(Constants.isGameSound){
			sp.play(spMap.get(4) ,1, 1, 0, 0, 1);
		}
		
		stopClick();
		clearLastBtn();
		Toast.makeText(this, "对方给你添加了几个单词，要不要谢谢他啊？", Toast.LENGTH_SHORT).show();
//		enListLayout.addView(child, index);
		Random r = new Random();
		int k = new Random().nextInt(3)+1;
		int[] ids = new int[]{R.drawable.wordadd1, R.drawable.wordadd2, R.drawable.wordadd3};
		final ImageView imgAdd = new ImageView(this);
		AbsoluteLayout.LayoutParams al = new AbsoluteLayout.LayoutParams(W/2,W/6,W/4,H/2-W/2);
		imgAdd.setImageResource(ids[k-1]);
		mainLayout.addView(imgAdd,al);
		Animation ani = AnimationUtils.loadAnimation(this, R.anim.nums);
		imgAdd.startAnimation(ani);
		ani.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {}
			public void onAnimationRepeat(Animation animation) {}
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mainLayout.removeView(imgAdd);
			}
		});
		for(int i=0; i<k; i++){
			int index = r.nextInt(enBtnList.size());
			Button enBtn = createWordButton(enwords[len+i]);
			enBtnList.add(enBtn);
			enListLayout.addView(enBtn, index);
			
			 index = r.nextInt(zhBtnList.size());
			Button zhBtn = createWordButton(zhwords[len+i]);
			zhBtnList.add(zhBtn);
			zhListLayout.addView(zhBtn,index);
		}
		//Log.d("tong", "after add: " + enBtnList.size() + " , " + zhBtnList.size());
		len += k; //更新k
		startClick();
		resetMyLastTv();
	}
	
	private void clearLastBtn() {
		if(lastBtn != null){
			lastBtn.setBackgroundResource(R.drawable.graybtn1);
			lastBtn = null;
			lastType = 0;
			lastIndex = -1;
		}
	}
	
	private Button createWordButton(String word){
		Button enBtn = new Button(this);
		enBtn.setMinHeight(btnSize);
		enBtn.setGravity(Gravity.CENTER);
		enBtn.setMinimumHeight((int) (btnSize*density));
		enBtn.setText(word);
		enBtn.setTextSize((float) 15);
		 enBtn.setLayoutParams(commllp);
		enBtn.setBackgroundResource(R.drawable.graybtn1);
		//,new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
		enBtn.setOnClickListener(listener);
		return enBtn;
	}
	
	public void addnewWordClick(View v){
		if(Constants.isGameSound){
			sp.play(spMap.get(3) ,1, 1, 0, 0, 1);
		}
		ClientThread.sendMsg("dispatch$addword");
		addSmallImage(R.drawable.i_add);
		v.setClickable(false);
		proBntsClickable[2] = false;
		((ImageButton)v).setImageResource(R.drawable.i_add0);
	}
	
	public void addSimle(){
		if(Constants.isGameSound){
			sp.play(spMap.get(4) ,1, 1, 0, 0, 1);
		}
		stopClick();
		clearLastBtn();
		Toast.makeText(this, "对方向你施展笑脸，赶快摩擦笑脸消除它！", Toast.LENGTH_LONG).show();
		simleImg = new ImageView(this);
		simleImg.setImageResource(R.drawable.smile);
		simleImg.setScaleType(ScaleType.FIT_XY);
		AbsoluteLayout.LayoutParams al = new AbsoluteLayout.LayoutParams( W,(int)(W*1.2), 0,H/2-W/4*3);
		mainLayout.addView(simleImg,al);
		simleImg.setOnTouchListener(touchListener);
		alpha = 250;
	}
	
	OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View arg0, MotionEvent event) {
			if(arg0 == simleImg){
				//Log.d("tong", " move ......." + event.getAction());
				if(event.getAction() == MotionEvent.ACTION_MOVE){
					//Log.d("tong", " move .......");
					simleImg.setAlpha(alpha);
					alpha -= 4;
					if(alpha<= 50){
						mainLayout.removeView(simleImg);
						startClick();
					}	
					return false;
				}
			}
			return true;
		}
	};
	
	/**
	 * 处理点击单词的事件
	 */
	public OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			int index = -1, flag = 0;
			index = enBtnList.indexOf(v);
			Button mybtn = (Button)v;
			//不是英文单词
			if(index == -1){
				flag = 2;
				index = zhBtnList.indexOf(v);
			}else{
				flag = 1;
			}
			if(index == -1){
				//Log.d("tong", "Logic error!  no word match!");
			}
			int type = 0; // 
			if(lastBtn != null){
				lastBtn.setBackgroundResource(R.drawable.graybtn1);
				//单词找对了
				if(index == lastIndex && flag != lastType){
					//Log.d("tong", "right!! match! ");
					boolean match = false;
					if( flag == 1 ){ //点击的英文单词
						
						passedList.add(((Button)v).getText().toString());

						deadList.add(v);
						deadList.add(zhBtnList.get(lastIndex));
						//开始消失动画
						v.startAnimation(disAnimation);
						zhBtnList.get(lastIndex).startAnimation(disAnimation);
						
						enBtnList.remove(v);
						zhBtnList.remove((int)lastIndex);
						
						match = true;
					}else if(flag == 2){
						passedList.add(enBtnList.get(lastIndex).getText().toString());

						deadList.add(v);
						deadList.add(enBtnList.get(lastIndex));
						v.startAnimation(disAnimation);
						enBtnList.get(lastIndex).startAnimation(disAnimation);
						match = true;
						zhBtnList.remove(v);
						enBtnList.remove((int)lastIndex);
					}
					if(match){
						lastBtn.setBackgroundResource(R.drawable.graybtn2);
						mybtn.setBackgroundResource(R.drawable.graybtn2);
						doMatch(600);
					}
					type = 1;
				}else if(flag == lastType){ //
				}
				else{
					continus = 0;
					mistakes ++;
					//Log.d("tong", "选错！");
					type = 1;//选错了
					if(Constants.isGameSound){
						sp.play(spMap.get(2) ,1, 1, 0, 0, 1);
					}
				}
			}
			
			if(type == 0){
				lastBtn = (Button) v;
				lastIndex = index;
				lastType = flag; //上次点 的单词 还是翻译 ？
				lastBtn.setBackgroundResource(R.drawable.graybtn2);
			}else{
				////Log.d("tong", "Save Last: null " );
				//tvMycur.setText("");
				lastBtn = null;
				lastIndex = -1;
				lastType = 0;
			}
		}

		
	};
	
	/**
	 * 处理单词匹配正确的情况
	 * @param d 
	 */
	public void doMatch(int d) {
		if(Constants.isGameSound){
			sp.play(spMap.get(1) ,1, 1, 0, 0, 1);
		}
		newMyTiemr(); //移除单词按钮
		if(continus >= 1 && System.currentTimeMillis()-lastMatch < 5000){
			continus++;
			if(continus < 10){
				numImg.setVisibility(View.VISIBLE);
				numImg.setImageResource(Constants.imgsid[continus-2]);
				hitImg.setVisibility(View.VISIBLE);
				numImg.startAnimation(numsAnimation);
				hitImg.startAnimation(numsAnimation);
			}else{
				num10img.setVisibility(View.VISIBLE);
				num10img.startAnimation(numsAnimation);
			}
//			new Timer().schedule(new TimerTask() {
//				@Override
//				public void run() {
//					if(System.currentTimeMillis() - lastMatch < 600){
//						try {
//							Thread.sleep(300);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//					Message msg = new Message();
//					msg.what = 20;
//					msg.arg1 = continus-2;
//					handler.sendMessage(msg);
//				}
//			}, 550);
		}else{
			continus = 1;
		}
		if(award && continus >= 6 && Math.random() * ( 6.0 /continus) < 0.3){
			addDaoju(-1);
		}
		
		if(continus > maxContinus) maxContinus = continus;
		//发送我消除单词的消息
		resetMyLastTv();
		
		ClientThread.sendMsg("match$" + myLast + "$" + maxContinus);
		if(myLast <= 0){
			
		}
		
		lastMatch = System.currentTimeMillis();
		
	}
	
	private void resetMyLastTv(){
		myLast = enBtnList.size();
		if(myLast < 10)
			tvMyLast.setText("0"+myLast);
		else
			tvMyLast.setText(""+myLast);
	}
	
	private void addDaoju(int i) {
		int ids[] = new int[]{R.drawable.i_tip, R.drawable.i_smile, R.drawable.i_add, R.drawable.i_refresh};
		int index[] = new int[]{2,1,3,0};
		if(i == -1){
			for(int j=0; j<4; j++){
				if(proBntsClickable[index[j]] == false){
					i = index[j];
					proBntsClickable[i] = true;
					break;
				}
			}
		}
		if(i == -1) return;
		award = true;
		propBtns[i].setImageResource(ids[i]);
		propBtns[i].setClickable(true);
		Toast.makeText(this, "获得道具 " + i, Toast.LENGTH_LONG).show();
	}

	static int delay = 600;
	public void newMyTiemr(){
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				 Message message = new Message();      
		            message.what = 10;      
		            handler.sendMessage(message);   
			}
		}, delay);
	}

	@Override
	public void onBackPressed() {
		if(state != GAMING){
			super.onBackPressed();
//			Intent intent = new Intent(this,Constants.class);
//			startActivity(intent);
			if(mp.isPlaying()) mp.stop();
			ClientThread.sendMsg("exitStadium");
		}else{
			new AlertDialog.Builder(GameActivity.this).setTitle("放弃游戏").setMessage("确认要放弃游戏吗？ 游戏中退出将扣除一定积分！ ")
			.setPositiveButton("退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ClientThread.sendMsg("exitStadium");
					if(mp.isPlaying()) mp.stop();
					finish();
				}
			}).create().show();
		}
	}
	
	public void propStopClick(){
		for(int i=0; i<propBtns.length; i++) propBtns[i].setClickable(false);
	}
	public void propStartClick(){
		for(int i=0; i<propBtns.length; i++){
			proBntsClickable[i] = true;
			propBtns[i].setClickable(true);
		}
	}
	
	public void stopClick(){
		for(int i=0; i<enBtnList.size(); i++){
			enBtnList.get(i).setOnClickListener(null);
			zhBtnList.get(i).setOnClickListener(null);
		}
	}
	public void startClick(){
		for(int i=0; i<enBtnList.size(); i++){
			enBtnList.get(i).setOnClickListener(listener);
			zhBtnList.get(i).setOnClickListener(listener);
		}
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		//Log.d("tong", "Game finished");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(!first){
			//Log.d("tong", "test width x,y: " +  tvOppLast.getLeft() + ";  " + tvOppLast.getWidth());
			oppBtn.setWidth((int) (W - tvOppLast.getLeft() - tvOppLast.getWidth() - 15*density));
			first = true;
		}
	}
	public void showMyINfo(View v){
		if(Constants.player == null) return;
		int gameCnt = 0, passCnt = 0, notPassCnt = 0;
		passCnt = DbUtil.getTotal1();
		notPassCnt = DbUtil.getTotal2();
		String info = "用户名:" + Constants.player.name + "\n" +
		"邮箱 :" + Constants.player.email + "\n" +
		"学校:" + Constants.player.school + "\n" +
		"积分:" + Constants.player.points + "\n" +
		"游戏次数:"+ 0 +" 次\n" +
		"已通过单词:" + passCnt + " 个\n"+
		"未通过单词："+ notPassCnt + " 个\n";
		new AlertDialog.Builder(this).setTitle("个人信息").setMessage(info)
		.create().show();
	}
	
	public void showOppINfo(View v){
		//Log.d("tong", "show info " + player);
		if(opp == null) return;
		int gameCnt = 0, passCnt = 0, notPassCnt = 0;
		String info = "用户名:" + opp.name + "\n" +
		"学校:" + opp.school + "\n" +
		"积分:" + opp.points + "\n" ;
		new AlertDialog.Builder(this).setTitle("对方信息").setMessage(info)
		.create().show();
	}
}
