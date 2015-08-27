package com.ncut.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.nuct.model.Player;

/**
 * 
 * @author GaoTong
 * 负责接收服务器端的数据
 */
public class ClientThread extends Thread{

	public static ClientThread cthread;
	
	//public static MsgListener precess; //某一时刻只有一个
	public static Handler handler; //某一时刻只有一个
	
	public static boolean isConn = false;
	public static Socket socket;
	private static BufferedReader in = null;  
    //private static PrintWriter out = null; 
    private static BufferedWriter buffout;
    String line;
//	public  String serverIp="10.51.41.116";
   // public  String serverIp="192.168.23.3"; //wordpk.acmerblog.com
    public  String serverIp="wordpk.acmerblog.com";
	//public  String serverIp="192.168.1.100";
	public ClientThread() throws UnknownHostException, IOException{
		try{
			if(socket!=null && socket.isConnected()){
				socket.close();
			}
		}catch(Exception e){
			
		}
		socket = new Socket();   
		InetSocketAddress isa = new InetSocketAddress(serverIp, 9985);     
		socket.connect(isa);
		//socket.setSoTimeout(10000);
		//socket.setKeepAlive(true);
		//socket = new Socket(serverIp, 9985);
		//socket.setSoTimeout(2000);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		buffout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
		//out = new PrintWriter(socket.getOutputStream(),true);
		isConn = true;
		cthread = this;
	}
	
	public ClientThread(Player p) throws UnknownHostException, IOException{
		try{
			if(socket!=null && socket.isConnected()){
				socket.close();
			}
		}catch(Exception e){
		}
		socket = new Socket();   
		InetSocketAddress isa = new InetSocketAddress(serverIp, 9985);     
		socket.connect(isa);
		//socket.setSoTimeout(10000);
		//socket.setKeepAlive(true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//out = new PrintWriter(socket.getOutputStream(),true);
		buffout = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"GBK"));
		String msg = "reconn" + "$" + p.getStr() + "\n";
		
		
		buffout.write("reconn" + "$" + p.getStr() + "\n");
		//out.println("reconn" + "$" + p.getStr()); //发送重新连接的信息
		//out.flush();
		buffout.flush();
		isConn = true;
		cthread = this;
	}
	
	@Override
	public void run() {
		////Log.d("tong", "thread running");
		//Looper.prepare();
		try {
			while( (line=in.readLine()) != null){
			//while(true){
				if(!line.equals(""))
				{
					Message msg = new Message();
					msg.obj = line;
					handler.sendMessage(msg);
					//precess.porcessMsg(line);
				}
				////Log.d("tong", "client getMsg:" + line);
				//processMsg(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			isConn = false;
			
			Message msg = new Message();
			msg.obj = "error";
			cthread = null;
			if(handler!=null)
				handler.sendMessage(msg);
			////Log.d("tong", "socket 断开！");
			//precess.porcessMsg("error");
		}finally{
			try {
				socket.close();
				////Log.d("tong", "socket closed!");
				
			} catch (IOException e) {
				////Log.d("tong","socket closed! failed");
			}
		}
		//Looper.loop();
		cthread = null;

	}
	

	public static void sendMsg(String msg){
		//当前对象为空，说明连接已经断开
		if(cthread == null){
			////Log.d("tong", "cthread == null, 提醒用户重新连接");
			Message m = new Message();
			m.obj = "error";
			handler.sendMessage(m);
			return;
		}
		try {
			buffout.write(msg + "\n");
			buffout.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//	
	
}
