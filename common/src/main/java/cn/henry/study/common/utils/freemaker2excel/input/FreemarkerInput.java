package cn.henry.study.common.utils.freemaker2excel.input;

import java.util.List;
import java.util.Map;

/**
 * @author 大脑补丁
 * @project freemarker-excel
 * @description: FreeMarker导出带图片的Excel需要的参数对象
 * @create 2020-04-14 14:21
 */
public class FreemarkerInput {
	/**
	 * 加载数据
	 */
	private Map dataMap;
	/**
	 * 模版名称
	 */
	private String templateName;
	/**
	 * 模版路径
	 */
	private String templateFilePath;
	/**
	 * 生成文件名称
	 */
	private String fileName;

	/**
	 * xml缓存文件路径
	 */
	private String xmlTempFile;

	/**
	 * 插入图片信息
	 */
	private List<ExcelImageInput> excelImageInputs;

	public Map getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map dataMap) {
		this.dataMap = dataMap;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateFilePath() {
		return templateFilePath;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getXmlTempFile() {
		return xmlTempFile;
	}

	public void setXmlTempFile(String xmlTempFile) {
		this.xmlTempFile = xmlTempFile;
	}

	public List<ExcelImageInput> getExcelImageInputs() {
		return excelImageInputs;
	}

	public void setExcelImageInputs(List<ExcelImageInput> excelImageInputs) {
		this.excelImageInputs = excelImageInputs;
	}
}
