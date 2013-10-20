package co880.CAA.ServerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

/**
 * 
 * @author Adam This class sends a list of users to the server and retrieves
 *         their locations and time stamps as a worker thread.
 * 
 */
public class GetOtherLocations {
	
	//Fields
	private HttpClient client;
	private HttpPost request;
	private InputStream inputStream;

	//Constructor
	public GetOtherLocations() {
	}

	//Methods
	/**
	 * @param A String array containing gmails
	 */
	public InputStream sendRequest(Set<String> inUsers) {
		client = new DefaultHttpClient();
		request = new HttpPost("http://your-site-goes-here.com/getUsersLocations.php");
		List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		Iterator<String> iterator = inUsers.iterator();
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

		return inputStream;
	}
	
}
