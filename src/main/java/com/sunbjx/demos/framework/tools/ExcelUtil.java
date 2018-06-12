package com.sunbjx.demos.framework.tools;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * @author sunbjx
 * @since 2018/5/13 15:15
 */
public class ExcelUtil {

    /**
     * 通用报表对象创建
     *
     * @param title       标题
     * @param headerNames 列头名
     * @param datas       数据
     * @return
     */
    public static HSSFWorkbook createCommonExcel(String title, List<String> headerNames, List<Object[]> datas) {

        HSSFWorkbook excel = null;

        if (headerNames == null || headerNames.size() == 0) {
            return excel;
        }

        //创建excel对象
        excel = new HSSFWorkbook();

        //创建worksheet对象
        HSSFSheet sheet = excel.createSheet();

        //创建字体
        HSSFFont cellFont = excel.createFont();
        cellFont.setFontHeightInPoints((short) 14);
        cellFont.setBold(true);
        cellFont.setFontName("黑体");

        //创建单元格样式
        HSSFCellStyle cellStyle = excel.createCellStyle();
        cellStyle.setFont(cellFont);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        //创建标题行
        HSSFRow titleRow = sheet.createRow(0);
        HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(title);
        titleCell.setCellStyle(cellStyle);
        //合并标题行
        int columnNum = headerNames.size();
        CellRangeAddress rangeAddress = new CellRangeAddress(0, 0, 0, columnNum - 1);
        sheet.addMergedRegion(rangeAddress);

        //创建表头
        HSSFRow headerRow = sheet.createRow(1);
        for (int i = 0; i < columnNum; i++) {
            //sheet.setColumnWidth(i, 20 * 256);
            // 英文自适应宽度
            //sheet.autoSizeColumn(i, true);
            // 中文自适应宽度
            sheet.setColumnWidth(i, headerNames.get(i).getBytes().length * 2 * 256);
            HSSFCell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerNames.get(i));
            headerCell.setCellStyle(cellStyle);
        }

        //添加数据行
        cellFont = excel.createFont();
        cellFont.setFontHeightInPoints((short) 12);
        cellFont.setFontName("黑体");

        cellStyle = excel.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setFont(cellFont);

        if (datas != null && datas.size() > 0) {
            //从第3行开始创建,因前面有标题行跟列头占据了两行
            int rownum = 2;
            for (Object[] rowData : datas) {
                HSSFRow dataRow = sheet.createRow(rownum);

                for (int i = 0; i < columnNum; i++) {
                    HSSFCell dataCell = dataRow.createCell(i);
                    dataCell.setCellValue(rowData[i] == null ? null : rowData[i].toString());
                    dataCell.setCellStyle(cellStyle);
                }
                rownum++;
            }
        }

        return excel;
    }
}
