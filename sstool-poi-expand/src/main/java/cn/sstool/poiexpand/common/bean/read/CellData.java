package cn.sstool.poiexpand.common.bean.read;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;

/**
 * 编  号：
 * 名  称：CellData
 * 描  述：
 * 完成日期：2019/6/19 23:25
 * @author：felix.shao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CellData {

    /** 列号 */
    private int colNum;

    /** 值 */
    private Object value;

    /** 样式 */
    @JSONField(serialize = false)
    private CellStyle cellStyle;

    /** 类型 */
    @JSONField(serialize = false)
    private CellType cellType;

}
