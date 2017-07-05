import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class mytest {

	
	public mytest() {
		
		
	}

	@Test
	public void myytest()
	{
		OutputStream os;
		int uid = 50;
		String contact = "bbba";
		URL url;
		try {
			url = new URL("http://115.159.27.70:8084/rest/feed/addFriend");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			String sb = "myId="+uid+"&friendId="+contact+";&token=123";
			os = connection.getOutputStream();
			os.write(sb.toString().getBytes("utf-8"));
			connection.getResponseCode();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
