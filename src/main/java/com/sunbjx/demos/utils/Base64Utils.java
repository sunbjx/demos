package com.sunbjx.demos.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by sunbjx on 2017/3/23.
 */
public class Base64Utils {

	// base64字符串转化成图片
	public static String GenerateImage(String imgStr) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return "";
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String imgFilePath = System.getProperty("user.dir") + "/head.jpg";// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return imgFilePath;
		} catch (Exception e) {
			return "";
		}
	}

	// 图片转化成base64字符串
	public static String GetImageStr(String imgFile) {
		// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
		// String imgFile = "d://test.jpg";//待处理的图片
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(imgFile);
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 对字节数组Base64编码
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串
	}

	// base64字符串转化成图片(ftp)
	public static String GenerateSpecifiedImage(String imgStr, String imageName) { // 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return "";
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpg图片
			String imgFilePath = System.getProperty("user.dir") + "/" + imageName + ".jpg";// 新生成的图片
			OutputStream out = new FileOutputStream(imgFilePath);
			out.write(b);
			out.flush();
			out.close();
			return imgFilePath;
		} catch (Exception e) {
			return "";
		}
	}

	public static String getRandomFourNum() {
		/*
		 * String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7",
		 * "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
		 * "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
		 * };
		 */
		String[] beforeShuffle = new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		List list = Arrays.asList(beforeShuffle);
		Collections.shuffle(list);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
		}
		String afterShuffle = sb.toString();
		String result = afterShuffle.substring(5, 9);
		return result;
	}

	public static boolean isMobileNO(String mobile) {
		if (mobile.length() != 11) {
			return false;
		} else {
			/**
			 * 移动号段正则表达式
			 */
			String pat1 = "^((13[4-9])|(147)|(15[0-2,7-9])|(178)|(18[2-4,7-8]))\\d{8}|(1705)\\d{7}$";
			/**
			 * 联通号段正则表达式
			 */
			String pat2 = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}|(1709)\\d{7}$";
			/**
			 * 电信号段正则表达式
			 */
			String pat3 = "^((133)|(153)|(177)|(18[0,1,9])|(149))\\d{8}$";
			/**
			 * 虚拟运营商正则表达式
			 */
			String pat4 = "^((170))\\d{8}|(1718)|(1719)\\d{7}$";

			Pattern pattern1 = Pattern.compile(pat1);
			Matcher match1 = pattern1.matcher(mobile);
			boolean isMatch1 = match1.matches();
			if (isMatch1) {
				return true;
			}
			Pattern pattern2 = Pattern.compile(pat2);
			Matcher match2 = pattern2.matcher(mobile);
			boolean isMatch2 = match2.matches();
			if (isMatch2) {
				return true;
			}
			Pattern pattern3 = Pattern.compile(pat3);
			Matcher match3 = pattern3.matcher(mobile);
			boolean isMatch3 = match3.matches();
			if (isMatch3) {
				return true;
			}
			Pattern pattern4 = Pattern.compile(pat4);
			Matcher match4 = pattern4.matcher(mobile);
			boolean isMatch4 = match4.matches();
			if (isMatch4) {
				return true;
			}
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println(getRandomFourNum());
		System.out.println(isMobileNO("17628296197"));
	}
}
