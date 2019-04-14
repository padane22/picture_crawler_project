package com.NetBian;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MultiThread_bian implements Runnable{

	public static int numPic = 1;
	public static int intThreadName = 1;

	// waiting for getting
	private List<String> allWaitUrls = new ArrayList<>();
	// have got
	private Set<String> allOverUrls = new HashSet<>();
	
	private int m_pageBegin;
	private int m_pageEnd;
	
	public MultiThread_bian(int min, int max){
		m_pageBegin = min;
		m_pageEnd = max;
	}
	
	@Override
	public void run() {
		// prevent from being full
		if(m_pageEnd > 422)
			m_pageEnd = 422;
		
		for (int i = m_pageBegin; i <= m_pageEnd; i++) {
			String str;
			if(i == 1)
				str = "http://www.netbian.com/2560x1440/index.htm";
			else
				str = "http://www.netbian.com/2560x1440/index_" + i + ".htm";
			System.out.println("searching for Page: " + i);
			searchImgUrl(str);
		}
		
		System.out.println("Thread " + intThreadName + "`s tasks have been finished!!!");
		
	}
	
	public void start(){
		Thread t = new Thread(this);
		t.start();
		intThreadName++;
	}
	
	public void searchImgUrl(String seedUrl) {
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
		Elements eles = document.getElementsByTag("ul");
		Element el = eles.get(1);
		Elements eles2 = el.children().select("li");
		for (Element ele : eles2) {
			String str = ele.child(0).attr("href");
			if (!str.contains("http") && str != "") {
				str = "http://www.netbian.com" + str;
			} else
				str = "";
			if (!allOverUrls.contains(str) && !allWaitUrls.contains(str)
					&& str != "") {
				allWaitUrls.add(str);
			}
			// save Images
			while (allWaitUrls.size() > 0) {
				saveAllImg(allWaitUrls.get(0));
			}
		}// end for
	}

	public void saveAllImg(String str) {

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
		String picName = document.getElementsByClass("actionb").get(0).text();
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

	public void downImg(String imgHtml, String ImgName)
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
		String filePath = "/home/padane22/Documents/Bi_An_Img";
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

			System.out.println("Finished a Img: " + fileName + "   total: "
					+ numPic++);
		} else
			System.out.println("Have been exited : " + fileName);

	}// endFunctionDownImg

}
