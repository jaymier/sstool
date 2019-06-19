package cn.sstool.poiexpand.common.bean.write.tag;

import cn.sstool.core.util.EmptyUtil;
import cn.sstool.core.util.StrUtil;
import cn.sstool.poiexpand.common.bean.read.CellData;
import cn.sstool.poiexpand.common.bean.read.RowData;
import cn.sstool.poiexpand.common.bean.write.WriteSheetData;
import cn.sstool.poiexpand.common.consts.SaxExcelConst;
import cn.sstool.poiexpand.common.consts.TagEnum;
import cn.sstool.core.util.ExprUtil;
import cn.sstool.poiexpand.common.util.write.TagUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 编  号：
 * 名  称：EachTagData
 * 描  述：迭代object的属性输出每个属性值，解决动态列模板导出
 * 完成日期：2019/6/19 23:45
 * @author：felix.shao
 */
@Data
@NoArgsConstructor
@Slf4j
public class EachTagData extends TagData{

    private String expr;

    private String modelName;

    /** 只遍历指定的属性 */
    private String onkeys;

    protected RowData readRowData;

    public EachTagData(RowData readRowData) {
        this.readRowData = readRowData;
    }

    @Override
    public String getRealExpr() {
        return null != value ?
                String.valueOf(value).replace(SaxExcelConst.TAG_KEY + TagEnum.EACH_TAG.getKey(), "").trim()
                : "";
    }

    @Override
    public void writeTagData(Workbook writeWb, SXSSFSheet writeSheet, WriteSheetData writeSheetData,
                             Map<String, Object> params, Map<String, CellStyle> writeCellStyleCache) {
        initExpr();
        initReadRowData(params);
        TagUtil.writeTagData(writeWb, writeSheet, writeSheetData, Arrays.asList(readRowData), params, writeCellStyleCache);
    }

    /**
     * each标签将根据数据动态初始化需要写入的数据
     *   只会读取第一个RowData的第一个列数据，然后根据数据以第一列复制多个列
     * @param params
     */
    private void initReadRowData(Map<String, Object> params){
        Map<String, CellData> cellDataMap = readRowData.getCellDatas();
        if (!EmptyUtil.isEmpty(cellDataMap)){
            CellData eachCellData = cellDataMap.get("0");

            Object iteratorObj = ExprUtil.getExprStrValue(params, expr);
            if(null == iteratorObj){
                return;
            }
            Iterator iterator;
            if(iteratorObj instanceof Map){
                iterator =  ExprUtil.getIterator(iteratorObj);
            } else {
                iterator =  ExprUtil.getIterator(ExprUtil.getBeanProperties(iteratorObj.getClass()));
            }

            int colNum = 0;
            while (iterator.hasNext()) {
                Object o = iterator.next();

                String property = "";
                if (o instanceof Field) {
                    property = ((Field) o).getName();
                } else if (o instanceof Map.Entry) {
                    property = ((Map.Entry) o).getKey().toString();
                } else if (o instanceof DynaProperty) {
                    property = ((DynaProperty) o).getName();
                } else {
                    property = o.toString();
                }

                property = SaxExcelConst.EXPR_START + modelName + "." + property + SaxExcelConst.EXPR_END;

                CellData colCellData = new CellData(colNum, property, eachCellData.getCellStyle(), eachCellData.getCellType());
                cellDataMap.put(String.valueOf(colNum ++), colCellData);
            }
        }
    }

    private void initExpr(){
        String realExpr = getRealExpr();
        if(StrUtil.isEmpty(realExpr)){
            return;
        }
        StringTokenizer st = new StringTokenizer(realExpr, " ");
        int pos = 0;
        while (st.hasMoreTokens()) {
            String str = st.nextToken();
            if (pos == 0) {
                expr = str;
            }
            if (pos == 1 && !"on".equals(str)) {
                onkeys = str;
            }
            if (pos == 2) {
                onkeys = str;
            }
            pos++;
        }

        modelName = expr.substring(SaxExcelConst.EXPR_START.length(), expr.length() - SaxExcelConst.EXPR_END.length());
    };

}
