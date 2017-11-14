/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.InetAddress;

/**
 *
 * @author arpaf
 */
public class MessagePacket {
	public String msg;
	public long time;
	public InetAddress address;

	public MessagePacket(String msg, long time, InetAddress address) {
		this.msg = msg;
		this.time = time;
		this.address = address;
	}

}
