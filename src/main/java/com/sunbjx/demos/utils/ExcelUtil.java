package com.sunbjx.demos.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.StringUtils;

/**
 * 
 * @author sunbjx Date: 2017年6月21日上午10:48:17
 */
public class ExcelUtil {

	public static Workbook createWorkBook(List<Map<String, Object>> list, String[] keys, String columnNames[]) {
		// 创建excel工作簿
		Workbook wb = new HSSFWorkbook();
		// 创建第一个sheet（页），并命名
		// Sheet sheet =
		// wb.createSheet(list.get(0).get("sheetName").toString());
		Sheet sheet = wb.createSheet("sheet1");
		// 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
		for (int i = 0; i < keys.length; i++) {
			sheet.setColumnWidth((short) i, (short) (35.7 * 150));
		}

		// 创建第一行
		Row row = sheet.createRow((short) 0);

		// 创建两种单元格格式
		CellStyle cs = wb.createCellStyle();
		CellStyle cs2 = wb.createCellStyle();

		// 创建两种字体
		Font f = wb.createFont();
		Font f2 = wb.createFont();

		// 创建第一种字体样式（用于列名）
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.BLACK.getIndex());
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);

		// 创建第二种字体样式（用于值）
		f2.setFontHeightInPoints((short) 10);
		f2.setColor(IndexedColors.BLACK.getIndex());

		// Font f3=wb.createFont();
		// f3.setFontHeightInPoints((short) 10);
		// f3.setColor(IndexedColors.RED.getIndex());

		// 设置第一种单元格的样式（用于列名）
		cs.setFont(f);
		cs.setBorderLeft(CellStyle.BORDER_THIN);
		cs.setBorderRight(CellStyle.BORDER_THIN);
		cs.setBorderTop(CellStyle.BORDER_THIN);
		cs.setBorderBottom(CellStyle.BORDER_THIN);
		cs.setAlignment(CellStyle.ALIGN_CENTER);

		// 设置第二种单元格的样式（用于值）
		cs2.setFont(f2);
		cs2.setBorderLeft(CellStyle.BORDER_THIN);
		cs2.setBorderRight(CellStyle.BORDER_THIN);
		cs2.setBorderTop(CellStyle.BORDER_THIN);
		cs2.setBorderBottom(CellStyle.BORDER_THIN);
		cs2.setAlignment(CellStyle.ALIGN_CENTER);
		// 设置列名
		for (int i = 0; i < columnNames.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue(columnNames[i]);
			cell.setCellStyle(cs);
		}
		// 设置每行每列的值
		for (short i = 0; i < list.size(); i++) {
			// Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
			// 创建一行，在页sheet上
			Row row1 = sheet.createRow(i + 1);
			// 在row行上创建一个方格
			for (short j = 0; j < keys.length; j++) {
				Cell cell = row1.createCell(j);
				cell.setCellValue(list.get(i).get(keys[j]) == null ? " " : list.get(i).get(keys[j]).toString());
				cell.setCellStyle(cs2);
			}
		}
		return wb;
	}

	/**
	 * 向指定模版的excel写入数据
	 * 
	 * @param filePath
	 * @param x
	 * @param y
	 * @param value
	 */
	public static void specifiedWrite(String filePath, int x, int y, String value) {
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
			FileOutputStream os;
			os = new FileOutputStream(filePath);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void specifiedLoopWrite(String filePath, int dateX, int dateY, String dateStart, String dateEnd,
			int x, int y, List<Map<String, Object>> list) {
		try {
			// 创建Excel的工作书册 Workbook,对应到一个excel文档
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));
			// 设置单元格样式
			CellStyle cs = wb.createCellStyle();
			cs.setBorderLeft(CellStyle.BORDER_THIN);
			cs.setBorderRight(CellStyle.BORDER_THIN);
			cs.setBorderTop(CellStyle.BORDER_THIN);
			cs.setBorderBottom(CellStyle.BORDER_THIN);
			cs.setAlignment(CellStyle.ALIGN_CENTER);
			// 获取页脚
			HSSFSheet sheet = wb.getSheetAt(0);
			// 获取行
			HSSFRow row = sheet.getRow(dateX);
			// 获取单元格
			HSSFCell cell = row.getCell((short) dateY);
			cell.setCellValue(dateStart);
			cell.setCellStyle(cs);
			cell = row.getCell((short) ++dateY);
			cell.setCellValue("至");
			cell.setCellStyle(cs);
			cell = row.getCell((short) ++dateY);
			cell.setCellValue(dateEnd);
			cell.setCellStyle(cs);
			for (Map<String, Object> map : list) {
				row = sheet.getRow(x);
				for (Object value : map.values()) {
					cell = row.getCell((short) y);
					cell.setCellValue(StringUtils.isEmpty(String.valueOf(value)) ? "" : String.valueOf(value));
					cell.setCellStyle(cs);
					++y;
				}
				++x;
			}

			FileOutputStream os;
			os = new FileOutputStream(filePath);
			wb.write(os);
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
