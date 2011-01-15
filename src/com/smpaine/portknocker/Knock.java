package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009,2010
 */

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Knock {
	public static boolean doTCPKnock (InetAddress host, int port, int timeout) {
		Socket s;
		try {
			// s = new Socket(host, port);
			s = new Socket();
			InetSocketAddress socket = new InetSocketAddress(host, port);
			// Use user-provided timeout
			s.connect(socket, timeout);
			if (s.isConnected())
				s.close();
		} catch (UnknownHostException ex) {
			return false;
		} catch (IllegalArgumentException ex) {
			return false;
		} catch (ConnectException ex) {
			// We dismiss "connection refused" as knockd operates at link-layer
		} catch (SocketTimeoutException ex) {
			// Ignore timeout -- went through all the above junk to send the
			// packets within 15 seconds
		} catch (IOException ex) {
			return false;
		}

		return true;
	}

	public static boolean doUDPKnock (InetAddress host, int port, int timeout) {
		DatagramSocket s;
		// do everything in threes (why not? -- won't send any of it anyways...)
		byte[] empty = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		try {
			s = new DatagramSocket();
			s.setSoTimeout(timeout);
			// s.connect(host, port);
			s.send(new DatagramPacket(empty, 0, empty.length, host, port));
		} catch (IllegalArgumentException ex) {
			// should be here, since this means the packet was blocked the the
			// host firewall
		} catch (SocketException ex) {
			return false;
		} catch (IOException ex) {
			return false;
		}
		return true;
	}
}
