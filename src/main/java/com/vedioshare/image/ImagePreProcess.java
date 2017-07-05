package com.vedioshare.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImagePreProcess {

    private static Map<BufferedImage, String> trainMap = null;

    private static int index = 0;

    // ��֤���ַ
    public static String getImageUrl = "http://xk2.ahu.cn/CheckCode.aspx";

    public static String srcPath = "img\\";

    public static String trainPath = "train\\";

    public static String tempPath = "temp\\";
    
    public static String resultPath = "";

    public static int isBlue(int colorInt) {
        Color color = new Color(colorInt);
        int rgb = color.getRed() + color.getGreen() + color.getBlue();
        if (rgb == 153) {
            return 1;
        }
        return 0;
    }

    public static int isBlack(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() <= 100) {
            return 1;
        }
        return 0;
    }

    public static int isWhite(int colorInt) {
        Color color = new Color(colorInt);
        if (color.getRed() + color.getGreen() + color.getBlue() > 600) {
            return 1;
        }
        return 0;
    }

    /**
     * ȥ����������ֵ��
     * 
     * @param picFile
     * @return
     * @throws Exception
     */
    public static BufferedImage removeBackgroud(String picFile) throws Exception {
        BufferedImage img = ImageIO.read(new File(picFile));
        img = img.getSubimage(5, 1, img.getWidth() - 5, img.getHeight() - 2);
        img = img.getSubimage(0, 0, 50, img.getHeight());
        int width = img.getWidth();
        int height = img.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (isBlue(img.getRGB(x, y)) == 1) {
                    img.setRGB(x, y, Color.BLACK.getRGB());
                } else {
                    img.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return img;
    }

    /**
     * ���Լ��Ĺ���ָ���֤��
     * 
     * @param img
     * @return
     * @throws Exception
     */
    public static List<BufferedImage> splitImage(BufferedImage img) throws Exception {
        List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
        int width = img.getWidth() / 4;
        int height = img.getHeight();
        subImgs.add(img.getSubimage(0, 0, width, height));
        subImgs.add(img.getSubimage(width, 0, width, height));
        subImgs.add(img.getSubimage(width * 2, 0, width, height));
        subImgs.add(img.getSubimage(width * 3, 0, width, height));
        return subImgs;
    }

    /**
     * ����ѵ���õ�����
     * 
     * @return
     * @throws Exception
     */
    public static Map<BufferedImage, String> loadTrainData() throws Exception {
        if (trainMap == null) {
            Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
            File dir = new File(ImagePreProcess.trainPath);
            File[] files = dir.listFiles();
            //System.out.println(files.toString());
            for (File file : files) {
                map.put(ImageIO.read(file), file.getName().charAt(0) + "");
            }
            trainMap = map;
        }
        return trainMap;
    }

    /**
     * ʶ��ָ�ĵ����ַ�
     * 
     * @param img
     * @param map
     * @return
     */
    public static String getSingleCharOcr(BufferedImage img, Map<BufferedImage, String> map) {
        String result = "#";
        int width = img.getWidth();
        int height = img.getHeight();
        int min = width * height;
        for (BufferedImage bi : map.keySet()) {
            int count = 0;
            if (Math.abs(bi.getWidth() - width) > 2)
                continue;
            int widthmin = width < bi.getWidth() ? width : bi.getWidth();
            int heightmin = height < bi.getHeight() ? height : bi.getHeight();
            Label1: for (int x = 0; x < widthmin; ++x) {
                for (int y = 0; y < heightmin; ++y) {
                    if (isBlack(img.getRGB(x, y)) != isBlack(bi.getRGB(x, y))) {
                        count++;
                        if (count >= min)
                            break Label1;
                    }
                }
            }
            if (count < min) {
                min = count;
                result = map.get(bi);
            }
        }
        return result;
    }

    /**
     * ��֤��ʶ��
     * 
     * @param file Ҫ��֤����֤�뱾��·��
     * @return
     * @throws Exception
     */
    public static String getAllOcr(String file) throws Exception {
        BufferedImage img = removeBackgroud(file);
        List<BufferedImage> listImg = splitImage(img);
        Map<BufferedImage, String> map = loadTrainData();
        String result = "";
        for (BufferedImage bi : listImg) {
            result += getSingleCharOcr(bi, map);
        }
        ImageIO.write(img, "PNG", new File(ImagePreProcess.resultPath + result + ".png"));
        return result;
    }

    /**
     * ������֤��
     * 
     * @param url
     * @throws MalformedURLException
     */
    public static String downloadImage(String url) {
        HttpURLConnection httpURLConnection = null;
        String responseCookie = null;
        FileOutputStream fos = null;
        InputStream in = null;

        try {
            URL ImageUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) ImageUrl.openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("GET");
            in = httpURLConnection.getInputStream();
            
            responseCookie = httpURLConnection.getHeaderField("Set-Cookie");
            responseCookie = responseCookie.substring(0, 42);
            
            File file = new File(srcPath + '0' + ".png");
            System.out.println("验证码Code Path"+file.getAbsolutePath());
            fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int length = 0;
            while ((length = in.read(b)) != -1) {
                fos.write(b, 0, length);
            }
            System.out.println("cookie:" + responseCookie);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        }
        return responseCookie;

        // CloseableHttpClient httpClient = JwUtils.getHttpClient();
        // for(int i=0; i<10; i++){
        // HttpGet getMethod = new HttpGet(url);
        // HttpResponse response = null;
        // try {
        // response = httpClient.execute(getMethod);
        // if("HTTP/1.1 200 OK".equals(response.getStatusLine().toString())){
        // HttpEntity entity = response.getEntity();
        //
        // InputStream is = entity.getContent();
        // OutputStream os = new FileOutputStream(new File(srcPath+i+".png"));
        // int length = -1;
        // byte[] bytes = new byte[1024];
        // while((length = is.read(bytes)) != -1){
        // os.write(bytes, 0, length);
        // }
        // os.close();
        // }
        // } catch (ClientProtocolException e) {
        // e.printStackTrace();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
    }

    /**
     * ����ѵ��
     * 
     * @throws Exception
     */
    public static void trainData() throws Exception {
        File dir = new File("temp\\");
        File[] files = dir.listFiles();
        for (File file : files) {
            BufferedImage img = removeBackgroud("temp\\" + file.getName());
            List<BufferedImage> listImg = splitImage(img);
            if (listImg.size() == 4) {
                for (int j = 0; j < listImg.size(); ++j) {
                    ImageIO.write(listImg.get(j), "PNG", new File("train\\" + file.getName().charAt(j) + "-"
                            + (index++) + ".png"));
                }
            }
        }
    }

    /**
     * @param args
     * @throws Exception
   
    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        downloadImage(getImageUrl);
        System.out.println("�������");
        long DownEnd = System.currentTimeMillis();

        System.out.println(String.valueOf(startTime - DownEnd));
        startTime = System.currentTimeMillis();
        // trainData();
        // for (int i = 0; i < 10; ++i) {
        // String text = getAllOcr("img\\" + i + ".png");
        // System.out.println(i + ".png = " + text);
        // }
        DownEnd = System.currentTimeMillis();
        System.out.println(String.valueOf(DownEnd - startTime));

    }  */

}
