package cn.henry.study.common.freemaker2excel;


import cn.henry.study.common.freemaker2excel.output.ExternalExcelOutput;
import cn.henry.study.common.utils.freemaker2excel.FreemarkerUtils;
import cn.henry.study.common.utils.freemaker2excel.input.FreemarkerInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project freemarker-excel
 * @description: 带注释的Excel
 * @author: XuChao
 * @create: 2020-08-14 16:59
 */
public class ExportCommentExcel {

	public void export() {
		List<ExternalExcelOutput> list = new ArrayList<ExternalExcelOutput>();
		ExternalExcelOutput output = new ExternalExcelOutput();
		output.setInvoiceDate("2020-01-02");
		output.setLeaseNumber("0001");
		output.setDescription("描述呀");
		output.setBillCode("233333");
		output.setTaxVat("23");
		output.setTaxRateVat("44");
		output.setTaxCode("22222");
		output.setTaxableAmount("29.98");
		output.setGlDate("2020-01-02");
		output.setDueDate("2020-01-02");
		output.setGrossAmount("2000.23");

		ExternalExcelOutput output2 = new ExternalExcelOutput();
		output2.setInvoiceDate("2020-01-02");
		output2.setLeaseNumber("0001");
		output2.setDescription("描述呀");
		output2.setBillCode("233333");
		output2.setTaxVat("23");
		output2.setTaxRateVat("44");
		output2.setTaxCode("22222");
		output2.setTaxableAmount("29.98");
		output2.setGlDate("2020-01-02");
		output2.setDueDate("2020-01-02");
		output2.setGrossAmount("2000.23");

		list.add(output);
		list.add(output2);

		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("dataList", list);

		FreemarkerInput freemakerInput = new FreemarkerInput();
		freemakerInput.setDataMap(dataMap);
		freemakerInput.setTemplateName("externalProject.ftl");
		freemakerInput.setTemplateFilePath("");
		freemakerInput.setFileName("带注释的表格");
		freemakerInput.setXmlTempFile("export/temp/");
		FreemarkerUtils.exportImageExcelNew("export/带注释(2007版).xlsx", freemakerInput);
	}

}