package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009-11
 */

import java.net.InetAddress;
import java.net.InetSocketAddress;
import android.database.Cursor;
import android.util.Log;

public class CheckHost {
	String hostname, nickname, username, port;
	int timeOut;
	PortKnocker from;
	long hostID;

	public CheckHost (PortKnocker from, long hostID, String hostname,
			String timeout, String nickname, String username, String port) {
		this.from = from;
		this.hostID = hostID;
		this.hostname = hostname;
		this.nickname = nickname;
		this.username = username;
		this.port = port;

		// Check nickname length, set to "test" if empty
		if (nickname.length() == 0) {
			nickname = "test";
		}

		// Check timeout for actual number, if none, use default of 1000
		try {
			// Keep timeout reasonable
			timeOut = Integer.parseInt(timeout);
			if (timeOut > 60000 || timeOut < 100) {
				timeOut = 1000;
			}
		} catch (NumberFormatException ex) {
			timeOut = 1000;
		}
	}

	public int CheckAndKnock () {
		int port, success = 0;
		final String host = hostname;
		String newHost;
		boolean hostChanged=false;
		InetAddress address;
		DBAdapter dbadapter;
		Cursor ports;

		dbadapter = new DBAdapter(from.getApplicationContext());
		dbadapter.open();
		// Get all ports for this host
		// Log.v("CheckHost","hostID="+hostID);
		ports = dbadapter.getRawPorts(hostID);

		// Error Check!
		// Check if hostname is filled
		if (host.length() == 0) {
			return -1;
		}

		// Resolve the host string before doing knocking, could cause
		// a speed increase by not having to lookup the hostname
		// Now we allow people to have a different hostname for each port,
		// so this only helps when all ports have the same hostname
		try {
			address = new InetSocketAddress(host, 0).getAddress();
		} catch (SecurityException ex) {
			// catch the only error attempting to resolve a hostname can cause
			return -1;
		}

		try {
			if (ports != null) {
				ports.moveToFirst();
				if (ports.isFirst()) {
					do {
						port = Integer.parseInt(ports.getString(ports
								.getColumnIndex(DBAdapter.KEY_PORT)));
						
						// Check if this port has a different address/IP
						newHost=ports.getString(ports.getColumnIndex(DBAdapter.KEY_HOST));
						if (!newHost.equals(host) || hostChanged) {
							hostChanged=true;
							Log.v("CheckHost","Different hostname for this ("+port+") port...");
							try {
								address = new InetSocketAddress(newHost, 0).getAddress();
							} catch (SecurityException ex) {
								// catch the only error attempting to resolve a hostname can cause
								return -1;
							}
						} else {
							Log.v("CheckHost","Same hostname for this ("+port+") port.");
						}
						

						// Log.v("CheckHost","Knocking "+ports.getString(ports.getColumnIndex(DBAdapter.KEY_PACKETTYPE))+" port "+port);
						if (ports.getString(
								ports.getColumnIndex(DBAdapter.KEY_PACKETTYPE))
								.equals("TCP")
								&& Knock.doTCPKnock(address, port, timeOut)) {
							success++;
						} else if (ports.getString(
								ports.getColumnIndex(DBAdapter.KEY_PACKETTYPE))
								.equals("UDP")
								&& Knock.doUDPKnock(address, port, timeOut)) {
							success++;
							try {
								// Wait timeOut # of seconds between sending UDP
								// packets, since UDP packets don't use the
								// timeout settings for sending (only receiving)
								Thread.sleep(timeOut);
							} catch (InterruptedException ex) {
								// do nothing
							}
						} else {

							success = -1;
						}
					} while (ports.moveToNext());
				}
			}
		} catch (NumberFormatException ex) {
			// do nothing here
		}
		ports.close();
		dbadapter.close();
		return success;
	}
}
