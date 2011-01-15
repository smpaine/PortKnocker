package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009,2010
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PortEdit extends Activity {
	private Long rowid, hostID;
	private DBAdapter dbadapter;
	private EditText port;
	private Spinner packetType;
	private Button savebutton;
	private Button CancelButton;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portedit);
		this.setTitle("Add/Edit Port");

		dbadapter = new DBAdapter(this);
		dbadapter.open();

		port = (EditText) findViewById(R.id.EditText01);
		packetType = (Spinner) findViewById(R.id.Spinner01);
		savebutton = (Button) findViewById(R.id.Button01);
		CancelButton = (Button) findViewById(R.id.Button02);

		ArrayAdapter<CharSequence> packetTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.packet_type,
						android.R.layout.simple_spinner_item);
		packetTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		packetType.setAdapter(packetTypeAdapter);

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
				finish();
			}
		});

		Bundle extras = getIntent().getExtras();
		// extras should ALWAYS contain hostID
		hostID = extras.getLong(DBAdapter.KEY_HOST_ID);
		if (extras.containsKey(DBAdapter.KEY_ID)) {
			rowid = extras.getLong(DBAdapter.KEY_ID);
			populateFields();
		}
		dbadapter.close();
	}

	private void populateFields () {
		if (rowid != null) {
			int ptype = 0;
			Cursor portInfo = dbadapter.getRawPort(rowid);
			startManagingCursor(portInfo);

			port.setText(portInfo.getString(portInfo
					.getColumnIndexOrThrow(DBAdapter.KEY_PORT)));

			if (portInfo.getString(
					portInfo.getColumnIndexOrThrow(DBAdapter.KEY_PACKETTYPE))
					.equals("UDP")) {
				ptype = 1;
			}
			packetType.setSelection(ptype);
		}
	}

	private void commit () {
		String portNum = port.getText().toString();
		String packet_type = (String) packetType.getSelectedItem();

		// Cleanup ports list and timeout (remove incorrect characters);
		portNum = portNum.replaceAll("[^0-9]", "");

		if (rowid == null) {
			dbadapter.addPort(hostID, portNum, packet_type);
		} else {
			dbadapter.updatePort(rowid, hostID, portNum, packet_type);
		}
	}
}
