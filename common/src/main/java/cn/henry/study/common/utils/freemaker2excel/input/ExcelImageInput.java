package cn.henry.study.common.utils.freemaker2excel.input;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;

import java.io.Serializable;
/**
 * @project freemarker-excel
 * @description: 自定义解析excel的图片解析类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class ExcelImageInput implements Serializable {

	/**
	 * 图片地址
	 */
	private String imgPath;

	/**
	 * sheet索引
	 */
	private Integer sheetIndex;

	/**
	 * 图片所在位置坐标（xls格式版，HSSFClientAnchor与XSSFClientAnchor只能二选一）
	 */
	private HSSFClientAnchor anchorXls;

	/**
	 * 图片所在位置坐标（xlsx格式版，XSSFClientAnchor与HSSFClientAnchor只能二选一）
	 */
	private XSSFClientAnchor anchorXlsx;

	private ExcelImageInput() {

	}

	/**
	 * Excel图片参数对象(xlsx版)
	 *
	 * @param imgPath
	 * @param sheetIndex
	 * @param anchorXlsx
	 */
	public ExcelImageInput(String imgPath, Integer sheetIndex, XSSFClientAnchor anchorXlsx) {
		this.imgPath = imgPath;
		this.sheetIndex = sheetIndex;
		this.anchorXlsx = anchorXlsx;
	}

	/**
	 * Excel图片参数对象(xls版)
	 *
	 * @param imgPath
	 * @param sheetIndex
	 * @param anchorXls
	 */
	public ExcelImageInput(String imgPath, Integer sheetIndex, HSSFClientAnchor anchorXls) {
		this.imgPath = imgPath;
		this.sheetIndex = sheetIndex;
		this.anchorXls = anchorXls;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Integer getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(Integer sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public HSSFClientAnchor getAnchorXls() {
		return anchorXls;
	}

	public void setAnchorXls(HSSFClientAnchor anchorXls) {
		this.anchorXls = anchorXls;
	}

	public XSSFClientAnchor getAnchorXlsx() {
		return anchorXlsx;
	}

	public void setAnchorXlsx(XSSFClientAnchor anchorXlsx) {
		this.anchorXlsx = anchorXlsx;
	}
}
