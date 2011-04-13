package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009-11
 */

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class PortsList extends Activity {
	private DBAdapter dbadapter;
	private ListView portList;
	private TextView hostInfo;
	private boolean hintShown = false;
	private long hostID;
	private String hostName;

	private ListAdapter updateList () {
		Cursor portsCursor = dbadapter.getRawPorts(hostID);
		startManagingCursor(portsCursor);

		String[] from = new String[] { DBAdapter.KEY_PORT,
				DBAdapter.KEY_PACKETTYPE };
		int[] to = new int[] { R.id.TextView01, R.id.TextView02 };

		SimpleCursorAdapter ports = new SimpleCursorAdapter(this,
				R.layout.hostitem, portsCursor, from, to);

		if (ports.getCount() == 0 && !hintShown) {
			hintShown = true;
			helpWindow.showHelpWindow(this,true);
		}

		return ports;
	}

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portslist);

		dbadapter = new DBAdapter(this.getApplicationContext());
		dbadapter.open();

		portList = (ListView) findViewById(R.id.ListView01);
		hostInfo = (TextView) findViewById(R.id.TextView01);

		portList.setOnCreateContextMenuListener(this);
		portList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		portList.setClickable(true);

		Bundle extras = getIntent().getExtras();
		// extras should ALWAYS contain hostID and hostName
		hostID = extras.getLong(DBAdapter.KEY_HOST_ID);
		hostName = extras.getString(DBAdapter.KEY_HOST);

		hostInfo.setText(hostInfo.getText() + " " + hostName);
		portList.setAdapter(updateList());
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, Menu.FIRST, 0, getResources().getText(R.string.add_port));
		return true;
	}

	@Override
	public void onCreateContextMenu (ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Menu.FIRST + 1, 1, getResources().getText(R.string.edit_port));
		menu.add(0, Menu.FIRST + 2, 2, getResources().getText(R.string.delete_port));
	}

	@Override
	public boolean onMenuItemSelected (int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case Menu.FIRST:
				Intent i = new Intent(this, PortEdit.class);
				i.putExtra(DBAdapter.KEY_HOST_ID, hostID);
				i.putExtra(DBAdapter.KEY_HOST, hostName);
				startActivity(i);
				portList.setAdapter(updateList());
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
				Intent k = new Intent(this, PortEdit.class);
				k.putExtra(DBAdapter.KEY_ID, info.id);
				k.putExtra(DBAdapter.KEY_HOST_ID, hostID);
				k.putExtra(DBAdapter.KEY_HOST, hostName);
				startActivity(k);
				portList.setAdapter(updateList());
				return true;
			case Menu.FIRST + 2:
				dbadapter.deletePort(info.id);
				portList.setAdapter(updateList());
				return true;
		}
		return false;
	}

}
