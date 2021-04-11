package cn.henry.study.common.freemaker2excel;

import cn.henry.study.common.freemaker2excel.output.PeriodPowerOutput;
import cn.henry.study.common.freemaker2excel.output.SendBillOutput;
import cn.henry.study.common.freemaker2excel.output.StationAmountOutput;
import cn.henry.study.common.freemaker2excel.output.StationBillOutput;
import cn.henry.study.common.utils.freemaker2excel.FreemarkerUtils;
import cn.henry.study.common.utils.freemaker2excel.input.ExcelImageInput;
import cn.henry.study.common.utils.freemaker2excel.input.FreemarkerInput;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class ExportImageExcel {

	/**
	 * 导出带有图片的Excel示例,2003版xsl格式
	 */
	public void export2003() {
		String imagePath = "";
		List<ExcelImageInput> excelImageInputs = new ArrayList<>();
		try {
			Enumeration<URL> urlEnumeration = this.getClass().getClassLoader().getResources("templates/image.png");
			URL url = urlEnumeration.nextElement();
			imagePath = url.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 若改变图片位置，修改后4个参数
		HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, (short) 16, 1, (short) 26, 27);
		ExcelImageInput excelImageInput = new ExcelImageInput(imagePath, 0, anchor);

		excelImageInputs.add(excelImageInput);
		FreemarkerInput freemarkerInput = new FreemarkerInput();
		freemarkerInput.setTemplateName("发票.ftl");
		freemarkerInput.setTemplateFilePath("");
		freemarkerInput.setDataMap(getExcelData());
		freemarkerInput.setXmlTempFile("export/temp/");
		// 若导出不带图片的Excel，此参数为空即可
		freemarkerInput.setExcelImageInputs(excelImageInputs);
		freemarkerInput.setFileName("导出带图片Excel缓存文件");
		// 导出到项目所在目录下，export文件夹中
		FreemarkerUtils.exportImageExcel("export/带图片(2003版).xls", freemarkerInput);
	}

	/**
	 * 导出带有图片的Excel2007版，XLSX格式
	 */
	public void export2007() {
		String imagePath = "";
		List<ExcelImageInput> excelImageInputs = new ArrayList<>();
		try {
			Enumeration<URL> urlEnumeration = this.getClass().getClassLoader().getResources("templates/image.png");
			URL url = urlEnumeration.nextElement();
			imagePath = url.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 若改变图片位置，修改后4个参数
		XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 16, 1, (short) 26, 27);
		ExcelImageInput excelImageInput = new ExcelImageInput(imagePath, 0, anchor);
		excelImageInputs.add(excelImageInput);
		FreemarkerInput freemarkerInput = new FreemarkerInput();
		freemarkerInput.setTemplateName("发票.ftl");
		freemarkerInput.setTemplateFilePath("");
		freemarkerInput.setDataMap(getExcelData());
		freemarkerInput.setXmlTempFile("export/temp/");
		// 若导出不带图片的Excel，此参数为空即可
		freemarkerInput.setExcelImageInputs(excelImageInputs);
		freemarkerInput.setFileName("导出带图片Excel缓存文件");
		// 导出到项目所在目录下，export文件夹中
		FreemarkerUtils.exportImageExcelNew("export/带图片(2007版).xlsx", freemarkerInput);
	}

	// 模拟Excel假数据数据
	private Map<String, Object> getExcelData() {
		SendBillOutput bill = new SendBillOutput();
		bill.setCustomerName("奥迪公司");
		bill.setIsGeneralTaxpayer("是");
		bill.setTaxNumber("123456789");
		bill.setAddressAndPhone("北京市望京SOHO" + "&#10;" + "010-8866396");
		bill.setBankAndAccount("中国银行&#10;123456");
		List<StationBillOutput> stationBillList = new ArrayList<StationBillOutput>();
		// 模拟n个电站
		for (int i = 0; i < 5; i++) {
			StationBillOutput stationBillOutput = new StationBillOutput();
			stationBillOutput.setDescription("奥迪公司3月份电费" + i);
			stationBillOutput.setPeriod("2020年03月01日_2020年03月31日");
			// 尖峰平谷时间段数据赋值
			List<PeriodPowerOutput> periodPowerList = new ArrayList<PeriodPowerOutput>();
			for (int j = 0; j < 5; j++) {
				PeriodPowerOutput periodPower = new PeriodPowerOutput();
				switch (j) {
				case 0:
					periodPower.setPowerName("尖");
					break;
				case 1:
					periodPower.setPowerName("峰");
					break;
				case 2:
					periodPower.setPowerName("平");
					break;
				case 3:
					periodPower.setPowerName("谷");
					break;
				case 4:
					periodPower.setPowerName("合计");
					break;
				default:
					break;
				}
				periodPower.setPower(new BigDecimal(j + 1000));
				periodPower.setPrice(new BigDecimal(j + 0.1));
				// 若Excel公式自动计算，这几个字段不用插值
				periodPower.setNoTaxMoney(new BigDecimal(j + 1002));
				periodPower.setTaxRate(13);
				periodPower.setTaxAmount(new BigDecimal(j + 1004));
				periodPower.setTaxmoney(new BigDecimal(j + 1005));
				periodPowerList.add(periodPower);
			}
			stationBillOutput.setPeriodPowerList(periodPowerList);
			stationBillOutput.setStationName("奥迪公司园区" + i + 1);
			stationBillList.add(stationBillOutput);
		}
		bill.setStationBillList(stationBillList);
		StationAmountOutput stationAmountOutput = new StationAmountOutput();
		stationAmountOutput.setPower(new BigDecimal(123));
		stationAmountOutput.setNoTaxMoney(new BigDecimal(456));
		stationAmountOutput.setTaxAmount(new BigDecimal(789));
		stationAmountOutput.setTaxmoney(new BigDecimal(2324));
		bill.setStationAmount(stationAmountOutput);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bill", bill);
		return map;
	}
}
