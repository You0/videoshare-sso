import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.junit.Test;

import junit.framework.TestCase;

public class CoTest {

    public  String BOUNDARY = UUID.randomUUID().toString(); //边界标识
    public  String PREFIX = "--", LINE_END = "\r\n", CONTENT_TYPE = "multipart/form-data"; // 内容类型;
    private  final String TAG = "uploadFile";
    private  final int TIME_OUT = 10 * 10000000; // 超时时间
    private  final String CHARSET = "utf-8"; // 设置编码
    private int filesSize;
    private int CurrentSize;
	
	@Test
	public void TestCase()
	{
		
		 HashMap<String,File> files = new HashMap<>();
         for(int i=0;i<1;i++){
        	 files.put("xxx",new File("D:\\123.ass"));
         }
         HashMap<String,String> text = new HashMap<>();
         text.put("username","xx");
         text.put("Token","xx");
		
		
		
		
		
		try {
            URL url1 = new URL("http://127.0.0.1:8080/videoshare-sso/rest/info/backgound");
            Socket socket = new Socket(url1.getHost(), url1.getPort());
            OutputStream os = socket.getOutputStream();
            PrintStream ps = new PrintStream(os, true, "UTF-8");

            ps.println("POST /videoshare-sso/rest/info/backgound HTTP/1.1");
            ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
            ps.println("Content-Length: " + "1024");  

            for (File file : files.values()) {
                filesSize += file.length();
            }


            for (Entry<String, String> entry : text.entrySet()) {
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);sb.append(BOUNDARY);sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name="+"\""+entry.getKey()+"\""+ LINE_END);
                sb.append(LINE_END);
                sb.append(entry.getValue()+LINE_END);
                os.write(sb.toString().getBytes());
            }




            for (Entry<String, File> entry : files.entrySet()) {
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition:form-data; name=\"img\"; filename=\""
                        + entry.getValue().getName()+".jpg"
                        + "\"" + LINE_END);
                sb.append("Content-Type:application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                os.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(entry.getValue());
                byte[] bytes = new byte[512];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    os.write(bytes, 0, len);
                    CurrentSize += len;
                }
                os.write(LINE_END.getBytes("UTF-8"));
                is.close();
            }
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                    .getBytes();
            os.write(end_data);
            os.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */



                InputStream is = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(reader);
                br.close();
                reader.close();
                is.close();
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	
}
