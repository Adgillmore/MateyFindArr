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

import co880.CAA.Activities.CAAActivity;
import co880.CAA.Model.EventModel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Adam
 * This class deletes a user's location data from the server using a worker thread.
 * 
 *
 */
public class DeleteLocationData extends AsyncTask<String, Integer, InputStream>{
	
	private HttpClient client; 
	private HttpPost request; 
	private Context ctx;
	private SharedPreferences pref;
	private String email;
	private String errorCode;
	private InputStream inputStream;
	private List<NameValuePair> postParameters;
	
	public DeleteLocationData(Context inCtx) {
		ctx = inCtx;
		pref = ctx.getSharedPreferences("caaPref", ctx.MODE_WORLD_READABLE);
		email = pref.getString("email", "Error in DeleteLocationData");
	}
			
	@Override
	/**
	 * @param A String array containing userID, latitude and longitude
	 */
	protected InputStream doInBackground(String...strings) { //Takes either a single string or array of strings
		
		client = new DefaultHttpClient();
        request = new HttpPost("http://your-site-goes-here.com/deleteUserSession.php");
       	postParameters = new ArrayList<NameValuePair>();
        	try {
        	postParameters.add(new BasicNameValuePair("gmail", strings[0]));
    		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
    		request.setEntity(formEntity);
    		HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        	}
    return inputStream; //Used to indicate completion of the task
	}

	@Override
	protected void onPostExecute(InputStream inputStream) {
		super.onPostExecute(inputStream);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean("noDataOnServer", true);
		editor.commit();
		Log.i("DeleteLocationData", "data deleted");

		// This method runs on the main UI thread
		String JSONString = new ReadJSONStream().JsonToString(inputStream);
		if (JSONString != null) {
			if (JSONString.length() > 8) {
				errorCode = JSONString.substring(0, 9);
				if (!errorCode.equals("[\"00000\"]")) {
					Toast.makeText(ctx, "Delete Location Data failed",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(ctx, "Delete Location Data failed",
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public List<NameValuePair> getPostParameters() {
		return postParameters;
	}
}
