package co880.CAA.ServerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.os.AsyncTask;

/**
 * 
 * @author Adam
 * This class sends a request to the server to update a user's location sharing status.
 *
 */
public class SetSharingStatus extends AsyncTask<String, Integer, Integer>{
	
	HttpClient client; 
	HttpPost request; 
	Context ctx;
		
	public SetSharingStatus(Context inCtx) {
		ctx = inCtx;
	}
			
	@Override
	/**
	 * @param A String array containing gmail address and status
	 */
	protected Integer doInBackground(String...strings) { //Takes either a single String or array of Strings
		
		client = new DefaultHttpClient();
        request = new HttpPost("http://your-site-goes-here.com/updateStatus.php");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        	try {
        	postParameters.add(new BasicNameValuePair("gmail", strings[0]));
        	postParameters.add(new BasicNameValuePair("status", strings[1]));

        	UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
    		request.setEntity(formEntity);
    		client.execute(request);
    		
        	}
        	catch (IOException e) {
        		e.printStackTrace();
        	}
        	
    return 1; //Used to indicate completion of the task
	}
	
	

}