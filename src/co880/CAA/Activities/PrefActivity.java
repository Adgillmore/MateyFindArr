package co880.CAA.Activities;

import co880.CAA.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
 
/**
 * 
 * @author Dan - simple Activity to handle to changing of preference from our
 * 'Settings' screen
 *
 */
public class PrefActivity extends PreferenceActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preference);
                
                ListPreference dataPref = (ListPreference) findPreference("listPref");
                dataPref.setSummary(dataPref.getEntry());
                dataPref.setEntryValues(getResources().getStringArray(R.array.frequencyMilliseconds));
                dataPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					
					public boolean onPreferenceChange(Preference preference, Object newValue) {
					preference.setSummary(newValue.toString());
						return true;
					}
				});
        }
}