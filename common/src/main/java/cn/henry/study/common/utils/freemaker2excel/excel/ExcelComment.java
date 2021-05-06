package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project cne-power-operation-web
 * @description: 单元格注释
 * @author 大脑补丁
 * @create: 2020-08-11 17:34
 */
public class ExcelComment {

	private String author;

	private ExcelData excelData;

	private ExcelFont excelFont;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public ExcelData getExcelData() {
		return excelData;
	}

	public void setExcelData(ExcelData excelData) {
		this.excelData = excelData;
	}

	public ExcelFont getExcelFont() {
		return excelFont;
	}

	public void setExcelFont(ExcelFont excelFont) {
		this.excelFont = excelFont;
	}
}