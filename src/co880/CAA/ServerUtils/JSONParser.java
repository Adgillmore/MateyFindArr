package co880.CAA.ServerUtils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Paint.Join;
import android.net.ParseException;

/**
 * 
 * @author Adam Based on code from http://fahmirahman.wordpress.com
 *         /2011/04/21/connection
 *         -between-php-server-and-android-client-using-http-and-json/
 */

public class JSONParser {

	// fields
	private String[] parsedString;
	private String[] value;
	private String[] parsedContactString;
	private String[] contactsValue;
	private String[][] twoDValue;

	// constructor
	public JSONParser() {
	}

	// methods
	/**
	 * @author Adam extract name value pairs from string
	 * @param inputStream
	 * @return
	 */
	public String[] parse(String inString, String[] keys, boolean forContacts) {
		try {
			JSONObject jObject = null;
			JSONObject json_data = null;
			JSONArray jArray = null;
			value = new String[keys.length];
			if (keys[0].equals("failure")) {
				jObject = new JSONObject(inString);
				value[0] = Integer.toString(jObject.getInt(keys[0]));
			} else {
				jArray = new JSONArray(inString);
				contactsValue = new String[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);
					if (forContacts) {
						contactsValue[i] = json_data.getString(keys[0]);
					} else {
						value[i] = json_data.getString(keys[i]);
					}
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (!forContacts && value != null && value.length > 0) {
			return value;
		} else if (forContacts && contactsValue != null
				&& contactsValue.length > 0) {
			return contactsValue;
		} else {
			String[] errorMessage = { "no value found" };
			return errorMessage;
		}
	}

	/**
	 * @author Adam Method to convert String representing JSON object in 2D
	 *         array of usernames and location data
	 * @param String
	 *            the JSON String, String[] keys for names of name value pairs
	 *            This allows the parser to be used for any JSON object for
	 *            which the name value pairs are known
	 * @return a 2D String array containing location (and other) data for each
	 *         email address supplied to the server.
	 */

	public String[][] twoDParse(String inString, String[] keys) {
		if (inString != null) {
			try {
				JSONArray jOuterArray = new JSONArray(inString);
				JSONObject json_data = null;
				twoDValue = new String[jOuterArray.length()][keys.length];
				for (int i = 0; i < jOuterArray.length(); i++) {
					json_data = jOuterArray.getJSONObject(i);
					for (int j = 0; j < keys.length; j++) {
						twoDValue[i][j] = json_data.getString(keys[j]);
					}

				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			if (twoDValue != null && twoDValue.length > 0) {
				return twoDValue;
			} else {
				return null; // or an error message
			}
		} else {
			return null;
		}
	}

	/**
	 * Extract event details e.g. Title, Start/End times, from String
	 * 
	 * @param inString
	 * @param keys
	 * @return
	 */
	public ArrayList<String> parseEvent(String inString, String requiredObject,
			String[] keys) {
		String strippedString = inString.replaceAll("\\\\", "");
		ArrayList<String> event = new ArrayList<String>();
		try {
			JSONObject wholeObject = new JSONObject(strippedString);
			JSONObject extractedObject = wholeObject
					.getJSONObject(requiredObject);

			for (int i = 0; i < keys.length; i++) {

				event.add(extractedObject.getString(keys[i]));

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return event;
	}

	/**
	 * Extract attendees from String of attendees
	 * 
	 * @param inString
	 * @return
	 */
	public ArrayList<String> parseAttendees(String inString) {
		String strippedString = inString.replaceAll("\\\\", "");
		ArrayList<String> attendees = new ArrayList<String>();
		try {
			JSONObject wholeObject = new JSONObject(strippedString);
			JSONObject attendeeObject = wholeObject
					.getJSONObject("attendeeObject");

			for (int i = 0; i < attendeeObject.length(); i++) {
				attendees.add(attendeeObject.getString("attendee" + i));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attendees;
	}

	// Accessor
	public String[] getParsedContactsString() {
		return parsedContactString;
	}

	public String[] getParsedString() {
		return parsedString;
	}

	public String[][] getTwoDValue() {
		return twoDValue;
	}

	public String getFirstTwoDValue() {
		return twoDValue[0][0];
	}

	// Mutator
	public void setParsedContactsString(String[] parsedString) {
		this.parsedContactString = parsedString;
	}

	public void setParsedString(String[] parsedString) {
		this.parsedString = parsedString;
	}

	public void setTwoDValue(String[][] stringArray) {
		twoDValue = stringArray;
	}
}
