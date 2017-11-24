package com.sunbjx.demos.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件下载工具类
 * @author sunbjx
 * Date: 2017年6月22日下午2:52:12
 */
public class DownloadFileUtils {
	public static boolean downLoadFile(String filePath, HttpServletResponse response) throws Exception {
		// 根据文件路径获得File文件
		File file = new File(filePath);
		// 取得文件名。
		String filename = file.getName();
		// 取得文件的后缀名。
		String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
		// 设置文件类型(这样设置就不止是下Excel文件了，一举多得)
		if ("pdf".equals(ext)) {
			response.setContentType("application/pdf;charset=GBK");
		} else if ("xls".equals(ext)) {
			response.setContentType("application/msexcel;charset=GBK");
		} else if ("doc".equals(ext)) {
			response.setContentType("application/msword;charset=GBK");
		}
		// 文件名
		response.setHeader("Content-Disposition",
				"attachment;filename=\"" + new String(filename.getBytes(), "ISO8859-1") + "\"");
		response.setContentLength((int) file.length());
		byte[] buffer = new byte[4096];// 缓冲区
		BufferedOutputStream output = null;
		BufferedInputStream input = null;
		try {
			output = new BufferedOutputStream(response.getOutputStream());
			input = new BufferedInputStream(new FileInputStream(file));
			int n = -1;
			// 遍历，开始下载
			while ((n = input.read(buffer, 0, 4096)) > -1) {
				output.write(buffer, 0, n);
			}
			output.flush(); // 不可少
			response.flushBuffer();// 不可少
		} catch (Exception e) {
			// 异常自己捕捉
		} finally {
			// 关闭流，不可少
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}
		return false;
	}
}
