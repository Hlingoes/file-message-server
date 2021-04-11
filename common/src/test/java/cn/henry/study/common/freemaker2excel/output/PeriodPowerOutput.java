
package cn.henry.study.common.freemaker2excel.output;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @project cne-power-operation-facade
 * @description: 电费申请单Excel中尖峰平谷部分
 * @author 大脑补丁
 * @create 2020-03-30 10:30
 */
public class PeriodPowerOutput implements Serializable {
    // 尖、峰、平、谷
    private String powerName;
    // 电量(尖、峰、平、谷、合计)
    private BigDecimal power;
    // 含税电价(尖、峰、平、谷、合计)
    private BigDecimal price;
    // 不含税金额(尖、峰、平、谷、合计)
    private BigDecimal noTaxMoney;
    // 税率(尖、峰、平、谷、合计)
    private Integer taxRate;
    // 税额(尖、峰、平、谷、合计)
    private BigDecimal taxAmount;
    // 含税金额(尖、峰、平、谷、合计)
    private BigDecimal taxmoney;

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName;
    }

    public BigDecimal getPower() {
        return power;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNoTaxMoney() {
        return noTaxMoney;
    }

    public void setNoTaxMoney(BigDecimal noTaxMoney) {
        this.noTaxMoney = noTaxMoney;
    }

    public Integer getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Integer taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxmoney() {
        return taxmoney;
    }

    public void setTaxmoney(BigDecimal taxmoney) {
        this.taxmoney = taxmoney;
    }
}
