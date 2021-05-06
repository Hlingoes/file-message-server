package cn.henry.study.common.utils.freemaker2excel;

import cn.henry.study.common.utils.freemaker2excel.excel.*;
import cn.henry.study.common.utils.freemaker2excel.input.ExcelImageInput;
import cn.henry.study.common.utils.freemaker2excel.input.FreemarkerInput;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Comment;
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
	public static void writeExcelFile(Map dataMap, String templateName, String templateFilePath, String fileFullPath) {
		try {
			File file = new File(fileFullPath);
			FileUtils.forceMkdirParent(file);
			FileOutputStream outputStream = new FileOutputStream(file);
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
	 * @param entity
	 * @author 大脑补丁 on 2020-04-14 15:34
	 */
	public static void createImagedExcel(String excelFilePath, FreemarkerInput entity) {
		try {
			File file = new File(excelFilePath);
			FileUtils.forceMkdirParent(file);
			FileOutputStream outputStream = new FileOutputStream(file);
			// 创建xml文件
			File tempXml = createTempXml(entity);
			SAXReader reader = new SAXReader();
			Document document = reader.read(tempXml);
			Map<String, ExcelStyle> styleMap = readXmlStyle(document);
			List<ExcelWorksheet> excelWorksheets = readXmlWorksheet(document);
			HSSFWorkbook wb = new HSSFWorkbook();
			for (ExcelWorksheet excelWorksheet : excelWorksheets) {
				HSSFSheet sheet = wb.createSheet(excelWorksheet.getName());
				ExcelTable excelTable = excelWorksheet.getExcelTable();
				List<ExcelRow> excelRows = excelTable.getExcelRows();
				List<ExcelColumn> excelColumns = excelTable.getExcelColumns();
				// 填充列宽
				fillColumnWidth(sheet, excelColumns);
				List<ExcelCellRangeAddressEntity> cellRangeAddresses = getExcelCellRangeAddressEntities(styleMap, wb, sheet, excelRows);
				// 添加合并单元格
				addCellRange(sheet, cellRangeAddresses);
			}
			// 加载图片到excel
			log.debug("4.开始写入图片：" + entity.getExcelImageInputs());
			if (!CollectionUtils.isEmpty(entity.getExcelImageInputs())) {
				drawImageInExcel(wb, entity.getExcelImageInputs());
			}
			log.debug("5.完成写入图片：" + entity.getExcelImageInputs());
			// 写入excel文件,response字符流转换成字节流，template需要字节流作为输出
			wb.write(outputStream);
			outputStream.close();
			log.info("导出成功,导出到目录：" + file.getCanonicalPath());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 删除xml缓存文件
			try {
				FileUtils.forceDelete(new File(entity.getXmlTempFile() + entity.getFileName() + ".xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
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

    private static List<ExcelCellRangeAddressEntity> getExcelCellRangeAddressEntities(Map<String, ExcelStyle> styleMap, HSSFWorkbook wb, HSSFSheet sheet, List<ExcelRow> excelRows) {
        List<ExcelCellRangeAddressEntity> cellRangeAddresses = new ArrayList<>();
        int createRowIndex = 0;
        for (int rowIndex = 0; rowIndex < excelRows.size(); rowIndex++) {
            ExcelRow excelRowInfo = excelRows.get(rowIndex);
            if (excelRowInfo == null) {
                continue;
            }
            createRowIndex = getIndex(createRowIndex, rowIndex, excelRowInfo.getIndex());
            HSSFRow row = sheet.createRow(createRowIndex);
            if (excelRowInfo.getHeight() != null) {
                Integer height = excelRowInfo.getHeight() * 20;
                row.setHeight(height.shortValue());
            }
            List<ExcelCell> excelCells = excelRowInfo.getExcelCells();
            if (CollectionUtils.isEmpty(excelCells)) {
                continue;
            }
            int startIndex = 0;
            for (int cellIndex = 0; cellIndex < excelCells.size(); cellIndex++) {
                ExcelCell excelCellInfo = excelCells.get(cellIndex);
                if (excelCellInfo == null) {
                    continue;
                }
                // 获取起始列
                startIndex = getIndex(startIndex, cellIndex, excelCellInfo.getIndex());
                HSSFCell cell = row.createCell(startIndex);
                String styleID = excelCellInfo.getStyleID();
                ExcelStyle excelStyle = styleMap.get(styleID);
                /*设置数据单元格格式*/
                CellStyle dataStyle = wb.createCellStyle();
                // 设置边框样式
                setBorder(excelStyle, dataStyle);
                // 设置对齐方式
                setAlignment(excelStyle, dataStyle);
                // 填充文本
                setValue(wb, excelCellInfo, cell, excelStyle, dataStyle);
                // 填充颜色
                setCellColor(wb, excelStyle, dataStyle);
                cell.setCellStyle(dataStyle);
                //单元格注释
                if (excelCellInfo.getExcelComment() != null) {
                    ExcelData excelData = excelCellInfo.getExcelComment().getExcelData();
                    Comment comment = sheet.createDrawingPatriarch()
                            .createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
                    comment.setString(new HSSFRichTextString(excelData.getText()));
                    cell.setCellComment(comment);
                }
                // 合并单元格
                startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, excelCellInfo, excelStyle);
            }
        }
        return cellRangeAddresses;
    }

    /**
	 * description: 填充列宽
	 *
	 * @param sheet
	 * @param excelColumns
	 * @return void
	 * @author Hlingoes 2021/4/27
	 */
    private static void fillColumnWidth(Sheet sheet, List<ExcelColumn> excelColumns) {
        int columnIndex = 0;
        for (int i = 0; i < excelColumns.size(); i++) {
            ExcelColumn excelColumn = excelColumns.get(i);
            columnIndex = getCellWidthIndex(columnIndex, i, excelColumn.getIndex());
            sheet.setColumnWidth(columnIndex, (int) excelColumn.getWidth() * 50);
        }
    }

    /**
	 * 导出Excel到输出流（支持Excel2007版，xlsx格式）
	 *
	 * @param entity
	 * @param outputStream
	 */
	private static void createExcel(FreemarkerInput entity, OutputStream outputStream) {
		try {
            File tempXml = createTempXml(entity);
            SAXReader reader = new SAXReader();
			Document document = reader.read(tempXml);
			Map<String, ExcelStyle> styleMap = readXmlStyle(document);
			List<ExcelWorksheet> excelWorksheets = readXmlWorksheet(document);
			XSSFWorkbook wb = new XSSFWorkbook();
			for (ExcelWorksheet excelWorksheet : excelWorksheets) {
				XSSFSheet sheet = wb.createSheet(excelWorksheet.getName());
				ExcelTable excelTable = excelWorksheet.getExcelTable();
				List<ExcelRow> excelRows = excelTable.getExcelRows();
				List<ExcelColumn> excelColumns = excelTable.getExcelColumns();
				// 填充列宽
                fillColumnWidth(sheet, excelColumns);
				int createRowIndex = 0;
				List<ExcelCellRangeAddressEntity> cellRangeAddresses = new ArrayList<>();
				for (int rowIndex = 0; rowIndex < excelRows.size(); rowIndex++) {
					ExcelRow excelRowInfo = excelRows.get(rowIndex);
					if (excelRowInfo == null) {
						continue;
					}
					createRowIndex = getIndex(createRowIndex, rowIndex, excelRowInfo.getIndex());
					XSSFRow row = sheet.createRow(createRowIndex);
					if (excelRowInfo.getHeight() != null) {
						Integer height = excelRowInfo.getHeight() * 20;
						row.setHeight(height.shortValue());
					}
					List<ExcelCell> excelCells = excelRowInfo.getExcelCells();
					if (CollectionUtils.isEmpty(excelCells)) {
						continue;
					}
					int startIndex = 0;
					for (int cellIndex = 0; cellIndex < excelCells.size(); cellIndex++) {
						ExcelCell excelCellInfo = excelCells.get(cellIndex);
						if (excelCellInfo == null) {
							continue;
						}
						// 获取起始列
						startIndex = getIndex(startIndex, cellIndex, excelCellInfo.getIndex());
						XSSFCell cell = row.createCell(startIndex);
						String styleID = excelCellInfo.getStyleID();
						ExcelStyle excelStyle = styleMap.get(styleID);
						/*设置数据单元格格式*/
						XSSFCellStyle dataStyle = wb.createCellStyle();
						// 设置边框样式
						setBorder(excelStyle, dataStyle);
						// 设置对齐方式
						setAlignment(excelStyle, dataStyle);
						// 填充文本
						setValue(wb, excelCellInfo, cell, excelStyle, dataStyle);
						// 填充颜色
						setCellColor(wb, excelStyle, dataStyle);
						cell.setCellStyle(dataStyle);
						//单元格注释
						if (excelCellInfo.getExcelComment() != null) {
							ExcelData excelData = excelCellInfo.getExcelComment().getExcelData();
							Comment comment = sheet.createDrawingPatriarch()
									.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, (short) 3, 3, (short) 5, 6));
							comment.setString(new XSSFRichTextString(excelData.getText()));
							cell.setCellComment(comment);
						}
						// 合并单元格
						startIndex = getCellRanges(createRowIndex, cellRangeAddresses, startIndex, excelCellInfo, excelStyle);
					}
				}
				// 添加合并单元格
				addCellRange(sheet, cellRangeAddresses);
			}
			// 加载图片到excel
			log.debug("4.开始写入图片：" + entity.getExcelImageInputs());
			if (!CollectionUtils.isEmpty(entity.getExcelImageInputs())) {
				drawImageInExcel(wb, entity.getExcelImageInputs());
			}
			log.debug("5.完成写入图片：" + entity.getExcelImageInputs());
			// 写入excel文件,response字符流转换成字节流，template需要字节流作为输出
			wb.write(outputStream);
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出excel异常：" + e.getMessage());
		} finally {

		}
	}

    private static File createTempXml(FreemarkerInput entity) throws IOException, TemplateException {
        // 创建xml文件
        Template template = getTemplate(entity.getTemplateName(), entity.getTemplateFilePath());
        File xml = new File(entity.getXmlTempFile() + entity.getFileName() + ".xml");
        FileUtils.forceMkdirParent(xml);
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xml), "UTF-8"));
        template.process(entity.getDataMap(), out);
        return xml;
    }

    public static Map<String, ExcelStyle> readXmlStyle(Document document) {
		Map<String, ExcelStyle> styleMap = ExcelXmlReader.getStyle(document);
		return styleMap;
	}

	public static List<ExcelWorksheet> readXmlWorksheet(Document document) {
		List<ExcelWorksheet> excelWorksheets = ExcelXmlReader.getWorksheet(document);
		return excelWorksheets;
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
	 * @param excelStyle:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setBorder(ExcelStyle excelStyle, CellStyle dataStyle) {
		if (excelStyle != null && excelStyle.getBorders() != null) {
			for (int k = 0; k < excelStyle.getBorders().size(); k++) {
				ExcelStyle.Border border = excelStyle.getBorders().get(k);
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
	 * description: description: 将图片写入Excel(XLSX版)
	 *
	 * @param wb
	 * @param excelImageInputs
	 * @return void
	 * @author Hlingoes 2021/4/27
	 */
    private static void drawImageInExcel(Workbook wb, List<ExcelImageInput> excelImageInputs) throws IOException {
        for (ExcelImageInput excelImageInput : excelImageInputs) {
            drawImageInExcel(wb, excelImageInput);
        }
    }

	/**
	 * description: 将图片写入Excel(XLSX版)
	 *
	 * @param wb
	 * @param excelImageInput
	 * @return void
	 * @author Hlingoes 2021/4/27
	 */
    private static void drawImageInExcel(Workbook wb, ExcelImageInput excelImageInput) throws IOException {
        Sheet sheet = wb.getSheetAt(excelImageInput.getSheetIndex());
        if (sheet != null) {
            // 画图的顶级管理器，一个sheet只能获取一个
            Drawing patriarch = sheet.createDrawingPatriarch();
            // anchor存储图片的属性，包括在Excel中的位置、大小等信息
            XSSFClientAnchor anchor = excelImageInput.getAnchorXlsx();
            anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
            // 插入图片
            String imagePath = excelImageInput.getImgPath();
            // 将图片写入到byteArray中
            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            BufferedImage bufferImg = ImageIO.read(new File(imagePath));
            // 图片扩展名
            String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
            ImageIO.write(bufferImg, imageType, byteArrayOut);
            // 通过poi将图片写入到Excel中
            patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
        }
    }

	/**
	 * description: 添加合并单元格（XLSX格式）
	 *
	 * @param sheet
	 * @param cellRangeAddresses
	 * @return void
	 * @author Hlingoes 2021/4/26
	 */
    private static void addCellRange(Sheet sheet, List<ExcelCellRangeAddressEntity> cellRangeAddresses) {
        if (CollectionUtils.isEmpty(cellRangeAddresses)) {
            return ;
        }
        for (ExcelCellRangeAddressEntity addressEntity : cellRangeAddresses) {
            CellRangeAddress cellRangeAddress = addressEntity.getCellRangeAddress();
            sheet.addMergedRegion(cellRangeAddress);
            if (CollectionUtils.isNotEmpty(addressEntity.getBorders())) {
                for (ExcelStyle.Border border : addressEntity.getBorders()) {
                    if (border != null) {
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
    }

    /**
	 * 设置对齐方式
	 *
	 * @param excelStyle:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setAlignment(ExcelStyle excelStyle, CellStyle dataStyle) {
		if (excelStyle != null && excelStyle.getAlignment() != null) {
			// 设置水平对齐方式
			String horizontal = excelStyle.getAlignment().getHorizontal();
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
			String vertical = excelStyle.getAlignment().getVertical();
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
			String wrapText = excelStyle.getAlignment().getWrapText();
			if (!ObjectUtils.isEmpty(wrapText)) {
				dataStyle.setWrapText(true);
			}
		}
	}

	/**
	 * 设置单元格背景填充色
	 *
	 * @param excelStyle:
	 * @param style:
	 * @return void
	 */
	private static void setCellColor(XSSFWorkbook wb, ExcelStyle excelStyle, XSSFCellStyle style) {
		if (excelStyle != null && excelStyle.getInterior() != null) {
			XSSFColor xssfColor = new XSSFColor(java.awt.Color.decode(excelStyle.getInterior().getColor()));
			style.setFillForegroundColor(xssfColor);
			style.setFillBackgroundColor(xssfColor);
			if ("Solid".equals(excelStyle.getInterior().getPattern())) {
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
		}
	}

	/**
	 * 设置单元格背景填充色
	 *
	 * @param excelStyle:
	 * @param style:
	 * @return void
	 */
	private static void setCellColor(HSSFWorkbook wb, ExcelStyle excelStyle, CellStyle style) {
		if (excelStyle != null && excelStyle.getInterior() != null) {
			HSSFColor hssfColor = getHssfColor(wb, excelStyle.getInterior().getColor());
			style.setFillForegroundColor(hssfColor.getIndex());
			style.setFillBackgroundColor(hssfColor.getIndex());
			if ("Solid".equals(excelStyle.getInterior().getPattern())) {
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			}
		}
	}

	private static HSSFColor getHssfColor(HSSFWorkbook wb, String color) {
		HSSFPalette customPalette = wb.getCustomPalette();
		java.awt.Color jawtColor = java.awt.Color.decode(color);
		return customPalette.addColor((byte) jawtColor.getRed(), (byte) jawtColor.getGreen(), (byte) jawtColor.getBlue());
	}

	/**
	 * 构造合并单元格集合
	 *
	 * @param createRowIndex:
	 * @param cellRangeAddresses:
	 * @param startIndex:
	 * @param excelCellInfo:
	 * @param excelStyle:
	 * @return int
	 */
	private static int getCellRanges(int createRowIndex, List<ExcelCellRangeAddressEntity> cellRangeAddresses,
									 int startIndex, ExcelCell excelCellInfo, ExcelStyle excelStyle) {
		if (excelCellInfo.getMergeAcross() != null || excelCellInfo.getMergeDown() != null) {
			CellRangeAddress cellRangeAddress = null;
			if (excelCellInfo.getMergeAcross() != null && excelCellInfo.getMergeDown() != null) {
				int mergeAcross = startIndex;
				if (excelCellInfo.getMergeAcross() != 0) {
					// 获取该单元格结束列数
					mergeAcross += excelCellInfo.getMergeAcross();
				}
				int mergeDown = createRowIndex;
				if (excelCellInfo.getMergeDown() != 0) {
					// 获取该单元格结束列数
					mergeDown += excelCellInfo.getMergeDown();
				}
				cellRangeAddress = new CellRangeAddress(createRowIndex, mergeDown, (short) startIndex,
						(short) mergeAcross);
			} else if (excelCellInfo.getMergeAcross() != null && excelCellInfo.getMergeDown() == null) {
				int mergeAcross = startIndex;
				if (excelCellInfo.getMergeAcross() != 0) {
					// 获取该单元格结束列数
					mergeAcross += excelCellInfo.getMergeAcross();
					// 合并单元格
					cellRangeAddress = new CellRangeAddress(createRowIndex, createRowIndex, (short) startIndex,
							(short) mergeAcross);
				}

			} else if (excelCellInfo.getMergeDown() != null && excelCellInfo.getMergeAcross() == null) {
				int mergeDown = createRowIndex;
				if (excelCellInfo.getMergeDown() != 0) {
					// 获取该单元格结束列数
					mergeDown += excelCellInfo.getMergeDown();
					// 合并单元格
					cellRangeAddress = new CellRangeAddress(createRowIndex, mergeDown, (short) startIndex,
							(short) startIndex);
				}
			}

			if (excelCellInfo.getMergeAcross() != null) {
				int length = excelCellInfo.getMergeAcross().intValue();
				for (int i = 0; i < length; i++) {
					startIndex += excelCellInfo.getMergeAcross();
				}
			}
			ExcelCellRangeAddressEntity excelCellRangeAddressEntity = new ExcelCellRangeAddressEntity();
			excelCellRangeAddressEntity.setCellRangeAddress(cellRangeAddress);
			if (excelStyle != null && excelStyle.getBorders() != null) {
				excelCellRangeAddressEntity.setBorders(excelStyle.getBorders());
			}
			cellRangeAddresses.add(excelCellRangeAddressEntity);
		}
		return startIndex;
	}

	/**
	 * 设置文本值内容（XLSX格式）
	 *
	 * @param wb:
	 * @param excelCellInfo:
	 * @param cell:
	 * @param excelStyle:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setValue(XSSFWorkbook wb, ExcelCell excelCellInfo,
								 XSSFCell cell, ExcelStyle excelStyle,
								 CellStyle dataStyle) {
		if (null == excelCellInfo.getExcelData()) {
			return ;
		}
		XSSFFont font = wb.createFont();
		if (!ObjectUtils.isEmpty(excelCellInfo.getExcelData().getType()) && "Number".equals(excelCellInfo.getExcelData().getType())) {
			cell.setCellType(CellType.NUMERIC);
		}
		if (null == excelStyle){
			return ;
		}
		if (excelStyle.getNumberFormat() != null) {
			if ("0%".equals(excelStyle.getNumberFormat().getFormat())) {
				XSSFDataFormat format = wb.createDataFormat();
				dataStyle.setDataFormat(format.getFormat(excelStyle.getNumberFormat().getFormat()));
			} else {
				dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
			}
		}
		if(null == excelStyle.getFont()) {
			return ;
		}
		ExcelStyle.Font esFont = excelStyle.getFont();
		if (!ObjectUtils.isEmpty(esFont.getColor())) {
			XSSFColor xssfColor = new XSSFColor(java.awt.Color.decode(esFont.getColor()));
			font.setColor(xssfColor);
		}
		addFontAttr(excelStyle, font, esFont);
		addCellAttr(excelCellInfo, cell, font);
		dataStyle.setFont(font);
	}

	private static void addCellAttr(ExcelCell excelCellInfo, Cell cell, Font font) {
		ExcelFont excelFont = excelCellInfo.getExcelData().getExcelFont();
		if (excelFont != null) {
			if (excelFont.getBold() > 0) {
				font.setBold(true);
			}
			if ("Number".equals(excelCellInfo.getExcelData().getType())) {
				cell.setCellValue(Float.parseFloat(excelFont.getText()));
			} else {
				cell.setCellValue(excelFont.getText());
			}
			if (!ObjectUtils.isEmpty(excelFont.getCharSet())) {
				font.setCharSet(Integer.valueOf(excelFont.getCharSet()));
			}
		} else {
			if (!ObjectUtils.isEmpty(excelCellInfo.getExcelData().getText())) {
				if ("Number".equals(excelCellInfo.getExcelData().getType())) {
					cell.setCellValue(Float.parseFloat(excelCellInfo.getExcelData().getText().replaceAll(",", "")));
				} else {
					cell.setCellValue(excelCellInfo.getExcelData().getText());
				}
			}
		}
	}

	private static void addFontAttr(ExcelStyle excelStyle, Font font, ExcelStyle.Font esFont) {
		if (esFont.getBold() > 0) {
			font.setBold(true);
		}
		if (!ObjectUtils.isEmpty(esFont.getFontName())) {
			font.setFontName(excelStyle.getFont().getFontName());
		}
		if (esFont.getSize() > 0) {
			// 设置字体大小
			font.setFontHeightInPoints((short) excelStyle.getFont().getSize());
		}
	}

	/**
	 * 设置文本值内容（XLS格式）
	 *
	 * @param wb:
	 * @param excelCellInfo:
	 * @param cell:
	 * @param excelStyle:
	 * @param dataStyle:
	 * @return void
	 */
	private static void setValue(HSSFWorkbook wb, ExcelCell excelCellInfo,
								 HSSFCell cell,
								 ExcelStyle excelStyle, CellStyle dataStyle) {
		if (null == excelCellInfo.getExcelData()) {
			return ;
		}
		HSSFFont font = wb.createFont();
		if (!ObjectUtils.isEmpty(excelCellInfo.getExcelData().getType()) && "Number".equals(excelCellInfo.getExcelData().getType())) {
			cell.setCellType(CellType.NUMERIC);
		}
		if (null == excelStyle){
			return ;
		}
		if (excelStyle.getNumberFormat() != null) {
			if ("0%".equals(excelStyle.getNumberFormat().getFormat())) {
				HSSFDataFormat format = wb.createDataFormat();
				dataStyle.setDataFormat(format.getFormat(excelStyle.getNumberFormat().getFormat()));
			} else {
				dataStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
			}
		}
		if(null == excelStyle.getFont()) {
			return ;
		}
		ExcelStyle.Font esFont = excelStyle.getFont();
		if (!ObjectUtils.isEmpty(esFont.getColor())) {
			HSSFColor hssfColor = getHssfColor(wb, esFont.getColor());
			font.setColor(hssfColor.getIndex());
		}
		addFontAttr(excelStyle, font, esFont);
		addCellAttr(excelCellInfo, cell, font);
		dataStyle.setFont(font);
	}

}
