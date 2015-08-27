package com.ncut.util;

import com.ncut.activity.R;
import com.nuct.model.Player;

public class Constants {
	public static Player player;
	public static int staId;
	
	//每个stadiumId 对应的房间中 单词个数
	public static int[] wordsNum = new int[]{12,18,24,12,18,24,12,18,24};
	
	//数字图片的ID
	public static int[] imgsid = new int[]{R.drawable.num2,R.drawable.num3,
		R.drawable.num4,R.drawable.num5,R.drawable.num6,R.drawable.num7,R.drawable.num8,
		R.drawable.num9};
	public static String[] tips = new String[]{
		"游戏提示:\n两次成功匹配单词的间隔5秒以内算连击",
		"游戏提示:\n游戏虽然重要，别忘了使用道具2给对方一个笑脸",
		"游戏提示:\n对方施展笑脸时快速摩擦笑脸可使其消除",
		"游戏提示:\n选择一个单词后，使用道具1可为你解决一个难词",
		"游戏提示:\n连击次数越多获得道具的几率越大",
		"游戏提示:\n使用道具3，可以给对方随机增加1-3个单词",
		"游戏提示:\n使用道具4会重新打乱对方的单词顺序",
		"游戏提示:\n每局新的游戏都有4个初始道具，别忘了及时使用哦",
		"游戏提示:\n限时抢分赛中不限制单词数量，多劳多得！",
		"游戏提示:\n单词多的亮瞎眼?不要急，消几个就简单了~"
	};
	public static String tablesName[] = new String[]{""};
	public static boolean isGameSound = true;
	
	//所有的进入房间 按钮id
		public static int ids[] = new int[]{
				R.id.sta_btn0,R.id.sta_btn1,R.id.sta_btn2,R.id.sta_btn3,R.id.sta_btn4,R.id.sta_btn5,R.id.sta_btn6,R.id.sta_btn7,R.id.sta_btn8,R.id.sta_btn9,R.id.sta_btn10,R.id.sta_btn11,R.id.sta_btn12,R.id.sta_btn13,R.id.sta_btn14,R.id.sta_btn15,R.id.sta_btn16,R.id.sta_btn17,R.id.sta_btn18,R.id.sta_btn19,R.id.sta_btn20,R.id.sta_btn21,R.id.sta_btn22,R.id.sta_btn23,R.id.sta_btn24,R.id.sta_btn25,R.id.sta_btn26,R.id.sta_btn27,R.id.sta_btn28,R.id.sta_btn29,
		};
				

		//单词分类的id
		public static int catIds[] = new int[]{
				R.id.catLL0,R.id.catLL1,R.id.catLL2,R.id.catLL3,R.id.catLL4,R.id.catLL5,R.id.catLL6,R.id.catLL7,R.id.catLL8,R.id.catLL9
		};
		
}
