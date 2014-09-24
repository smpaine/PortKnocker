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
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HostEdit extends Activity {
	private Long rowid;
	private DBAdapter dbadapter;
	private EditText hostName,timeout,nickname,username,label,port;
	private Button savebutton,CancelButton;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hostedit);
		this.setTitle(R.string.add_edit_host);

		dbadapter = new DBAdapter(this);
		dbadapter.open();

		label = (EditText) findViewById(R.id.EditText06);
		hostName = (EditText) findViewById(R.id.EditText01);
		timeout = (EditText) findViewById(R.id.EditText02);
		nickname = (EditText) findViewById(R.id.EditText03);
		username = (EditText) findViewById(R.id.EditText04);
		port = (EditText) findViewById(R.id.EditText05);
		savebutton = (Button) findViewById(R.id.Button01);
		CancelButton = (Button) findViewById(R.id.Button02);

		CancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick (View view) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		savebutton.setOnClickListener(new View.OnClickListener() {
			public void onClick (View view) {
				dbadapter.open();
				commit();
				dbadapter.close();
				setResult(RESULT_OK);
				viewPorts();
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(DBAdapter.KEY_ID)) {
				rowid = extras.getLong(DBAdapter.KEY_ID);
				populateFields();
			}
		}
		dbadapter.close();
	}

	@SuppressWarnings("deprecation")
	private void populateFields () {
		if (rowid != null) {
			Cursor host = dbadapter.getRawHost(rowid);
			startManagingCursor(host);

			label.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_LABEL)));
			hostName.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_HOST)));
			timeout.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_TIMEOUT)));
			nickname.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_NICKNAME)));
			username.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_USERNAME)));
			port.setText(host.getString(host
					.getColumnIndexOrThrow(DBAdapter.KEY_PORT)));
		}
	}

	private void commit () {
		String labelText = label.getText().toString();
		String host = hostName.getText().toString();
		String timeOut = timeout.getText().toString();
		String nick = nickname.getText().toString();
		String user = username.getText().toString();
		String portNum = port.getText().toString();

		// Cleanup timeout and portNum (just in case)
		timeOut = timeOut.replaceAll("[^0-9]", "");
		portNum = portNum.replaceAll("[^0-9]", "");

		if (rowid == null) {
			rowid=dbadapter.createHost(labelText, host, timeOut, nick, user, portNum);
		} else {
			dbadapter.updateHost(rowid, labelText, host, timeOut, nick, user, portNum);
		}
	}
	
	private void viewPorts() {
		Intent k = new Intent(this, PortsList.class);
		k.putExtra(DBAdapter.KEY_HOST_ID, rowid);
		k.putExtra(DBAdapter.KEY_HOST, hostName.getText().toString());
		startActivity(k);
	}
}
