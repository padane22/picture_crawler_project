package com.Zol_Image_Download;

import java.io.*;
import java.net.*;
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

public class ZolUpdate {

	/**
	 * @param args
	 */
	//Record the name of pictures
	public static List<String> m_picName = new ArrayList<>();
	// The last time of updating
	public static String[] lastUpdateTime = "2019-02-01".split("-"); // **********************************change
																	// this
																	// after you
																	// run the
																	// progress
	//check if the search should continue
	public static boolean isNeedContinue = true;

	// picture count
	private static int numPic = 1;
	// waiting for getting
	private static List<String> allWaitUrls = new ArrayList<>();
	// have got
	private static Set<String> allOverUrls = new HashSet<>();

	public static void main(String[] args) {
		String seed = "http://desk.zol.com.cn/pc/";
		int pageMin = 1;
		int pageMax = 422;
		// int pageMax = 1;
		for (int i = pageMin; i <= pageMax; i++) {
			System.out.println("Searching for Page " + i);
			if (i == 1)			//change 0 to 1 in Aug 27, Monday
				searchImgUrl(seed);
			else {
				String seedi = seed + i + ".html";
				searchImgUrl(seedi);
			}
			if(!isNeedContinue){
				break;
			}
		}// end for
		
		if(m_picName.size() > 0)
			WriteUpdateLog();
		
		System.out.println("All Img have been updated!!!");
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
		// save current Img
		Element ele = document.getElementById("tagfbl");
		Elements es = ele.children();
		// Elements eleTargets = ele.getElementsByAttributeValue("target",
		// "_blank");
		String picBestUrl = es.get(0).attr("href");
		if (!picBestUrl.contains("http"))
			picBestUrl = "http://desk.zol.com.cn/" + picBestUrl;
		try {
			downImg(picBestUrl, document.getElementById("titleName").text());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		allWaitUrls.remove(0);
		allOverUrls.add(str);

		/*
		 * // save current Img Element ele = document.getElementById("bigImg");
		 * downImg(ele.attr("src")); allWaitUrls.remove(0);
		 * allOverUrls.add(str);
		 */

		// save next url
		Element eleNext = document.getElementById("pageNext");
		String nextUrl = eleNext.attr("href");
		if (nextUrl.contains(".html")) {
			if (!nextUrl.contains("http")) {
				nextUrl = "http://desk.zol.com.cn" + nextUrl;
				if (!allOverUrls.contains(nextUrl)
						&& !allWaitUrls.contains(nextUrl)) {
					allWaitUrls.add(nextUrl);
				}
			} else {
				if (!allOverUrls.contains(nextUrl)
						&& !allWaitUrls.contains(nextUrl)) {
					allWaitUrls.add(nextUrl);
				}
			}
		}

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
		
		Elements eles = document.getElementsByClass("pic");
		
		for (Element ele : eles) {
			String str = ele.attr("href");
			// get update time
			String strUpdateTime = "";
			try{
				strUpdateTime = ele.nextElementSibling().nextElementSibling().text();
			}
			catch(Exception e){
				
			}
			String[] updateTime = null;
			if(strUpdateTime != ""){
				updateTime = disposeTime(strUpdateTime);
			}
			
			
			if (!str.contains("http")) {
				str = "http://desk.zol.com.cn" + str;
			}
			if (!allOverUrls.contains(str) && !allWaitUrls.contains(str) && isNeedUpdate(updateTime)) {
				allWaitUrls.add(str);
			}
			// save Images
			while (allWaitUrls.size() > 0) {
				saveAllImg(allWaitUrls.get(0));
			}
		}// end for
	}// end function searchImgUrl
	
	//Check if this needs update
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
		else
		{
			isNeedContinue = false;
			return false;
		}
	}

	//dispose the string time
	public static String[] disposeTime(String str) {		
		String TimeYMD[] = str.split("-");
		return TimeYMD;
	}

	// save image in the disk
	public static void downImg(String imgHtml, String ImgClassification)
			throws IOException {
		Document document = Jsoup
				.connect(imgHtml)
				.userAgent(
						"Mozilla/5.0(Macintosh;U;IntelMacOSX10_6_8;en-us)AppleWebKit/534.50(KHTML,likeGecko)Version/5.1Safari/534.50")
				.get();
		Elements es = document.getElementsByTag("img");
		Element e1 = es.get(0);
		String imgSrc = e1.attr("src");
		// 若指定文件夹没有，则先创�?
		String filePath = "C:\\Users\\padane\\Pictures\\Zol";
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 截取图片文件�?
		String fileName = ImgClassification
				+ imgSrc.substring(imgSrc.lastIndexOf('/') + 1, imgSrc.length());

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
			System.out.println("Updated a Img: " + fileName + "   total: "
					+ numPic++);
		} else
			System.out.println("Have been exited : " + fileName);

	}// endFunctionDownImg
	
	//Function of writing the log
	public static void WriteUpdateLog(){
		Calendar c = Calendar.getInstance();
		
		Date d = c.getTime();
		
		String filePath = "C:\\Users\\padane\\Pictures";
		String fileName = "ZolImgUpdate_Log.txt";
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

}// endClassZolImageDownload
