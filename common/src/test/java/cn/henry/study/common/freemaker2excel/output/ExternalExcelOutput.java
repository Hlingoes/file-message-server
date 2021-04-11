package cn.henry.study.common.freemaker2excel.output;

import java.io.Serializable;

/**
 * @description: 外部项目Excel返回值
 * @create: 2020-08-05 17:48
 */
public class ExternalExcelOutput implements Serializable {

	/**
	 * 发票日期(后一个月最后一天)
	 */
	private String invoiceDate;

	/**
	 * 租赁编号
	 */
	private String leaseNumber;

	/**
	 * 结算单描述
	 */
	private String description;

	/**
	 * 账单编码
	 */
	private String billCode;

	/**
	 * 增值税税额 Tax-VAT。（公式：taxable_amount / tax_rate / 100）
	 */
	private String taxVat;

	/**
	 * 增值税税率 Tax rate-VAT
	 */
	private String taxRateVat;

	/**
	 * 增值税编码
	 */
	private String taxCode;

	/**
	 * 税额
	 */
	private String taxableAmount;

	/**
	 * 新能源费用日期(后一个月最后一天)
	 */
	private String glDate;

	/**
	 * 到期日期(后一个月最后一天)
	 */
	private String dueDate;

	/**
	 * 总金额（页面保留两位小数）。(公式=grossAmount/(1+taxRateVat/100))
	 */
	private String grossAmount;

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getLeaseNumber() {
		return leaseNumber;
	}

	public void setLeaseNumber(String leaseNumber) {
		this.leaseNumber = leaseNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBillCode() {
		return billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getTaxVat() {
		return taxVat;
	}

	public void setTaxVat(String taxVat) {
		this.taxVat = taxVat;
	}

	public String getTaxRateVat() {
		return taxRateVat;
	}

	public void setTaxRateVat(String taxRateVat) {
		this.taxRateVat = taxRateVat;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public String getTaxableAmount() {
		return taxableAmount;
	}

	public void setTaxableAmount(String taxableAmount) {
		this.taxableAmount = taxableAmount;
	}

	public String getGlDate() {
		return glDate;
	}

	public void setGlDate(String glDate) {
		this.glDate = glDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}
}