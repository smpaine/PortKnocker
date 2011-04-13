package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009-11
 */

import com.smpaine.portknocker.R;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PortKnocker extends Activity {
	private int success;
	private DBAdapter dbadapter;
	private Handler mHandler = new Handler();
	private Button exitButton;
	private ListView hostList;
	private CheckHost host;
	private boolean hintShown = false;
	private PortKnocker me;
	private ConnectivityManager connectionInfo;
	private NetworkInfo wifiInfo, mobileInfo;
	private State netState;

	private void launchKnocking () {
		new Thread(new Runnable() {
			public void run () {
				if (host == null) {
					success = -1;
				} else {
					success = host.CheckAndKnock();
				}
				mHandler.post(new Runnable() {
					public void run () {
						endKnocking();
					}
				});
			}
		}).start();
	}

	private void endKnocking () {
		if (success < 0) {
			Toast.makeText(PortKnocker.this,
					getResources().getText(R.string.knock_failure),
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(PortKnocker.this,
					getResources().getText(R.string.knock_success),
					Toast.LENGTH_SHORT).show();
			if (host.username.length() > 0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse("ssh://" + host.username + "@"
						+ host.hostname + ":" + host.port + "/#"
						+ host.nickname));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(Intent.createChooser(i, "Choose SSH Program"));
				} catch (ActivityNotFoundException ex) {
					Toast.makeText(
							PortKnocker.this,
							getResources().getText(
									R.string.connectbot_not_found),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private boolean checkInternet () {
		try {
			connectionInfo = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			wifiInfo = connectionInfo
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			mobileInfo = connectionInfo
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			netState = wifiInfo.getState();
			if (netState.equals(State.CONNECTED)
					|| netState.equals(State.CONNECTING)) {
				return true;
			}
			netState = mobileInfo.getState();
			if (netState.equals(State.CONNECTED)
					|| netState.equals(State.CONNECTING)) {
				return true;
			}
		} catch (SecurityException ex) {
			// Can't notify from here
		}
		return false;
	}

	private ListAdapter updateList () {

		Cursor hostsCursor = dbadapter.getRawHosts();
		startManagingCursor(hostsCursor);

		String[] from = new String[] { DBAdapter.KEY_LABEL };
		int[] to = new int[] { R.id.TextView01 };

		SimpleCursorAdapter hosts = new SimpleCursorAdapter(this,
				R.layout.hostitem_1, hostsCursor, from, to);

		if (hosts.getCount() == 0 && !hintShown) {
			hintShown = true;
			helpWindow.showHelpWindow(me,false);
		}
		return hosts;
	}

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		me = this;

		dbadapter = new DBAdapter(this.getApplicationContext());
		dbadapter.open();

		exitButton = (Button) findViewById(R.id.Button01);
		hostList = (ListView) findViewById(R.id.ListView01);

		hostList.setOnCreateContextMenuListener(this);
		hostList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		hostList.setClickable(true);

		hostList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick (AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg3 != AdapterView.INVALID_ROW_ID && arg3 > 0) {
					if (!checkInternet()) {
						Toast.makeText(PortKnocker.this,
								getResources().getText(R.string.no_internet),
								Toast.LENGTH_LONG).show();
					} else {
						host = new CheckHost(me, arg3, dbadapter.getHost(arg3),
								dbadapter.getTimeout(arg3), dbadapter
										.getNickname(arg3), dbadapter
										.getUsername(arg3), dbadapter
										.getPort(arg3));
						Toast
								.makeText(
										PortKnocker.this,
										getResources().getText(
												R.string.start_knocking),
										Toast.LENGTH_SHORT).show();
						launchKnocking();
					}
				} else if (hostList.getAdapter().isEmpty()) {
					helpWindow.showHelpWindow(me,false);
				}
			}
		});

		exitButton.setOnClickListener(new View.OnClickListener() {
			public void onClick (View view) {
				finish();
			}
		});

		hostList.setAdapter(updateList());
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, getResources().getText(R.string.add_host));
		return true;
	}

	@Override
	public void onCreateContextMenu (ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST + 1, 0, getResources().getText(
				R.string.send_knock));
		menu.add(0, Menu.FIRST + 2, 1, getResources().getText(
				R.string.edit_host));
		menu.add(0, Menu.FIRST + 3, 1, getResources().getText(
				R.string.edit_ports));
		menu.add(0, Menu.FIRST + 4, 2, getResources().getText(
				R.string.delete_host));
	}

	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case Menu.FIRST:
				Intent i = new Intent(this, HostEdit.class);
				startActivity(i);
				hostList.setAdapter(updateList());
				return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onContextItemSelected (MenuItem item) {
		super.onContextItemSelected(item);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
			case Menu.FIRST + 1:
				if (info.id != AdapterView.INVALID_ROW_ID && info.id >= 0) {
					if (!checkInternet()) {
						Toast.makeText(PortKnocker.this,
								getResources().getText(R.string.no_internet),
								Toast.LENGTH_LONG).show();
					} else {
						host = new CheckHost(me, info.id, dbadapter
								.getHost(info.id), dbadapter
								.getTimeout(info.id), dbadapter
								.getNickname(info.id), dbadapter
								.getUsername(info.id), dbadapter
								.getPort(info.id));
						Toast
								.makeText(
										PortKnocker.this,
										getResources().getText(
												R.string.start_knocking),
										Toast.LENGTH_SHORT).show();
						launchKnocking();
					}
				} else if (hostList.getAdapter().isEmpty()) {
					helpWindow.showHelpWindow(me,false);
				}
				return true;
			case Menu.FIRST + 2:
				Intent j = new Intent(this, HostEdit.class);
				j.putExtra(DBAdapter.KEY_ID, info.id);
				startActivity(j);
				hostList.setAdapter(updateList());
				return true;
			case Menu.FIRST + 3:
				Intent k = new Intent(this, PortsList.class);
				k.putExtra(DBAdapter.KEY_HOST_ID, info.id);
				k.putExtra(DBAdapter.KEY_HOST, dbadapter.getHost(info.id));
				startActivity(k);
				return true;
			case Menu.FIRST + 4:
				dbadapter.deleteHost(info.id);
				hostList.setAdapter(updateList());
				return true;
		}
		return false;
	}
}