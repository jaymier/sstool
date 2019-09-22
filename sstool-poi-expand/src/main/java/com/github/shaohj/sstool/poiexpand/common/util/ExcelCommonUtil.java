package com.github.shaohj.sstool.poiexpand.common.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCell;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMergeCells;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet;

import java.util.Date;

/**
 * 编  号：
 * 名  称：ExcelCommonUtil
 * 描  述：
 * 完成日期：2019/6/19 23:53
 * @author：felix.shao
 */
public class ExcelCommonUtil {

    /**
     * 获取Sheet最大的数据列数
     * @param sheet :
     * @return int  最大列数为3则返回3
     * @author SHJ
     */
    public static int getMaxCellNum(Sheet sheet){
        int rows = sheet.getPhysicalNumberOfRows();
        int maxCellNum = 0;

        for(int i=0; i< rows; i++){
            Row tRow = sheet.getRow(i);
            if(null != tRow){
                int tempNum = tRow.getLastCellNum();
                maxCellNum = tempNum > maxCellNum ? tempNum : maxCellNum;
            }
        }
        return maxCellNum;
    }

    /**
     * 获取excel所有的列宽
     * @param sheet
     * @param maxCellNum
     * @return
     */
    public static int[] getCellWidths(Sheet sheet, int maxCellNum){
        int[] cellWidths = new int[maxCellNum];

        for (int i = 0; i < maxCellNum; i++) {
            cellWidths[i] = sheet.getColumnWidth(i);
        }

        return cellWidths;
    }

    public static Object getCellValue(Cell cell){
        Object result = null;
        switch (cell.getCellTypeEnum()) {
            case BOOLEAN:
                result = cell.getBooleanCellValue(); break;
            case FORMULA:
                result = cell.getCellFormula(); break;
            case NUMERIC:
                result = cell.getNumericCellValue(); break;
            case STRING:
                result = cell.getStringCellValue(); break;
            default:
        }
        return result;
    }

    public static void setCellValue(Cell writeCell, Object value){
        if(null == value){
            writeCell.setCellValue("");
        } else {
            if ("java.lang.Integer".equals(value.getClass().getName())) {
                writeCell.setCellValue(Double.parseDouble(value.toString()));
            } else if ("java.lang.Double".equals(value.getClass().getName())) {
                writeCell.setCellValue(((Double) value).doubleValue());
            } else if ("java.util.Date".equals(value.getClass().getName())) {
                writeCell.setCellValue((Date) value);
            } else if ("java.lang.Boolean".equals(value.getClass().getName())) {
                writeCell.setCellValue(((Boolean) value).booleanValue());
            } else if ("java.lang.String".equals(value.getClass().getName())) {
                writeCell.setCellValue(value.toString());
            } else {
                writeCell.setCellValue(value.toString());
            }
        }
    }

    public static void addMergeRegion(SXSSFWorkbook sxssfWorkbok, String sheetName, CellRangeAddress region, int mergeCellsCount) {
        XSSFSheet sheet = sxssfWorkbok.getXSSFWorkbook().getSheet(sheetName);
        CTWorksheet ctWorksheet = sheet.getCTWorksheet();

        CTMergeCells ctMergeCells = mergeCellsCount > 0 ?ctWorksheet.getMergeCells():ctWorksheet.addNewMergeCells();
        CTMergeCell ctMergeCell = ctMergeCells.addNewMergeCell();

        ctMergeCell.setRef(region.formatAsString());
    }

}
