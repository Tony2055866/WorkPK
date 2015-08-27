package com.ncut.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ncut.dao.DbUtil;
import com.ncut.util.Constants;
import com.ncut.util.MyUtil;
import com.nuct.model.Player;
import com.nuct.view.MyDialog;

public class SingleGame extends Activity {
	String enwords[] = new String[]{
			"ago","agony","agree","agreement","agriculture","ahead","aid","aim","air","aircraft","airline","airplane","airport","alarm","alcohol","alike","alive","all","allow","alloy","almost"	
	};
	String zhwords[] = new String[]{
			"ad.以前","n.极度痛苦","vi.同意；持相同意见","n.协定，协议；同意","n.农业，农艺；农学","ad.在前；向前；提前","n.帮助，救护；助手","vi.瞄准，针对；致力","n.空气；空中；外观","n.飞机，飞行器","n.航空公司；航线","n.飞机","n.机场，航空站","n.惊恐，忧虑；警报","n.酒精，乙醇","a.同样的，相同的","a.活着的；活跃的","a.全部的 prep.全部","vt.允许，准许；任","n.合金；(金属的)成色","ad.几乎，差不多"
	};
	public static int btnSize = 45;
	public LinearLayout enListLayout,zhListLayout;
	public Button mynameBtn;
	private ImageView StartBtniv;
	private  int continus, maxContinus , mistakes;
	public int W,H, len;
	LinearLayout.LayoutParams commllp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	
	private ArrayList<String> passedList,noPassedList;
	
