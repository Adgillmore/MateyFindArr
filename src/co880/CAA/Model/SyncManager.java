package co880.CAA.Model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author Adam
 * Based on the AccountManager code in RegisterUser by Dan.
 * Modified to find the Google calendar account and force 
 * a sync.
 */
public class SyncManager {
	
	public static final String TAG = "SyncManager";
	
	public SyncManager() {
	}

	/**
	 * Called whenever a sync of the Android calendar is 
	 * required with the online Google calendar
	 * @param inCtx
	 */
	public void forceSync(Context inCtx) {
	
	Account accountToSync = null;
	Account[] accounts = AccountManager.get(inCtx).getAccounts();
	for(Account account: accounts) {
		if ((account.type).equals("com.google")) {
			accountToSync = account;
		}
	}
	if (accountToSync != null) {
		ContentResolver.requestSync(accountToSync, "com.android.calendar", new Bundle());
		Log.i(TAG, "Started calendar sync");
	}
	}
}
