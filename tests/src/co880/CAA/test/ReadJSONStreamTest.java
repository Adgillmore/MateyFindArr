package co880.CAA.test;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.test.AndroidTestCase;

import co880.CAA.ServerUtils.ReadJSONStream;

public class ReadJSONStreamTest extends AndroidTestCase {
	private ReadJSONStream myReader;
	private InputStream testStream;
	private String expectedResults;
	private String actualResults;
	HttpClient client; 
	HttpGet request; 
	
	public ReadJSONStreamTest() {
		super();
		myReader = new ReadJSONStream();
		expectedResults = "[{\"gmail\":\"adgillmore@gmail.com\"}]\n" +
				"<!-- Hosting24 Analytics Code -->\n" +
				"<script type=\"text/javascript\" src=\"http://stats.hosting24.com/count.php\"></script>\n" +
				"<!-- End Of Analytics Code -->\n";
		client = new DefaultHttpClient();
        request = new HttpGet("http://your-site-goes-here.com/testInputStream.php");
    }

	protected void setUp() throws Exception {
		super.setUp();
		try {
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		testStream = entity.getContent();
		}
    	catch (IOException e) {
    		e.printStackTrace();
    	}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testJsonToString() {
		actualResults = myReader.JsonToString(testStream);
		assertEquals(expectedResults, actualResults);
	}

}
