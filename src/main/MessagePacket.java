/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.net.InetAddress;

/**
 * Wrapper around messages toc keep all the important datas
 * @author arpaf
 */
public class MessagePacket {
	public String msg;
	public long time;
	/**
	 * sender InetAdress
	 * SHOULD be in IPV6
	 */
	public InetAddress address;

	public MessagePacket(String msg, long time, InetAddress address) {
		this.msg = msg;
		this.time = time;
		this.address = address;
	}

}
