package com.sunbjx.demos.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

public class Test {

	public static void main(String[] args) {

		String filePath = System.getProperty("user.dir") + "/src/main/resources/日报.xls";
		System.out.println(filePath);
		int x = 4;// 横向值,从1开始
		int y = 0;// 纵向值,从0开始
		String[] value = { "你我", "的", "天", "空", "美", "的", "天", "空", "美", "的", "天", "空", "你我", "的", "天", "空", "美", "的",
				"天", "空", "美", "的", "天", "空" };
		for (String string : value) {
			new Test().test(filePath, x, y, string);
			++y;
		}

	}

	public void test(String filePath, int x, int y, String value) {
		try {
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));
			CellStyle cs = wb.createCellStyle();
			cs.setBorderLeft(CellStyle.BORDER_THIN);
			cs.setBorderRight(CellStyle.BORDER_THIN);
			cs.setBorderTop(CellStyle.BORDER_THIN);
			cs.setBorderBottom(CellStyle.BORDER_THIN);
			cs.setAlignment(CellStyle.ALIGN_CENTER);
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFRow row = sheet.getRow(x);
			HSSFCell cell = row.getCell((short) y);
			cell.setCellValue(value);
			cell.setCellStyle(cs);
			FileOutputStream os;
			os = new FileOutputStream(filePath);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HttpServletResponse download(String path, HttpServletResponse response) {
		try {
			// path是指欲下载的文件的路径。
			path = System.getProperty("user.dir") + "/src/main/resources/日报.xls";
			File file = new File(path);
			// 取得文件名。
			String filename = file.getName();
			// 取得文件的后缀名。
			String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

			// 以流的形式下载文件。
			InputStream fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return response;
	}
}
