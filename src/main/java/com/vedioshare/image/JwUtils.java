package com.vedioshare.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.BoldAction;

public class JwUtils {
    private String GBKUrl = null;

    private String NJ = null;

    private String BaseUrl = null;

    private String Username = null;

    private String Password = null;

    private String Site = null;

    public String getSite() {
        return Site;
    }

    public void setSite(String site) {
        Site = site;
    }


    public String Name = null;

    private String Encrypt = null;

    private String CrossUrl = null;

    private String SpecialUrl = null;

    private String SportUrl = null;

    private ArrayList<String> SubjectContent = new ArrayList<String>();

    private ArrayList<String> CrossSubjectContent = new ArrayList<String>();

    private ImagePreProcess imagePreProcess = new ImagePreProcess();

    private String ImageUrl = null;

    private String Cookies = null;

    public ArrayList<String> PostData = new ArrayList<String>();

    public JwUtils(String Site, String Username, String Password) {
        this.Username = Username;
        this.Password = Password;
        this.Site = Site;
        BaseUrl = "http://xk" + Site + ".ahu.cn/";
        ImageUrl = "http://xk" + Site + ".ahu.cn/CheckCode.aspx";
        CrossUrl = BaseUrl + "zylb.aspx?xh=" + Username + "&nj=" + "20" + Username.substring(2, 4);

    }
    
    public void  initUrl() {
        BaseUrl = "http://xk" + Site + ".ahu.cn/";
        ImageUrl = "http://xk" + Site + ".ahu.cn/CheckCode.aspx";
        CrossUrl = BaseUrl + "zylb.aspx?xh=" + Username + "&nj=" + "20" + Username.substring(2, 4);
    }

    public JwUtils() {
    }
    
   
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String chinaToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {// ���ַ�Χ \u4e00-\u9fa5 (����)
                result += "%u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

	public String getName()
	{
		String result;
		String temp;
		int length;
		String tempUrl = BaseUrl+"xs_main.aspx?xh="+Username;
		HttpConnectionUtils util = new HttpConnectionUtils(tempUrl);
		HttpURLConnection connection =  util.GetConnection("GET","",this.Cookies);
		result = Read(connection);
		util.release();
		String regex = "<span id=\"xhxm\">.*?同学";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(result);
		if (!matcher.find()) {
			return "";
		}
		result = matcher.group(0);
		length = result.length();
		result = result.substring(16,length-2);
		System.out.println(result);
		temp = result;
		result = chinaToUnicode(result);

		GBKUrl = BaseUrl + "xsxk.aspx?xh="+Username+"&xm="+result+"&gnmkdm=N121101";
		SportUrl = BaseUrl+"xstyk.aspx?xh="+Username+"&xm="+result+"&gnmkdm=N121102";

		return temp;
	}



    public String Read(HttpURLConnection Connection) {
        BufferedReader is = null;
        StringBuffer result = new StringBuffer();
        String line = null;
        try {
            is = new BufferedReader(new InputStreamReader(Connection.getInputStream()));
            line = is.readLine();

            while (line != null) {
                result.append(line + "\n");
                line = is.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
            
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
       
        return result.toString();
    }

    public String GETEVENTVALIDATION(String Html) throws UnsupportedEncodingException {
        int end = 0;
        String regEx = "id=\"__EVENTVALIDATION\" value=.*\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(Html);
        if (!matcher.find()) {
            return null;
        }
        end = matcher.group(0).length();

        return URLEncoder.encode(matcher.group(0).substring(30, end - 1), "utf-8");
    }

    public String GETVIEWSTATE(String Html) throws UnsupportedEncodingException {
        int end = 0;
        String regEx = "id=\"__VIEWSTATE\" value=.*\"";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(Html);
        if (!matcher.find()) {
            return null;
        }
        end = matcher.group(0).length();

        return URLEncoder.encode(matcher.group(0).substring(24, end - 1), "utf-8");
    }

    public String OrcImage_Login() {
        String __VIEWSTATE = null;
        String __EVENTVALIDATION = null;
        String result = null;
        String Cookies = null;
        Scanner in = null;
        HttpURLConnection httpURLConnection = null;
        Cookies = imagePreProcess.downloadImage(ImageUrl);
        try {
            HttpConnectionUtils httpConnectionUtils = new HttpConnectionUtils(BaseUrl + "default2.aspx");
            String code = imagePreProcess.getAllOcr(ImagePreProcess.srcPath + '0' + ".png");
            ArrayList<String> arrayList = new ArrayList<String>();
            httpURLConnection = httpConnectionUtils.GetConnection("GET", "", "");
            result = Read(httpURLConnection);
            __VIEWSTATE = GETVIEWSTATE(result);
            __EVENTVALIDATION = GETEVENTVALIDATION(result);
            System.out.println("yzm:" + code);
            arrayList.add("__VIEWSTATE");
            arrayList.add(__VIEWSTATE);
            arrayList.add("txtUserName");
            arrayList.add(Username);
            arrayList.add("TextBox2");
            arrayList.add(Password);
            arrayList.add("txtSecretCode");
            arrayList.add(code);
            arrayList.add("RadioButtonList1");
            arrayList.add("%D1%A7%C9%FA");
            arrayList.add("Button1");
            arrayList.add("#");
            arrayList.add("lbLanguage");
            arrayList.add("#");
            arrayList.add("hidPdrs");
            arrayList.add("#");
            arrayList.add("hidsc");
            arrayList.add("#");
            arrayList.add("__EVENTVALIDATION");
            arrayList.add(__EVENTVALIDATION);

            httpURLConnection = httpConnectionUtils.GetConnection("POST", arrayList, Cookies);
            Boolean connectResult = httpConnectionUtils.connect();
            System.out.println(connectResult );
            httpConnectionUtils.release();
            this.Cookies = Cookies;
            return getName();
//            System.out.println("名字是"+Name);
//            return connectResult;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // in.close();
        }

        return "";
    }

//    
//    public static void main(String[] args) {
//        //9562Aa
//        JwUtils jwUtils = new JwUtils("2", "E41414050", "9572Aa");
//        jwUtils.OrcImage_Login();
//    }

}
