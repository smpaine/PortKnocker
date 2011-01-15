package com.smpaine.portknocker;

/**
 * Port Knocker A port knocking application for android Based off of the
 * original PortKnocking application by Alexis Robert Under GPL 3 License
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Copyright Stephen Paine 2009,2010
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

public class helpWindow {
	static void showHelpWindow (final Activity activity, boolean ports) {
		final TextView message = new TextView(activity);
		final SpannableString s;
		
		if (ports) {
			s=new SpannableString(activity.getText(R.string.portsInstructions));
		} else {
			s=new SpannableString(activity.getText(R.string.hostInstructions));
		}
		Linkify.addLinks(s, Linkify.WEB_URLS);
		message.setText(s);
		message.setMovementMethod(LinkMovementMethod.getInstance());

		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		if (ports) {
			builder.setTitle(R.string.portsInstructionsTitle);
		} else {
			builder.setTitle(R.string.hostInstructionsTitle);
		}
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.ok, null);
		builder.setView(message);
		builder.create().show();
	}
}
