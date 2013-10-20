package co880.CAA.ServerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

/**
 * 
 * @author Adam
 *This class creates the HTTP client to send the event details to the server for 
 *distribution to other users via the GCM sevice.
 */
public class SendEventDetails {
	
	//Fields
		private HttpClient client;
		private HttpPost request;
		private InputStream inputStream;
		String result;
		
		public SendEventDetails() {
			
		}
		
		public InputStream shareEvent(JSONObject event, ArrayList<String> users) {
		client = new DefaultHttpClient();
		request = new HttpPost("http://your-site-goes-here.com/GCMServer.php");
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair(("JSONString"), event.toString()));
		Iterator<String> iterator = users.iterator();
		int i=0;
		while (iterator.hasNext()) {
			postParameters
			.add(new BasicNameValuePair(("user" + i), iterator.next()));
			i++;
		}
		
		try {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(
					postParameters);
			request.setEntity(formEntity);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = new ReadJSONStream().JsonToString(inputStream);
		return inputStream;
		}
		
		public String getResult() {
			return result;
		}

}
