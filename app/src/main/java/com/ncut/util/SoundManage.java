package com.ncut.util;

import java.util.HashMap;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;

import com.ncut.activity.R;


public class SoundManage {
	public  HashMap<String,Integer> map;  
    public  SoundPool soundPool;
   // static Activity context;
    public SoundManage(Activity context){
    	//this.context = context;
    	map = new HashMap<String, Integer>();
    	soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);  
    	map.put("back", soundPool.load(context,R.raw.back2new , 1));  
        map.put("daoju", soundPool.load(context, R.raw.item1, 1));  
        map.put("lose", soundPool.load(context, R.raw.lose, 1));  
    }
    public void play(String sound){
    	soundPool.play(map.get(sound), 1, 1, 1, 0, 1);
    }
    
   
}