	float density;
	public TextView tvOppLast,tvMyLast;
	List<Button> enBtnList,zhBtnList;
	public AbsoluteLayout mainLayout;
	public Animation disAnimation, numsAnimation, disAnimationLong;
	public Queue<View> deadList;
	public ImageView startBtn,pkImg;
	public static Player player;
	public Button lastBtn;
	public int lastIndex,lastType;
//	public ImageView ivs[] = new ImageView[8],;
	ImageView num10img,hitImg, numImg;
	private long lastMatch;
	public static int[] wordsNum = new int[]{12,18,24,12,18,24,12,18,24};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlegame);
		passedList = new ArrayList<String>();
		noPassedList = new ArrayList<String>();
		player = Constants.player;
		//staId = StadiumActivity.staId;
		mynameBtn = (Button) findViewById(R.id.myBnt);
		if(player != null)
			mynameBtn.setText(player.name);
		else
			mynameBtn.setText("guest");
		//oppPanel = (LinearLayout) this.findViewById(R.id.oppPanel);
	
		enBtnList = new ArrayList<Button>();
		zhBtnList = new ArrayList<Button>();
		commllp.setMargins(10, 2, 10, 2); //单词的布局
		
		//initWords(enwords,zhwords);
		disAnimation = AnimationUtils.loadAnimation(this, R.anim.btnami);
		disAnimationLong = AnimationUtils.loadAnimation(this, R.anim.btnamilong);
		
		//disAnimation.setAnimationListener(aniListener);
		deadList = new LinkedList<View>();
		numsAnimation = AnimationUtils.loadAnimation(this, R.anim.nums2);
	
		mainLayout = (AbsoluteLayout) this.findViewById(R.id.mainLayout);
		RelativeLayout top = (RelativeLayout) this.findViewById(R.id.top);
		LinearLayout bottomLayout = (LinearLayout) this.findViewById(R.id.bottom);
		LinearLayout svLayout = (LinearLayout) this.findViewById(R.id.scrollViewLL);
		enListLayout = (LinearLayout) this.findViewById(R.id.enwordsLL);
		zhListLayout = (LinearLayout) this.findViewById(R.id.zhwordsLL);
		pkImg = (ImageView) this.findViewById(R.id.pkImg);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		 W = dm.widthPixels;
		 H = dm.heightPixels;
		density = dm.density;
		
		ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);
		ScrollView sv2 = (ScrollView) findViewById(R.id.scrollView2);
		sv1.setVerticalScrollBarEnabled(false);
		sv2.setVerticalScrollBarEnabled(false);
		//FrameLayout.LayoutParams enp = new FrameLayout.LayoutParams((int) (W*0.45),LayoutParams.WRAP_CONTENT);
		//FrameLayout.LayoutParams zhp = new FrameLayout.LayoutParams((int) (W*0.55),LayoutParams.WRAP_CONTENT);
		//maingameLayout.setLayoutParams(mnp);
		LinearLayout.LayoutParams enp = new LinearLayout.LayoutParams((int) (W*0.45),LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams zhp = new LinearLayout.LayoutParams((int) (W*0.55),LayoutParams.WRAP_CONTENT);
		sv1.setLayoutParams(enp);
		sv2.setLayoutParams(zhp);
		//enListLayout.setLayoutParams(enp);
		//zhListLayout.setLayoutParams(zhp);
		
		// 上中下，分布占不同比例
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(W,(int) (H * 0.12));
		LinearLayout.LayoutParams llp2 = new LinearLayout.LayoutParams(W,(int) (H * 0.78));
		LinearLayout.LayoutParams llp3 = new LinearLayout.LayoutParams(W,(int) (H * 0.1));
		
		svLayout.setLayoutParams(llp2);
		top.setLayoutParams(llp);
		bottomLayout.setLayoutParams(llp3);
		
		//设置PK 图片的大小
		//RelativeLayout.LayoutParams llpimg = new RelativeLayout.LayoutParams((int)(llp.height * 0.6), (int) (llp.height * 0.6) );
		RelativeLayout.LayoutParams llpimg = (android.widget.RelativeLayout.LayoutParams) pkImg.getLayoutParams();
		llpimg.width =llpimg.height =(int) (H * 0.08);
		//llpimg.gravity = Gravity.CENTER;
		pkImg.setLayoutParams(llpimg);
		
		tvMyLast = (TextView) this.findViewById(R.id.tvMyLast);
		tvMyLast.setText(""+ wordsNum[NoNetActivity.staId]);
		
		AbsoluteLayout.LayoutParams imglp = new AbsoluteLayout.LayoutParams( (int) (40*density), (int) (55*density),(int) (W/2 - 60*density), (int) (H * 0.125) );
		numImg = new ImageView(this);
		numImg.setLayoutParams(imglp);
		mainLayout.addView(numImg);
		numImg.setVisibility(View.INVISIBLE);
//		for(int i=0; i<ivs.length; i++){
//			if(ivs[i] == null){
//				ivs[i] = new ImageView(this);
//				ivs[i].setImageResource(StadiumActivity.imgsid[i]);
//				ivs[i].setLayoutParams(imglp);
//				mainLayout.addView(ivs[i]);
//				ivs[i].setVisibility(View.INVISIBLE);
//			}
//		}
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
	
	private void initWords(String[] enwords2, String[] zhwords2) {
		len = wordsNum[NoNetActivity.staId];
		enwords2 = new String[len+5];
		zhwords2 = new String[len+5];
		String tableName="cet4";
		if(NoNetActivity.staId > 2) tableName="cet6";
		if(NoNetActivity.staId > 5) tableName="word";
		DbUtil.loadWords(len+5, tableName, enwords2, zhwords2);
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
	
	public Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 20){
				if(msg.arg1 >= 8){
					num10img.setVisibility(View.INVISIBLE);
				}else{
					hitImg.setVisibility(View.INVISIBLE);
					numImg.setVisibility(View.INVISIBLE);
					//ivs[msg.arg1].setVisibility(View.INVISIBLE);
				}
			}else{
				while(deadList.size() > 0){
					Button mybtn =  (Button) deadList.peek();
					mybtn.setVisibility(View.GONE);
					deadList.remove();
					////Log.d("tong", "remove Btn"  + mybtn.getText().toString());
				}
			}
			
		};
	};
	
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
			////Log.d("tong", "Selected: " + ((Button)v).getText().toString() + "  " + index + "  " + flag);
			
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
				}
			}
			if(type == 0){
				lastBtn = (Button) v;
				lastIndex = index;
				lastType = flag; //上次点 的单词 还是翻译 ？
				lastBtn.setBackgroundResource(R.drawable.graybtn2);
				
			}else{
				////Log.d("tong", "Save Last: null " );
				lastBtn = null;
				lastIndex = -1;
				lastType = 0;
			}
		}
	};
	
	private void doMatch(int d) {
		// TODO Auto-generated method stub
		resetMyLastTv();
		if(enBtnList.size() == 0){
//			new MyDialog(context, theme, cmds).show();
		}
		delay = d;
		newMyTiemr(); //移除单词按钮
		//Log.d("test", "continus :" + continus);
		if(continus >= 1 && System.currentTimeMillis()-lastMatch < 5000){
			continus++;
			final int tmp = continus;
			if(continus < 10){
				numImg.setImageResource(Constants.imgsid[continus-2]);
				numImg.setVisibility(View.VISIBLE);
				hitImg.setVisibility(View.VISIBLE);
				numImg.startAnimation(numsAnimation);
				hitImg.startAnimation(numsAnimation);
			}else{
				num10img.setVisibility(View.VISIBLE);
				num10img.startAnimation(numsAnimation);
			}
			
		}else{
			continus = 1;
		}
		if(continus > maxContinus) maxContinus = continus;
		lastMatch = System.currentTimeMillis();
	}
	
	
	public static int delay = 600;
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
	private void resetMyLastTv(){
		int myLast = enBtnList.size();
		if(myLast < 10)
			tvMyLast.setText("0"+myLast); 
		else
			tvMyLast.setText(""+myLast);
		if(myLast == 0){
			DbUtil.saveLearned(passedList, NoNetActivity.staId);
			DbUtil.saveNoPassed(noPassedList,  NoNetActivity.staId);
			
			MyDialog dialog = new MyDialog(this, R.style.MyDialog, maxContinus);
			dialog.show();
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					resetGame();
				}

			});
		}
	}

	private void resetGame() {
		maxContinus = 0;
		// TODO Auto-generated method stub
		enListLayout.setVisibility(View.GONE);
		zhListLayout.setVisibility(View.GONE);
		startBtn.setVisibility(View.VISIBLE);
		tvMyLast.setText(wordsNum[NoNetActivity.staId]+"");
		findViewById(R.id.sibtn_tip).setClickable(false);
		
	}
	public void tipClick(View view){
		////Log.d("tong", lastBtn.toString() + " " + lastIndex);
		if(lastBtn == null || lastIndex == -1){
			Toast.makeText(this, "你还没有选择一个单词", Toast.LENGTH_LONG).show();
			return;
		}else{
			
			//选择的英语
			if(lastType == 1 ){
				noPassedList.add(lastBtn.getText().toString());
				
				int index = enBtnList.indexOf(lastBtn);
				deadList.add(lastBtn);
				deadList.add(zhBtnList.get(index));
				//开始消失动画
				lastBtn.startAnimation(disAnimationLong);
				zhBtnList.get(index).startAnimation(disAnimationLong);
				
			//	enListLayout.removeView(lastBtn);
			//	zhListLayout.removeView(zhBtnList.get(index));
				
				enBtnList.remove(lastBtn);
				zhBtnList.remove((int)index);
			}else{
				
				int index = zhBtnList.indexOf(lastBtn);
				
				noPassedList.add(enBtnList.get(index).getText().toString());
				
				deadList.add(lastBtn);
				deadList.add(enBtnList.get(index));
			//	enListLayout.removeView(enBtnList.get(index));
			//	zhListLayout.removeView(lastBtn);
				
				lastBtn.startAnimation(disAnimationLong);
				enBtnList.get(index).startAnimation(disAnimationLong);
				
				zhBtnList.remove(lastBtn);
				enBtnList.remove((int)index);
			}
			doMatch(1500);//延迟消失时间
			lastBtn = null;
			lastIndex = -1;
			lastType = 0;
			//当前道具不可用
			//((ImageButton)view).setImageResource(R.drawable.i_tip0);
			//view.setClickable(false);
			//proBntsClickable[0] = false;
		}
	}
	public void readyGame(View v){
		v.setVisibility(View.GONE);
		startBtn = (ImageView) v;
		zhListLayout.setVisibility(View.VISIBLE);
		enListLayout.setVisibility(View.VISIBLE);
		//getWords(enwords, zhwords);
		initWords(enwords,zhwords);
		findViewById(R.id.sibtn_tip).setClickable(true);
	}
}
