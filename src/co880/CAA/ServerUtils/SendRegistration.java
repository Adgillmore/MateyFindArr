package co880.CAA.ServerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

//import com.google.gson.Gson;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Adam 
 * This class sends a user's gmail address and GCM registration ID to the 
 * server to add a user to the database of registered users.
 * 
 */
public class SendRegistration extends AsyncTask<String, Integer, InputStream> {

	private HttpClient client;
	private HttpPost request;
	private Context ctx;
	private InputStream inputStream;
	private String sentGmail;
	private String errorCode;

	public SendRegistration(Context inCtx) {
		ctx = inCtx;
	}

	@Override
	/**
	 * @param A String array containing gmail and GCM registration ID
	 */
	protected InputStream doInBackground(String... strings) { // Takes either a
																// single string
																// or array of
																// strings

		client = new DefaultHttpClient();
		request = new HttpPost("http://your-site-goes-here.com/addUser.php");
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		try {
			postParameters.add(new BasicNameValuePair("gmail", strings[0]));
			postParameters.add(new BasicNameValuePair("regID", strings[1]));
			sentGmail = strings[0];
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters);
			request.setEntity(formEntity);

			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return inputStream; // Used by callback methods
	}

	protected void onPostExecute(InputStream inputStream) {
		// This method runs on the main UI thread
		String JSONString = new ReadJSONStream().JsonToString(inputStream);
		if (JSONString != null) {
			if (JSONString.length() > 8) {
				errorCode = JSONString.substring(0, 9);
				if (errorCode.equals("[\"00000\"]")) {
					Toast.makeText(ctx, "Registered " + sentGmail,
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(ctx, "Registration failed",
							Toast.LENGTH_LONG).show();
				}
			}
		}

	}

	// Could store returnedGmail somewhere if useful for Location Activity or
	// CAAActivity

	public String getErrorCode() {
		return errorCode;
	}

}
