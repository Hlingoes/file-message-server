
package cn.henry.study.common.freemaker2excel.output;

import java.io.Serializable;
import java.util.List;

/**
 * @project cne-power-operation-facade
 * @description: 结算申请单导出Excel--单电站信息
 * @author 大脑补丁
 * @create 2020-03-30 10:30
 */
public class StationBillOutput implements Serializable {
    // 发票数量
    // private Integer invoiceCount;
    // 描述
    private String description;
    // 计费周期
    private String period;
    // 尖峰平谷
    private List<PeriodPowerOutput> periodPowerList;
    // 园区地址
    private String stationName;
    // 发票号码
    private String invoiceNumber;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<PeriodPowerOutput> getPeriodPowerList() {
        return periodPowerList;
    }

    public void setPeriodPowerList(List<PeriodPowerOutput> periodPowerList) {
        this.periodPowerList = periodPowerList;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }
}
