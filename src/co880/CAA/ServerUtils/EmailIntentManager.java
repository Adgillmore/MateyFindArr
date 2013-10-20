/**
 * 
 */
package co880.CAA.ServerUtils;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *@author Adam 
 *Additional contributor: Ka lan
 * Main references from android dev guide http://developer.android.com/reference/android/content/Intent.html and StackOverflow.
 */
public final class EmailIntentManager {

	private static String subject = "Ahoy Matey!";
	private static String message = "";


public static void launchEmailIntent(Context context, String emailAddress) {
	//@ Ka lan: One change to filter to apps can send emails.
	Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
	emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
	emailIntent.setData(Uri.parse("mailto:" + emailAddress));
	emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
	emailIntent.putExtra(Intent.EXTRA_TEXT, message);
	context.startActivity(emailIntent);
}
}