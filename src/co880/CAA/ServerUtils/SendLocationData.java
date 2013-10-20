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

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * 
 * @author Adam
 * This class sends location data to the server as a worker thread.
 * 
 *
 */
public class SendLocationData extends AsyncTask<String, Integer, Integer>{
	
	private HttpClient client; 
	private HttpPost request; 
	private Context ctx;
	private List<NameValuePair> postParameters;
		
	public SendLocationData(Context inCtx) {
		ctx = inCtx;
	}
			
	@Override
	/**
	 * @param A String array containing userID, latitude and longitude
	 * speed, timestamp and status.
	 */
	protected Integer doInBackground(String...strings) { //Takes either a single string or array of strings
		
		client = new DefaultHttpClient();
        request = new HttpPost("http://your-site-goes-here.com/addLocation.php");
        postParameters = new ArrayList<NameValuePair>();
        	try {
        	postParameters.add(new BasicNameValuePair("gmail", strings[0]));
    		postParameters.add(new BasicNameValuePair("latitude", strings[1]));	
    		postParameters.add(new BasicNameValuePair("longitude", strings[2]));
    		postParameters.add(new BasicNameValuePair("speed", strings[3]));
    		postParameters.add(new BasicNameValuePair("timestamp", strings[4]));
       		postParameters.add(new BasicNameValuePair("status", strings[5]));

        	UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
    		request.setEntity(formEntity);
    		client.execute(request);
        	}
    		catch (IOException e) {
        		e.printStackTrace();
        	}
        	
    return 1; 

	}

	protected void onPostExecute(InputStream inputStream) {
	}

	
	public List<NameValuePair> getPostParameters() {
		return postParameters;
	}


}