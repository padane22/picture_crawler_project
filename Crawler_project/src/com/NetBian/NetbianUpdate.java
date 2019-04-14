package com.NetBian;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NetbianUpdate {

	//check if the search should continue
	public static boolean isNeedContinue = true;
		
	//Record the name of pictures
	public static List<String> m_picName = new ArrayList<String>();
	//Count the number of pictures
	public static int numPic = 1;
	// The last time of updating
	public static String[] lastUpdateTime = "2019-03-15".split("-");			//**********************************

	// waiting for getting
	private static List<String> allWaitUrls = new ArrayList<>();
	// have got
	private static Set<String> allOverUrls = new HashSet<>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int pageBegin = 1;
		int pageEnd = 115;
		for (int i = pageBegin; i <= pageEnd; i++) {
			System.out.println("Searching for Page " + i);
			String str;
			if (i == 1)
				str = "http://www.netbian.com/2560x1440/index.htm";
			else
				str = "http://www.netbian.com/2560x1440/index_" + i + ".htm";
			searchImgUrl(str);
			
			if(!isNeedContinue)
				break;
		}
		
		//Write a log
		if(m_picName.size() > 0)
			WriteUpdateLog();
		
		//Finished
		System.out.println("Finished all update!!!");
	}

	public static void searchImgUrl(String seedUrl) {
		Document document = null;
		try {
			document = Jsoup
					.connect(seedUrl)
					.userAgent(
							"Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50")
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get http elements
		Elements eles = document.getElementsByTag("ul");
		Element el = eles.get(1);									//*L*******11-25
		Elements eles2 = el.children().select("li");
		for (Element ele : eles2) {
			String str = ele.child(0).attr("href");
			// get update time
			String strUpdateTime = "";
			String[] updateTime = null;

			if (!str.contains("http") && str != "") {
				str = "http://www.netbian.com" + str;
				// get update time
				//strUpdateTime = ele.children().select("p").get(0).text();
				strUpdateTime = ele.child(0).attr("title");
				updateTime = disposeTime(strUpdateTime);
			} else
				str = "";

			if (!allOverUrls.contains(str) && !allWaitUrls.contains(str)
					&& str != "" && isNeedUpdate(updateTime)) {
				allWaitUrls.add(str);
			}
			// save Images
			while (allWaitUrls.size() > 0) {
				saveAllImg(allWaitUrls.get(0));
			}
		}// end for
	}

	public static boolean isNeedUpdate(String[] updateTime) {
		if (updateTime == null)
			return false;
		else if (Integer.parseInt(updateTime[0]) > Integer
				.parseInt(lastUpdateTime[0]))
			return true;
		else if ((Integer.parseInt(updateTime[0]) == Integer
				.parseInt(lastUpdateTime[0]))
				&& (Integer.parseInt(updateTime[1]) > Integer
						.parseInt(lastUpdateTime[1])))
			return true;
		else if ((Integer.parseInt(updateTime[0]) == Integer
				.parseInt(lastUpdateTime[0]))
				&& (Integer.parseInt(updateTime[1]) == Integer
						.parseInt(lastUpdateTime[1]))
				&& (Integer.parseInt(updateTime[2]) > Integer
						.parseInt(lastUpdateTime[2])))
			return true;
		else{
			isNeedContinue = false;
			return false;
		}
	}

	public static String[] disposeTime(String str) {
		String updateTime = str.split("��")[1];			//Chinese colon
		String TimeYMD[] = updateTime.split("-");
		return TimeYMD;
	}

	public static void saveAllImg(String str) {

		Document document = null;
		try {
			document = Jsoup
					.connect(str)
					.userAgent(
							"Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50")
					.get();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// save Img
		Elements es = document.getElementsByClass("pic-down");
		String picBestUrl = es.get(0).child(0).attr("href");
		String picName = "";
		
		//two kinds of page form
		if(document.getElementsByClass("actionb").isEmpty()){		
			picName = document.getElementsByClass("action").get(0).getElementsByTag("h1").get(0).text(); //2018-11-25
			}
		else{
			picName = document.getElementsByClass("action").get(0).text(); 
			}
		
		//keep the file name far from the address
		if(picName.contains("/")){
			picName = picName.replaceAll("/", "");
		}
		
		if (!picBestUrl.contains("http"))
			picBestUrl = "http://www.netbian.com" + picBestUrl;
		try {
			downImg(picBestUrl, picName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		allWaitUrls.remove(0);
		allOverUrls.add(str);
	}

	public static void downImg(String imgHtml, String ImgName)
			throws IOException {
		Document document = Jsoup
				.connect(imgHtml)
				.userAgent(
						"Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50")
				.get();
		Elements es = document.getElementsByTag("img");
		Element e1 = es.get(1);
		String imgSrc = e1.attr("src");
		// 若指定文件夹没有，则先创�?
		String filePath = "C:\\Users\\padane\\Pictures\\NetBian";
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 截取图片文件�?
		String fileName = ImgName
				+ imgSrc.substring(imgSrc.lastIndexOf('/') + 1, imgSrc.length());

		// try {
		// // 文件名里面可能有中文或�?�空格，�?以这里要进行处理。但空格又会被URLEncoder转义为加�?
		// String urlTail = URLEncoder.encode(fileName, "UTF-8");
		// // 因此要将加号转化为UTF-8格式�?%20
		// imgSrc = imgSrc.substring(0, imgSrc.lastIndexOf('/') + 1)
		// + urlTail.replaceAll("\\+", "\\%20");
		//
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// }
		// 写出的路�?
		File file = new File(filePath + File.separator + fileName);

		if (!file.exists()) {
			try {
				// 获取图片URL
				URL url = new URL(imgSrc);
				// 获得连接
				URLConnection connection = url.openConnection();
				// 设置10秒的相应时间
				connection.setConnectTimeout(10 * 1000);
				// 获得输入�?
				InputStream in = connection.getInputStream();
				// 获得输出�?
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				// 构建缓冲�?
				byte[] buf = new byte[1024];
				int size;
				// 写入到文�?
				while (-1 != (size = in.read(buf))) {
					out.write(buf, 0, size);
				}
				out.close();
				in.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			m_picName.add(fileName);
			System.out.println("Update a Img: " + fileName + "   total: "
					+ numPic++);
		} else
			System.out.println("Have been exited : " + fileName);

	}// endFunctionDownImg
	
	public static void WriteUpdateLog(){
		Calendar c = Calendar.getInstance();
		
		Date d = c.getTime();
		
		String filePath = "C:\\Users\\padane\\Pictures";
		String fileName = "BianUpdate_Log.txt";
		File f = new File(filePath + File.separator + fileName);
		
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Create File failed: " + fileName);
			}
		}
		
		try {
			FileWriter fw = new FileWriter(f, true);
			fw.write("The last time for update is :  total pictures - " + (numPic - 1) + "\n");
			fw.write(d.toString() + "\n");
			fw.write("The details: \n");
			for (int i = 0; i < m_picName.size(); i++){
				fw.write(m_picName.get(i) + "\n");
			}
			fw.write("=================END======================\n");
			//Blank at last
			fw.write("\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Open file failed: " + f.getName());
		}
		
		//System.out.println(d.toString());
	}//EndFunctionWriteLog

}
