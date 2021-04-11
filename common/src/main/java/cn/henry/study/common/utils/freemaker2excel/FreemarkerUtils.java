package cn.henry.study.common.utils.freemaker2excel;

import cn.henry.study.common.utils.freemaker2excel.excel.*;
import cn.henry.study.common.utils.freemaker2excel.input.ExcelImageInput;
import cn.henry.study.common.utils.freemaker2excel.input.FreemarkerInput;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Table;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author 大脑补丁
 * @project freemarker-excel
 * @description: freemarker工具类
 * @create 2020-04-14 09:43
 */
public class FreemarkerUtils {

	private static final Logger log = LoggerFactory.getLogger(FreemarkerUtils.class);

	/**
	 * 导出Excel到指定文件中
	 *
	 * @param dataMap          数据源
	 * @param templateName     模板名称（包含文件后缀名.ftl）
	 * @param templateFilePath 模板所在路径（不能为空，当前路径传空字符：""）
	 * @param fileFullPath     文件完整路径（如：usr/local/fileName.xls）
	 * @author 大脑补丁 on 2020-04-05 11:51
	 */
	@SuppressWarnings("rawtypes")
	public static void exportToFile(Map dataMap, String templateName, String templateFilePath, String fileFullPath) {
		try {
			File file = new File(fileFullPath);
			FileUtils.forceMkdirParent(file);
			FileOutputStream outputStream = new FileOutputStream(file);
			exportToStream(dataMap, templateName, templateFilePath, outputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出Excel到输出流
	 *
	 * @param dataMap          数据源
	 * @param templateName     模板名称（包含文件后缀名.ftl）
	 * @param templateFilePath 模板所在路径（不能为空，当前路径传空字符：""）
	 * @param outputStream     输出流
	 * @author 大脑补丁 on 2020-04-05 11:52
	 */
	@SuppressWarnings("rawtypes")
	public static void exportToStream(Map dataMap, String templateName, String templateFilePath,
			FileOutputStream outputStream) {
		try {
			Template template = getTemplate(templateName, templateFilePath);
			OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream, "UTF-8");
			Writer writer = new BufferedWriter(outputWriter);
			template.process(dataMap, writer);
			writer.flush();
			writer.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 导出到文件中（导出到硬盘，xls格式）
	 *
	 * @param excelFilePath
	 * @param freemakerEntity
	 * @author 大脑补丁 on 2020-04-14 15:34
	 */
	public static void exportImageExcel(String excelFilePath, FreemarkerInput freemakerEntity) {
		try {
			File file = new File(excelFilePath);
			FileUtils.forceMkdirParent(file);
			FileOutputStream outputStream = new FileOutputStream(file);
			createImageExcleToStream(freemakerEntity, outputStream);
			// 删除xml缓存文件
			FileUtils.forceDelete(new File(freemakerEntity.getXmlTempFile() + freemakerEntity.getFileName() + ".xml"));
			log.info("导出成功,导出到目录：" + file.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导出到文件中（导出到硬盘，xlsx格式）
	 *
	 * @param excelFilePath
	 * @param freemakerEntity
	 * @author 大脑补丁 on 2020-04-14 15:34
	 */
	public static void exportImageExcelNew(String excelFilePath, FreemarkerInput freemakerEntity) {
		try {
			File file = new File(excelFilePath);
			FileUtils.forceMkdirParent(file);
			FileOutputStream outputStream = new FileOutputStream(file);
			createExcelToStream(freemakerEntity, outputStream);
			// 删除xml缓存文件
			FileUtils.forceDelete(new File(freemakerEntity.getXmlTempFile() + freemakerEntity.getFileName() + ".xml"));
			log.info("导出成功,导出到目录：" + file.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取项目templates文件夹下的模板
	private static Template getTemplate(String templateName, String filePath) throws IOException {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
		configuration.setDefaultEncoding("UTF-8");
		configuration.setTemplateUpdateDelayMilliseconds(0);
		configuration.setEncoding(Locale.CHINA, "UTF-8");
		configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates" + filePath);
		configuration.setOutputEncoding("UTF-8");
		return configuration.getTemplate(templateName, "UTF-8");
	}

	/**
	 * 导出Excel到输出流（支持Excel2003版，xls格式）
	 *
	 * @param freemakerEntity
	 * @param outputStream
	 */
	private static void createImageExcleToStream(FreemarkerInput freemakerEntity, OutputStream outputStream) {
		Writer out = null;
		try {
			// 创建xml文件
			Template template = getTemplate(freemakerEntity.getTemplateName(), freemakerEntity.getTemplateFilePath());
			File tempXMLFile = new File(freemakerEntity.getXmlTempFile() + freemakerEntity.getFileName() + ".xml");
			FileUtils.forceMkdirParent(tempXMLFile);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempXMLFile), "UTF-8"));
			template.process(freemakerEntity.getDataMap(), out);
			if (log.isDebugEnabled()) {
				log.debug("1.完成将文本数据导入到XML文件中");
			}
			SAXReader reader = new SAXReader();
			Document document = reader.read(tempXMLFile);
			Map<String, Style> styleMap = readXmlStyle(document);
			log.debug("2.完成解析XML中样式信息");
			List<Worksheet> worksheets = readXmlWorksheet(document);
			if (log.isDebugEnabled()) {
				log.debug("3.开始将XML信息写入Excel，数据为：" + worksheets.toString());
			}
			HSSFWorkbook wb = new HSSFWorkbook();
			for (Worksheet worksheet : worksheets) {
				HSSFSheet sheet = wb.createSheet(worksheet.getName());
				cn.henry.study.common.utils.freemaker2excel.excel.Table table = worksheet.getTable();
				List<cn.henry.study.common.utils.freemaker2excel.excel.Row> rows = table.getRows();
				List<Column> columns = table.getColumns();
				// 填充列宽
				int columnIndex = 0;
				for (int i = 0; i < columns.size(); i++) {
					Column column = columns.get(i);
					columnIndex = getCellWidthIndex(columnIndex, i, column.getIndex());
					sheet.setColumnWidth(columnIndex, (int) column.getWidth() * 50);
				}
				int createRowIndex = 0;
				List<CellRangeAddressEntity> cellRangeAddresses = new ArrayList<>();
				for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
					cn.henry.study.common.utils.freemaker2excel.excel.Row rowInfo = rows.get(rowIndex);
					if (rowInfo == null) {
						continue;
					}
					createRowIndex = getIndex(createRowIndex, rowIndex, rowInfo.getIndex());
					HSSFRow row = sheet.createRow(createRowIndex);
					if (rowInfo.getHeight() != null) {
						Integer height = rowInfo.getHeight() * 20;
						row.setHeight(height.shortValue());
					}
					List<cn.henry.study.common.utils.freemaker2excel.excel.Cell> cells = rowInfo.getCells();
					if (CollectionUtils.isEmpty(cells)) {
						continue;
					}
					int startIndex = 0;
					for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
						cn.henry.study.common.utils.freemaker2excel.excel.Cell cellInfo = cells.get(cellIndex);
						if (cellInfo == null) {
							continue;
						}
						// 获取起始列
						startIndex = getIndex(startIndex, cellIndex, cellInfo.getIndex());
						HSSFCell cell = row.createCell(startIndex);
						String styleID = cellInfo.getStyleID();
						cn.henry.study.common.utils.freemaker2excel.excel.Style style = styleMap.get(styleID);
						/*设置数据单元格格式*/
						CellStyle dataStyle = wb.createCellStyle();
						// 设置边框样式
						setBorder(style, dataStyle);
						// 设置对齐方式
						setAlignment(style, dataStyle);
						// 填充文本
						setValue(wb, cellInfo, cell, style, dataStyle);
						// 填充颜色
						setCellColor(style, dataStyle);
						cell.setCellStyle(dataStyle);
						//单元格注释
						if (cellInfo.getComment() != null) {
							Data data = cellInfo.getComment().getData();
							Comment comment = sheet.createDrawingPatriarch()
									.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
							comment.setString(new HSSFRichTextString(data.getText()));
							cell.setCellComment(comment);
						}
						// 合并单元格
						startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, cellInfo, style);
					}
				}
				// 添加合并单元格
				addCellRange(sheet, cellRangeAddresses);
			}
			// 加载图片到excel
			log.debug("4.开始写入图片：" + freemakerEntity.getExcelImageInputs());
			if (!CollectionUtils.isEmpty(freemakerEntity.getExcelImageInputs())) {
				writeImageToExcel(freemakerEntity.getExcelImageInputs(), wb);
			}
			log.debug("5.完成写入图片：" + freemakerEntity.getExcelImageInputs());
			// 写入excel文件,response字符流转换成字节流，template需要字节流作为输出
			wb.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出excel异常：" + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 导出Excel到输出流（支持Excel2007版，xlsx格式）
	 *
	 * @param freemakerEntity
	 * @param outputStream
	 */
	private static void createExcelToStream(FreemarkerInput freemakerEntity, OutputStream outputStream) {
		Writer out = null;
		try {
			// 创建xml文件
			Template template = getTemplate(freemakerEntity.getTemplateName(), freemakerEntity.getTemplateFilePath());
			File tempXMLFile = new File(freemakerEntity.getXmlTempFile() + freemakerEntity.getFileName() + ".xml");
			FileUtils.forceMkdirParent(tempXMLFile);
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempXMLFile), "UTF-8"));
			template.process(freemakerEntity.getDataMap(), out);
			if (log.isDebugEnabled()) {
				log.debug("1.完成将文本数据导入到XML文件中");
			}
			SAXReader reader = new SAXReader();
			Document document = reader.read(tempXMLFile);
			Map<String, Style> styleMap = readXmlStyle(document);
			log.debug("2.完成解析XML中样式信息");
			List<Worksheet> worksheets = readXmlWorksheet(document);
			if (log.isDebugEnabled()) {
				log.debug("3.开始将XML信息写入Excel，数据为：" + worksheets.toString());
			}
			XSSFWorkbook wb = new XSSFWorkbook();
			for (Worksheet worksheet : worksheets) {
				XSSFSheet sheet = wb.createSheet(worksheet.getName());
				cn.henry.study.common.utils.freemaker2excel.excel.Table table = worksheet.getTable();
				List<cn.henry.study.common.utils.freemaker2excel.excel.Row> rows = table.getRows();
				List<Column> columns = table.getColumns();
				// 填充列宽
				int columnIndex = 0;
				for (int i = 0; i < columns.size(); i++) {
					Column column = columns.get(i);
					columnIndex = getCellWidthIndex(columnIndex, i, column.getIndex());
					sheet.setColumnWidth(columnIndex, (int) column.getWidth() * 50);
				}
				int createRowIndex = 0;
				List<CellRangeAddressEntity> cellRangeAddresses = new ArrayList<>();
				for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
					cn.henry.study.common.utils.freemaker2excel.excel.Row rowInfo = rows.get(rowIndex);
					if (rowInfo == null) {
						continue;
					}
					createRowIndex = getIndex(createRowIndex, rowIndex, rowInfo.getIndex());
					XSSFRow row = sheet.createRow(createRowIndex);
					if (rowInfo.getHeight() != null) {
						Integer height = rowInfo.getHeight() * 20;
						row.setHeight(height.shortValue());
					}
					List<cn.henry.study.common.utils.freemaker2excel.excel.Cell> cells = rowInfo.getCells();
					if (CollectionUtils.isEmpty(cells)) {
						continue;
					}
					int startIndex = 0;
					for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++) {
						cn.henry.study.common.utils.freemaker2excel.excel.Cell cellInfo = cells.get(cellIndex);
						if (cellInfo == null) {
							continue;
						}
						// 获取起始列
						startIndex = getIndex(startIndex, cellIndex, cellInfo.getIndex());
						XSSFCell cell = row.createCell(startIndex);
						String styleID = cellInfo.getStyleID();
						Style style = styleMap.get(styleID);
						/*设置数据单元格格式*/
						CellStyle dataStyle = wb.createCellStyle();
						// 设置边框样式
						setBorder(style, dataStyle);
						// 设置对齐方式
						setAlignment(style, dataStyle);
						// 填充文本
						setValue(wb, cellInfo, cell, style, dataStyle);
						// 填充颜色
						setCellColor(style, dataStyle);
						cell.setCellStyle(dataStyle);
						//单元格注释
						if (cellInfo.getComment() != null) {
							Data data = cellInfo.getComment().getData();
							Comment comment = sheet.createDrawingPatriarch()
									.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
							comment.setString(new XSSFRichTextString(data.getText()));
							cell.setCellComment(comment);
						}
						// 合并单元格
						startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, cellInfo, style);
					}
				}
				// 添加合并单元格
				addCellRange(sheet, cellRangeAddresses);
			}
			// 加载图片到excel
			log.debug("4.开始写入图片：" + freemakerEntity.getExcelImageInputs());
			if (!CollectionUtils.isEmpty(freemakerEntity.getExcelImageInputs())) {
				writeImageToExcel(freemakerEntity.getExcelImageInputs(), wb);
			}
			log.debug("5.完成写入图片：" + freemakerEntity.getExcelImageInputs());
			// 写入excel文件,response字符流转换成字节流，template需要字节流作为输出
			wb.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出excel异常：" + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

	public static Map<String, Style> readXmlStyle(Document document) {
		Map<String, Style> styleMap = XmlReader.getStyle(document);
		return styleMap;
	}

	public static List<Worksheet> readXmlWorksheet(Document document) {
		List<Worksheet> worksheets = XmlReader.getWorksheet(document);
		return worksheets;
	}

	private static int getIndex(int columnIndex, int i, Integer index) {
		if (index != null) {
			columnIndex = index - 1;
		}
		if (index == null && columnIndex != 0) {
			columnIndex = columnIndex + 1;
		}
		if (index == null && columnIndex == 0) {
			columnIndex = i;
		}
		return columnIndex;
	}

	private static int getCellWidthIndex(int columnIndex, int i, Integer index) {
		if (index != null) {
			columnIndex = index;
		}
		if (index == null && columnIndex != 0) {
			columnIndex = columnIndex + 1;
		}
		if (index == null && columnIndex == 0) {
			columnIndex = i;
		}
		return columnIndex;
	}

	/**
	 * 设置边框
	 *
	 * @param style:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setBorder(Style style, CellStyle dataStyle) {
		if (style != null && style.getBorders() != null) {
			for (int k = 0; k < style.getBorders().size(); k++) {
				Style.Border border = style.getBorders().get(k);
				if (border != null) {
					if ("Bottom".equals(border.getPosition())) {
						dataStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
						dataStyle.setBorderBottom(BorderStyle.THIN);
					}
					if ("Left".equals(border.getPosition())) {
						dataStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
						dataStyle.setBorderLeft(BorderStyle.THIN);
					}
					if ("Right".equals(border.getPosition())) {
						dataStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
						dataStyle.setBorderRight(BorderStyle.THIN);
					}
					if ("Top".equals(border.getPosition())) {
						dataStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
						dataStyle.setBorderTop(BorderStyle.THIN);
					}
				}

			}
		}
	}

	/**
	 * 将图片写入Excel(XLS版)
	 *
	 * @param excelImageInputs
	 * @param wb
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static void writeImageToExcel(List<ExcelImageInput> excelImageInputs, HSSFWorkbook wb) throws IOException {
		BufferedImage bufferImg = null;
		if (!CollectionUtils.isEmpty(excelImageInputs)) {
			for (ExcelImageInput excelImageInput : excelImageInputs) {
				Sheet sheet = wb.getSheetAt(excelImageInput.getSheetIndex());
				if (sheet == null) {
					continue;
				}
				// 画图的顶级管理器，一个sheet只能获取一个
				Drawing patriarch = sheet.createDrawingPatriarch();
				// anchor存储图片的属性，包括在Excel中的位置、大小等信息
				HSSFClientAnchor anchor = excelImageInput.getAnchorXls();
				anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
				// 插入图片
				String imagePath = excelImageInput.getImgPath();
				// 将图片写入到byteArray中
				ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
				bufferImg = ImageIO.read(new File(imagePath));
				// 图片扩展名
				String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
				ImageIO.write(bufferImg, imageType, byteArrayOut);
				// 通过poi将图片写入到Excel中
				patriarch.createPicture(anchor,
						wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
			}
		}
	}

	/**
	 * 将图片写入Excel(XLSX版)
	 *
	 * @param excelImageInputs
	 * @param wb
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static void writeImageToExcel(List<ExcelImageInput> excelImageInputs, XSSFWorkbook wb) throws IOException {
		BufferedImage bufferImg = null;
		if (!CollectionUtils.isEmpty(excelImageInputs)) {
			for (ExcelImageInput excelImageInput : excelImageInputs) {
				Sheet sheet = wb.getSheetAt(excelImageInput.getSheetIndex());
				if (sheet == null) {
					continue;
				}
				// 画图的顶级管理器，一个sheet只能获取一个
				Drawing patriarch = sheet.createDrawingPatriarch();
				// anchor存储图片的属性，包括在Excel中的位置、大小等信息
				XSSFClientAnchor anchor = excelImageInput.getAnchorXlsx();
				anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
				// 插入图片
				String imagePath = excelImageInput.getImgPath();
				// 将图片写入到byteArray中
				ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
				bufferImg = ImageIO.read(new File(imagePath));
				// 图片扩展名
				String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
				ImageIO.write(bufferImg, imageType, byteArrayOut);
				// 通过poi将图片写入到Excel中
				patriarch.createPicture(anchor,
						wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
			}
		}
	}

	/**
	 * 添加合并单元格（XLS格式）
	 *
	 * @param sheet:
	 * @param cellRangeAddresses:
	 * @return void
	 */
	private static void addCellRange(HSSFSheet sheet, List<CellRangeAddressEntity> cellRangeAddresses) {
		if (!CollectionUtils.isEmpty(cellRangeAddresses)) {
			for (CellRangeAddressEntity cellRangeAddressEntity : cellRangeAddresses) {
				CellRangeAddress cellRangeAddress = cellRangeAddressEntity.getCellRangeAddress();
				sheet.addMergedRegion(cellRangeAddress);
				if (CollectionUtils.isEmpty(cellRangeAddressEntity.getBorders())) {
					continue;
				}
				for (int k = 0; k < cellRangeAddressEntity.getBorders().size(); k++) {
					Style.Border border = cellRangeAddressEntity.getBorders().get(k);
					if (border == null) {
						continue;
					}
					if ("Bottom".equals(border.getPosition())) {
						RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Left".equals(border.getPosition())) {
						RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Right".equals(border.getPosition())) {
						RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Top".equals(border.getPosition())) {
						RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
					}
				}
			}
		}
	}

	/**
	 * 添加合并单元格（XLSX格式）
	 *
	 * @param sheet:
	 * @param cellRangeAddresses:
	 * @return void
	 */
	private static void addCellRange(XSSFSheet sheet, List<CellRangeAddressEntity> cellRangeAddresses) {
		if (!CollectionUtils.isEmpty(cellRangeAddresses)) {
			for (CellRangeAddressEntity cellRangeAddressEntity : cellRangeAddresses) {
				CellRangeAddress cellRangeAddress = cellRangeAddressEntity.getCellRangeAddress();
				sheet.addMergedRegion(cellRangeAddress);
				if (CollectionUtils.isEmpty(cellRangeAddressEntity.getBorders())) {
					continue;
				}
				for (int k = 0; k < cellRangeAddressEntity.getBorders().size(); k++) {
					Style.Border border = cellRangeAddressEntity.getBorders().get(k);
					if (border == null) {
						continue;
					}
					if ("Bottom".equals(border.getPosition())) {
						RegionUtil.setBorderBottom(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Left".equals(border.getPosition())) {
						RegionUtil.setBorderLeft(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Right".equals(border.getPosition())) {
						RegionUtil.setBorderRight(BorderStyle.THIN, cellRangeAddress, sheet);
					}
					if ("Top".equals(border.getPosition())) {
						RegionUtil.setBorderTop(BorderStyle.THIN, cellRangeAddress, sheet);
					}
				}
			}
		}
	}

	/**
	 * 设置对齐方式
	 *
	 * @param style:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setAlignment(Style style, CellStyle dataStyle) {
		if (style != null && style.getAlignment() != null) {
			// 设置水平对齐方式
			String horizontal = style.getAlignment().getHorizontal();
			if (!ObjectUtils.isEmpty(horizontal)) {
				if ("Left".equals(horizontal)) {
					dataStyle.setAlignment(HorizontalAlignment.LEFT);
				} else if ("Center".equals(horizontal)) {
					dataStyle.setAlignment(HorizontalAlignment.CENTER);
				} else {
					dataStyle.setAlignment(HorizontalAlignment.RIGHT);
				}
			}

			// 设置垂直对齐方式
			String vertical = style.getAlignment().getVertical();
			if (!ObjectUtils.isEmpty(vertical)) {
				if ("Top".equals(vertical)) {
					dataStyle.setVerticalAlignment(VerticalAlignment.TOP);
				} else if ("Center".equals(vertical)) {
					dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				} else if ("Bottom".equals(vertical)) {
					dataStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
				} else if ("JUSTIFY".equals(vertical)) {
					dataStyle.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				} else {
					dataStyle.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
				}
			}
			// 设置换行
			String wrapText = style.getAlignment().getWrapText();
			if (!ObjectUtils.isEmpty(wrapText)) {
				dataStyle.setWrapText(true);
			}
		}
	}

	/**
	 * 设置单元格背景填充色
	 *
	 * @param style:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setCellColor(Style style, CellStyle dataStyle) {
		if (style != null && style.getInterior() != null) {
			if ("#FF0000".equals(style.getInterior().getColor())) {
				// 填充单元格
				dataStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
				dataStyle.setFillBackgroundColor(IndexedColors.RED.getIndex());
			} else if ("#92D050".equals(style.getInterior().getColor())) {
				// 填充单元格
				dataStyle.setFillForegroundColor(IndexedColors.LIME.getIndex());
			}
			if ("Solid".equals(style.getInterior().getPattern())) {
				dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
		}
	}

	/**
	 * 构造合并单元格集合
	 *
	 * @param createRowIndex:
	 * @param cellRangeAddresses:
	 * @param startIndex:
	 * @param cellInfo:
	 * @param style:
	 * @return int
	 */
	private static int getCellRanges(int createRowIndex, List<CellRangeAddressEntity> cellRangeAddresses,
			int startIndex, cn.henry.study.common.utils.freemaker2excel.excel.Cell cellInfo, Style style) {
		if (cellInfo.getMergeAcross() != null || cellInfo.getMergeDown() != null) {
			CellRangeAddress cellRangeAddress = null;
			if (cellInfo.getMergeAcross() != null && cellInfo.getMergeDown() != null) {
				int mergeAcross = startIndex;
				if (cellInfo.getMergeAcross() != 0) {
					// 获取该单元格结束列数
					mergeAcross += cellInfo.getMergeAcross();
				}
				int mergeDown = createRowIndex;
				if (cellInfo.getMergeDown() != 0) {
					// 获取该单元格结束列数
					mergeDown += cellInfo.getMergeDown();
				}
				cellRangeAddress = new CellRangeAddress(createRowIndex, mergeDown, (short) startIndex,
						(short) mergeAcross);
			} else if (cellInfo.getMergeAcross() != null && cellInfo.getMergeDown() == null) {
				int mergeAcross = startIndex;
				if (cellInfo.getMergeAcross() != 0) {
					// 获取该单元格结束列数
					mergeAcross += cellInfo.getMergeAcross();
					// 合并单元格
					cellRangeAddress = new CellRangeAddress(createRowIndex, createRowIndex, (short) startIndex,
							(short) mergeAcross);
				}

			} else if (cellInfo.getMergeDown() != null && cellInfo.getMergeAcross() == null) {
				int mergeDown = createRowIndex;
				if (cellInfo.getMergeDown() != 0) {
					// 获取该单元格结束列数
					mergeDown += cellInfo.getMergeDown();
					// 合并单元格
					cellRangeAddress = new CellRangeAddress(createRowIndex, mergeDown, (short) startIndex,
							(short) startIndex);
				}
			}

			if (cellInfo.getMergeAcross() != null) {
				int length = cellInfo.getMergeAcross().intValue();
				for (int i = 0; i < length; i++) {
					startIndex += cellInfo.getMergeAcross();
				}
			}
			CellRangeAddressEntity cellRangeAddressEntity = new CellRangeAddressEntity();
			cellRangeAddressEntity.setCellRangeAddress(cellRangeAddress);
			if (style != null && style.getBorders() != null) {
				cellRangeAddressEntity.setBorders(style.getBorders());
			}
			cellRangeAddresses.add(cellRangeAddressEntity);
		}
		return startIndex;
	}

	/**
	 * 设置文本值内容（XLSX格式）
	 *
	 * @param wb:
	 * @param cellInfo:
	 * @param cell:
	 * @param style:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setValue(XSSFWorkbook wb, cn.henry.study.common.utils.freemaker2excel.excel.Cell cellInfo,
								 XSSFCell cell, cn.henry.study.common.utils.freemaker2excel.excel.Style style,
								 CellStyle dataStyle) {
		if (cellInfo.getData() != null) {
			XSSFFont font = wb.createFont();
			if (style != null && style.getFont() != null) {
				String color = style.getFont().getColor();
				if ("#FF0000".equals(color)) {
					font.setColor(IndexedColors.RED.getIndex());
				} else if ("#000000".equals(color)) {
					font.setColor(IndexedColors.BLACK.getIndex());
				}
			}
			if (!ObjectUtils.isEmpty(cellInfo.getData().getType()) && "Number".equals(cellInfo.getData().getType())) {
				cell.setCellType(CellType.NUMERIC);
			}
			if (style != null && style.getFont().getBold() > 0) {
				font.setBold(true);
			}
			if (style != null && !ObjectUtils.isEmpty(style.getFont().getFontName())) {
				font.setFontName(style.getFont().getFontName());
			}
			if (style != null && style.getFont().getSize() > 0) {
				// 设置字体大小道
				font.setFontHeightInPoints((short) style.getFont().getSize());
			}

			if (cellInfo.getData().getFont() != null) {
				if (cellInfo.getData().getFont().getBold() > 0) {
					font.setBold(true);
				}
				if ("Number".equals(cellInfo.getData().getType())) {
					cell.setCellValue(Float.parseFloat(cellInfo.getData().getFont().getText()));
				} else {
					cell.setCellValue(cellInfo.getData().getFont().getText());
				}
				if (!ObjectUtils.isEmpty(cellInfo.getData().getFont().getCharSet())) {
					font.setCharSet(Integer.valueOf(cellInfo.getData().getFont().getCharSet()));
				}
			} else {
				if ("Number".equals(cellInfo.getData().getType())) {
					if (!ObjectUtils.isEmpty(cellInfo.getData().getText())) {
						// cell.setCellValue(Float.parseFloat(cellInfo.getData().getText()));
						cell.setCellValue(Float.parseFloat(cellInfo.getData().getText().replaceAll(",", "")));
					}
				} else {
					cell.setCellValue(cellInfo.getData().getText());

				}
			}

			if (style != null) {
				if (style.getNumberFormat() != null) {
						String color = style.getFont().getColor();
						if ("#FF0000".equals(color)) {
							font.setColor(IndexedColors.RED.getIndex());
						} else if ("#000000".equals(color)) {
							font.setColor(IndexedColors.BLACK.getIndex());
						}
						if ("0%".equals(style.getNumberFormat().getFormat())) {
							XSSFDataFormat format = wb.createDataFormat();
							dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
						} else {
							dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
						}
					// XSSFDataFormat format = wb.createDataFormat();
					// dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
				}
			}
			dataStyle.setFont(font);
		}
	}

	/**
	 * 设置文本值内容（XLS格式）
	 *
	 * @param wb:
	 * @param cellInfo:
	 * @param cell:
	 * @param style:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setValue(HSSFWorkbook wb, cn.henry.study.common.utils.freemaker2excel.excel.Cell cellInfo,
								 HSSFCell cell,
								 cn.henry.study.common.utils.freemaker2excel.excel.Style style, CellStyle dataStyle) {
		if (cellInfo.getData() != null) {
			HSSFFont font = wb.createFont();
			if (style != null && style.getFont() != null) {
				String color = style.getFont().getColor();
				if ("#FF0000".equals(color)) {
					font.setColor(IndexedColors.RED.getIndex());
				} else if ("#000000".equals(color)) {
					font.setColor(IndexedColors.BLACK.getIndex());
				}
			}
			if (!ObjectUtils.isEmpty(cellInfo.getData().getType()) && "Number".equals(cellInfo.getData().getType())) {
				cell.setCellType(CellType.NUMERIC);
			}
			if (style != null && style.getFont().getBold() > 0) {
				font.setBold(true);
			}
			if (style != null && !ObjectUtils.isEmpty(style.getFont().getFontName())) {
				font.setFontName(style.getFont().getFontName());
			}
			if (style != null && style.getFont().getSize() > 0) {
				// 设置字体大小道
				font.setFontHeightInPoints((short) style.getFont().getSize());
			}

			if (cellInfo.getData().getFont() != null) {
				if (cellInfo.getData().getFont().getBold() > 0) {
					font.setBold(true);
				}
				if ("Number".equals(cellInfo.getData().getType())) {
					cell.setCellValue(Float.parseFloat(cellInfo.getData().getFont().getText()));
				} else {
					cell.setCellValue(cellInfo.getData().getFont().getText());
				}
				if (!ObjectUtils.isEmpty(cellInfo.getData().getFont().getCharSet())) {
					font.setCharSet(Integer.valueOf(cellInfo.getData().getFont().getCharSet()));
				}
			} else {
				if ("Number".equals(cellInfo.getData().getType())) {
					if (!ObjectUtils.isEmpty(cellInfo.getData().getText())) {
						// cell.setCellValue(Float.parseFloat(cellInfo.getData().getText()));
						cell.setCellValue(Float.parseFloat(cellInfo.getData().getText().replaceAll(",", "")));
					}
				} else {
					cell.setCellValue(cellInfo.getData().getText());

				}
			}

			if (style != null) {
				if (style.getNumberFormat() != null) {
					String color = style.getFont().getColor();
					if ("#FF0000".equals(color)) {
						font.setColor(IndexedColors.RED.getIndex());
					} else if ("#000000".equals(color)) {
						font.setColor(IndexedColors.BLACK.getIndex());
					}
					if ("0%".equals(style.getNumberFormat().getFormat())) {
						HSSFDataFormat format = wb.createDataFormat();
						dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
					} else {
						dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
					}
					// HSSFDataFormat format = wb.createDataFormat();
					// dataStyle.setDataFormat(format.getFormat(style.getNumberFormat().getFormat()));
				}
			}
			dataStyle.setFont(font);
		}
	}

	/**
	 * Windows平台下，通过office文件另存为的方式，将xml直接转化为xsl格式文件（需配合对应的dll包）。linux无法使用，因为无对应so库
	 *
	 * @param xmlfile
	 * @param xlsxfile
	 */
	public static void xml2xlsx(String xmlfile, String xlsxfile) {
		ActiveXComponent app = new ActiveXComponent("Excel.Application");
		try {
			app.setProperty("Visible", new Variant(false));
			Dispatch excels = app.getProperty("Workbooks").toDispatch();
			Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method,
					new Object[] { xmlfile, new Variant(false), new Variant(true) }, new int[1]).toDispatch();

			Dispatch.invoke(excel, "SaveAs", Dispatch.Method, new Object[] { xlsxfile, new Variant(51) }, new int[1]);
			Variant f = new Variant(false);
			Dispatch.call(excel, "Close", f);
		} catch (Exception e) {
			log.info("转为XLSX出错", e);
			e.printStackTrace();
		} finally {
			app.invoke("Quit", new Variant[] {});
		}
	}

}
