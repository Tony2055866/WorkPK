package com.ncut.net;

/**
 * 
 * @author GaoTong
 * 处理从服务器发送过来的消息
 */
public interface MsgListener {
	public void porcessMsg(String msg);
}
