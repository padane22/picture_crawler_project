package com.Zol_Image_Download;

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

public class MultiThread_ZolImg implements Runnable {

	// picture count
	private static int numPic = 1;
	// waiting for getting
	private List<String> allWaitUrls = new ArrayList<>();
	// have got
	private Set<String> allOverUrls = new HashSet<>();
	
	//The numbers of Beginning and ending
	private int m_pageMin, m_pageMax;

	public MultiThread_ZolImg(int min, int max) {
		// TODO Auto-generated constructor stub
		m_pageMin = min;
		m_pageMax = max;
	}

	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		String seed = "http://desk.zol.com.cn/pc/";	
		// int pageMax = 1;
		for (int i = m_pageMin; i <= m_pageMax; i++) {
			System.out.println("Searching for Page " + i);
			if (i == 1)			//change 0 to 1 in Aug 27, Monday
				searchImgUrl(seed);
			else {
				String seedi = seed + i + ".html";
				searchImgUrl(seedi);
			}
		}// end for
			// System.out.println("Begin to download");
		/*
		 * while (allWaitUrls.size() > 0) { saveAllImg(allWaitUrls.get(0)); }
		 */
		System.out.println("All tasks have been finished!!!");
	}

	/**
	 * @param args	About Pictures
	 */

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

		// save other url
//		Elements eleOthers = document.getElementsByClass("pic");
//		for (Element e : eleOthers) {
//			String strOther = e.attr("href");
//			if (strOther.contains(".html")) {
//				if (!strOther.contains("http")) {
//					strOther = "http://desk.zol.com.cn" + strOther;
//					if (!allOverUrls.contains(strOther)
//							&& !allWaitUrls.contains(strOther)) {
//						allWaitUrls.add(strOther);
//					}
//				} else {
//					if (!allOverUrls.contains(strOther)
//							&& !allWaitUrls.contains(strOther)) {
//						allWaitUrls.add(strOther);
//					}
//				}
//			}
//
//		}

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
		Elements eles = document.getElementsByClass("pic");
		for (Element ele : eles) {
			String str = ele.attr("href");
			if (!str.contains("http")) {
				str = "http://desk.zol.com.cn" + str;
			}
			if (!allOverUrls.contains(str) && !allWaitUrls.contains(str)) {
				allWaitUrls.add(str);
			}
			// save Images
			while (allWaitUrls.size() > 0) {
				saveAllImg(allWaitUrls.get(0));
			}
		}// end for
	}// end function searchImgUrl

	// save image in the disk
	public void downImg(String imgHtml, String ImgClassification)
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
		String filePath = "/home/padane22/Documents/ZolImgClassified";
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		// 截取图片文件�?
		String fileName = ImgClassification
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
