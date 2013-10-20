package co880.CAA.ServerUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author Adam
 * This class reads the inputStream from the server and
 * converts it to a string for parsing.
 * Taken from http://fahmirahman.wordpress.com
 * /2011/04/21/connection-between-php-server-and
 * -android-client-using-http-and-json/
 */
public class ReadJSONStream {
	private StringBuilder sb;
	
	public ReadJSONStream() {
		sb = new StringBuilder();
	}
	
	public String JsonToString(InputStream inputStream){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			sb.append(reader.readLine() + "\n");
			String line = "0";
			
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				}
		inputStream.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	return sb.toString();
	}
}
