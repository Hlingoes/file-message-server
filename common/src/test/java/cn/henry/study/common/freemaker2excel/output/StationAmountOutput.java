
package cn.henry.study.common.freemaker2excel.output;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @project cne-power-operation-facade
 * @description: 导出Excel-合计行
 * @author 大脑补丁
 * @create 2020-03-31 18:23
 */
public class StationAmountOutput implements Serializable {
    private BigDecimal power;
    private BigDecimal noTaxMoney;
    private BigDecimal taxAmount;
    private BigDecimal taxmoney;

    public BigDecimal getPower() {
        return power;
    }

    public void setPower(BigDecimal power) {
        this.power = power;
    }

    public BigDecimal getNoTaxMoney() {
        return noTaxMoney;
    }

    public void setNoTaxMoney(BigDecimal noTaxMoney) {
        this.noTaxMoney = noTaxMoney;
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
