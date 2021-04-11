
package cn.henry.study.common.freemaker2excel.output;

import java.io.Serializable;
import java.util.List;

/**
 * @project cne-power-operation-facade
 * @description: 导出Excel：申请单
 * @author 大脑补丁
 * @create 2020-03-26 15:26
 */
public class SendBillOutput implements Serializable {

    // 客户名称
    private String customerName;
    // 是否一般纳税人
    private String isGeneralTaxpayer;
    // 税号
    private String taxNumber;
    // 客户公司地址及电话
    private String addressAndPhone;
    // 开户银行和账号
    private String bankAndAccount;
    // 信息列表
    private List<StationBillOutput> stationBillList;
    // 合计栏
    private StationAmountOutput stationAmount;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIsGeneralTaxpayer() {
        return isGeneralTaxpayer;
    }

    public void setIsGeneralTaxpayer(String isGeneralTaxpayer) {
        this.isGeneralTaxpayer = isGeneralTaxpayer;
    }

    public String getTaxNumber() {
        return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    public String getAddressAndPhone() {
        return addressAndPhone;
    }

    public void setAddressAndPhone(String addressAndPhone) {
        this.addressAndPhone = addressAndPhone;
    }

    public String getBankAndAccount() {
        return bankAndAccount;
    }

    public void setBankAndAccount(String bankAndAccount) {
        this.bankAndAccount = bankAndAccount;
    }

    public List<StationBillOutput> getStationBillList() {
        return stationBillList;
    }

    public void setStationBillList(List<StationBillOutput> stationBillList) {
        this.stationBillList = stationBillList;
    }

    public StationAmountOutput getStationAmount() {
        return stationAmount;
    }

    public void setStationAmount(StationAmountOutput stationAmount) {
        this.stationAmount = stationAmount;
    }
}
