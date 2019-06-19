package cn.sstool.poiexpand.sax07;

import cn.sstool.core.util.EmptyUtil;
import cn.sstool.poiexpand.common.bean.write.WriteSheetData;
import cn.sstool.poiexpand.common.bean.write.tag.PageForeachTagData;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 编  号：
 * 名  称：Sax07ExcelWriteUtil
 * 描  述：
 * 完成日期：2019/02/04 16:17
 *
 * @author：felix.shao
 */
public class Sax07ExcelWriteUtil {

    /**
     * 写入sheet数据，没有大数据分页标签
     * @param writeWb
     * @param params
     * @param writeSheetDatas
     */
    public static void writeSheetData(SXSSFWorkbook writeWb, Map<String, Object> params, List<WriteSheetData> writeSheetDatas){
        List<Sax07ExcelPageWriteService> sax07ExcelPageWriteServices = new ArrayList<>(0);
        writeSheetData(writeWb, params, writeSheetDatas, sax07ExcelPageWriteServices);
    }

    /**
     * 写入sheet数据，有一个大数据分页标签
     * @param writeWb
     * @param params
     * @param writeSheetDatas
     * @param sax07ExcelPageWriteService
     */
    public static void writeSheetData(SXSSFWorkbook writeWb, Map<String, Object> params, List<WriteSheetData> writeSheetDatas, Sax07ExcelPageWriteService sax07ExcelPageWriteService){
        List<Sax07ExcelPageWriteService> sax07ExcelPageWriteServices = new ArrayList<>(1);
        sax07ExcelPageWriteServices.add(sax07ExcelPageWriteService);
        writeSheetData(writeWb, params, writeSheetDatas);
    }

    /**
     * 写入sheet数据，有多个大数据分页标签
     * @param writeWb
     * @param params
     * @param writeSheetDatas
     * @param sax07ExcelPageWriteServices
     */
    public static void writeSheetData(SXSSFWorkbook writeWb, Map<String, Object> params, List<WriteSheetData> writeSheetDatas, List<Sax07ExcelPageWriteService> sax07ExcelPageWriteServices){
        if(EmptyUtil.isEmpty(writeSheetDatas)){
            return;
        }
        writeSheetDatas.stream().forEach(writeSheetData -> {
            //创建sheet
            SXSSFSheet writeSheet =  writeWb.createSheet(writeSheetData.getSheetName());

            /** 设置列宽为模板文件的列宽 */
            if(!EmptyUtil.isEmpty(writeSheetData.getCellWidths())){
                for (int i = 0; i < writeSheetData.getCellWidths().length; i++) {
                    writeSheet.setColumnWidth(i, writeSheetData.getCellWidths()[i]);
                }
            }

            if(EmptyUtil.isEmpty(writeSheetData.getWriteTagDatas())){
                return;
            }
            // 样式需要做缓存特殊处理，以sheet为单位作缓存处理，定义在此保证线程安全
            final Map<String, CellStyle> writeCellStyleCache = new HashMap<>();
            writeSheetData.getWriteTagDatas().forEach((readRowNum, tagData) -> {
                if(tagData instanceof PageForeachTagData){
                    // 大数据导出service
                    if(null != tagData.getValue() && !EmptyUtil.isEmpty(sax07ExcelPageWriteServices)){
                        sax07ExcelPageWriteServices.stream()
                                .filter(sax07ExcelPageWriteService -> sax07ExcelPageWriteService.getExprVal().equalsIgnoreCase(String.valueOf(tagData.getValue())))
                                .findFirst().ifPresent(sax07ExcelPageWriteService -> {
                            sax07ExcelPageWriteService.init(tagData, writeWb, writeSheet, writeSheetData, writeCellStyleCache);
                            sax07ExcelPageWriteService.pageWriteData();
                        });
                    }
                } else {
                    tagData.writeTagData(writeWb, writeSheet, writeSheetData, params, writeCellStyleCache);
                }
            });
        });
    }

}
