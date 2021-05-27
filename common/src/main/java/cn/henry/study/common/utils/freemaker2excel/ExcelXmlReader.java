package cn.henry.study.common.utils.freemaker2excel;

import cn.henry.study.common.utils.freemaker2excel.excel.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;

/**
 * @author 大脑补丁
 * @project freemarker-excel
 * @description: 读取XML文件工具
 * @create 2020-04-21 08:58
 */
public class ExcelXmlReader {

	// 获取样式
	@SuppressWarnings("rawtypes")
	public static Map<String, ExcelStyle> getStyle(Document document) {
		// 创建一个LinkedHashMap用于存放style，按照id查找
		Map<String, ExcelStyle> styleMap
				= new LinkedHashMap<String, ExcelStyle>();
		// 新建一个Style类用于存放节点数据
		ExcelStyle excelStyle = null;
		// 获取根节点
		Element root = document.getRootElement();
		// 获取根节点下的Styles节点
		Element styles = root.element("Styles");
		// 获取Styles下的Style节点
		List styleList = styles.elements("ExcelStyle");
		Iterator<?> it = styleList.iterator();
		while (it.hasNext()) {
			// 新建一个Style类用于存放节点数据
			excelStyle = new ExcelStyle();
			Element e = (Element) it.next();
			String id = e.attributeValue("ID").toString();
			// 设置style的id
			excelStyle.setId(id);
			if (e.attributeValue("Name") != null) {
				String name = e.attributeValue("Name").toString();
				// 设置style的name
				excelStyle.setName(name);
			}
			// 获取Style下的NumberFormat节点
			Element enumberFormat = e.element("NumberFormat");
			if (enumberFormat != null) {
				ExcelStyle.NumberFormat numberFormat
						= new ExcelStyle.NumberFormat();
				numberFormat.setFormat(enumberFormat.attributeValue("Format"));
				excelStyle.setNumberFormat(numberFormat);
			}
			ExcelStyle.Alignment alignment
					= new ExcelStyle.Alignment();
			// 获取Style下的Alignment节点
			Element ealignment = e.element("Alignment");
			if (ealignment != null) {
				// 设置aligment的相关属性，并且设置style的aliment属性
				alignment.setHorizontal(ealignment.attributeValue("Horizontal"));
				alignment.setVertical(ealignment.attributeValue("Vertical"));
				alignment.setWrapText(ealignment.attributeValue("WrapText"));
				excelStyle.setAlignment(alignment);
			}
			// 获取Style下的Borders节点
			Element Borders = e.element("Borders");
			if (Borders != null) {
				// 获取Borders下的Border节点
				List Border = Borders.elements("Border");
				// 用迭代器遍历Border节点
				Iterator<?> borderIterator = Border.iterator();
				List<ExcelStyle.Border> lborders
						= new ArrayList<ExcelStyle.Border>();
				while (borderIterator.hasNext()) {
					Element bd = (Element) borderIterator.next();
					ExcelStyle.Border border
							= new ExcelStyle.Border();
					border.setPosition(bd.attributeValue("Position"));
					if (bd.attribute("LineStyle") != null) {
						border.setLinestyle(bd.attributeValue("LineStyle"));
						int weight = Integer.parseInt(bd.attributeValue("Weight"));
						border.setWeight(weight);
						border.setColor(bd.attributeValue("Color"));
					}
					lborders.add(border);
				}
				excelStyle.setBorders(lborders);
			}

			// 设置font的相关属性，并且设置style的font属性
			ExcelStyle.Font font
					= new ExcelStyle.Font();
			Element efont = e.element("ExcelFont");
			font.setFontName(efont.attributeValue("FontName"));
			if (efont.attributeValue("Size") != null) {
				double size = Double.parseDouble(efont.attributeValue("Size"));
				font.setSize(size);
			}
			if (efont.attribute("Bold") != null) {
				int bold = Integer.parseInt(efont.attributeValue("Bold"));
				font.setBold(bold);
			}
			font.setColor(efont.attributeValue("Color"));
			excelStyle.setFont(font);
			// 设置Interior的相关属性，并且设置style的interior属性
			ExcelStyle.Interior interior
					= new ExcelStyle.Interior();
			if (e.element("Interior") != null) {
				Element einterior = e.element("Interior");
				interior.setColor(einterior.attributeValue("Color"));
				interior.setPattern(einterior.attributeValue("Pattern"));
			}
			excelStyle.setInterior(interior);
			if (e.element("Protection") != null) {
				Element protectione = e.element("Protection");
				ExcelStyle.Protection protection
						= new ExcelStyle.Protection();
				protection.setModifier(protectione.attributeValue("Protected"));
				excelStyle.setProtection(protection);
			}
			styleMap.put(id, excelStyle);

		}
		return styleMap;
	}

	public static List<ExcelWorksheet> getWorksheet(Document document) {
		List<ExcelWorksheet> excelWorksheets = new ArrayList<>();
		Element root = document.getRootElement();
		// 读取根节点下的Worksheet节点
		List<Element> sheets = root.elements("ExcelWorksheet");
		if (CollectionUtils.isEmpty(sheets)) {
			return excelWorksheets;
		}

		for (Element sheet : sheets) {
			ExcelWorksheet excelWorksheet
					= new ExcelWorksheet();
			String name = sheet.attributeValue("Name");
			excelWorksheet.setName(name);
			ExcelTable excelTable = getTable(sheet);
			excelWorksheet.setExcelTable(excelTable);
			excelWorksheets.add(excelWorksheet);
		}
		return excelWorksheets;
	}

	private static ExcelTable getTable(Element sheet) {
		Element tableElement = sheet.element("ExcelTable");
		if (tableElement == null) {
			return null;
		}
		ExcelTable excelTable
				= new ExcelTable();
		String expandedColumnCount = tableElement.attributeValue("ExpandedColumnCount");
		if (expandedColumnCount != null) {
			excelTable.setExpandedColumnCount(Integer.parseInt(expandedColumnCount));
		}
		String expandedRowCount = tableElement.attributeValue("ExpandedRowCount");
		if (expandedRowCount != null) {
			excelTable.setExpandedRowCount(Integer.parseInt(expandedRowCount));
		}
		String fullColumns = tableElement.attributeValue("FullColumns");
		if (fullColumns != null) {
			excelTable.setFullColumns(Integer.parseInt(fullColumns));
		}

		String fullRows = tableElement.attributeValue("FullRows");
		if (fullRows != null) {
			excelTable.setFullRows(Integer.parseInt(fullRows));
		}
		String defaultColumnWidth = tableElement.attributeValue("DefaultColumnWidth");
		if (defaultColumnWidth != null) {
			excelTable.setDefaultColumnWidth(Double.valueOf(defaultColumnWidth).intValue());
		}

		String defaultRowHeight = tableElement.attributeValue("DefaultRowHeight");
		if (defaultRowHeight != null) {
			excelTable.setDefaultRowHeight(Double.valueOf(defaultRowHeight).intValue());
		}
		// 读取列
		List<ExcelColumn> excelColumns = getColumns(tableElement, expandedColumnCount, defaultColumnWidth);
		excelTable.setExcelColumns(excelColumns);

		// 读取行
		List<ExcelRow> excelRows = getRows(tableElement);
		excelTable.setExcelRows(excelRows);
		return excelTable;
	}

	@SuppressWarnings("unchecked")
	private static List<ExcelRow> getRows(Element tableElement) {
		List<Element> rowElements = tableElement.elements("ExcelRow");
		if (CollectionUtils.isEmpty(rowElements)) {
			return null;
		}
		List<ExcelRow> excelRows = new ArrayList<>();
		for (Element rowElement : rowElements) {
			ExcelRow excelRow =
					new ExcelRow();
			String height = rowElement.attributeValue("Height");
			if (height != null) {
				excelRow.setHeight(Double.valueOf(height).intValue());
			}

			String index = rowElement.attributeValue("Index");
			if (index != null) {
				excelRow.setIndex(Integer.valueOf(index));
			}
			List<ExcelCell> excelCells = getCells(rowElement);
			excelRow.setExcelCells(excelCells);
			excelRows.add(excelRow);
		}
		return excelRows;
	}

	@SuppressWarnings("unchecked")
	private static List<ExcelCell> getCells(Element rowElement) {
		List<Element> cellElements = rowElement.elements("ExcelCell");
		if (CollectionUtils.isEmpty(cellElements)) {
			return null;
		}
		List<ExcelCell> excelCells = new ArrayList<>();
		for (Element cellElement : cellElements) {
			ExcelCell excelCell = new ExcelCell();
			String styleID = cellElement.attributeValue("StyleID");
			if (styleID != null) {
				excelCell.setStyleID(styleID);
			}
			String mergeAcross = cellElement.attributeValue("MergeAcross");
			if (mergeAcross != null) {
				excelCell.setMergeAcross(Integer.valueOf(mergeAcross));
			}

			String mergeDown = cellElement.attributeValue("MergeDown");
			if (mergeDown != null) {
				excelCell.setMergeDown(Integer.valueOf(mergeDown));
			}

			String index = cellElement.attributeValue("Index");
			if (index != null) {
				excelCell.setIndex(Integer.valueOf(index));
			}
			Element commentElement = cellElement.element("ExcelComment");
			if (commentElement != null) {
				ExcelComment excelComment
						= new ExcelComment();
				String author = commentElement.attributeValue("Author");
				Element fontElement = commentElement.element("ExcelFont");
				Element dataElement = commentElement.element("ExcelData");
				if (dataElement != null) {
					ExcelData excelData
							= new ExcelData();
					excelData.setText(dataElement.getStringValue());
					excelComment.setExcelData(excelData);
				}
				if (fontElement != null) {
					ExcelFont excelFont
							= new ExcelFont();
					excelFont.setText(fontElement.getText());
					excelFont.setBold(1);
					String color = fontElement.attributeValue("Color");
					if (color != null) {
						excelFont.setColor(color);
					}
					excelComment.setExcelFont(excelFont);
				}
				excelComment.setAuthor(author);
				excelCell.setExcelComment(excelComment);
			}

			Element dataElement = cellElement.element("ExcelData");
			if (dataElement != null) {
				ExcelData excelData
						= new ExcelData();
				String type = dataElement.attributeValue("Type");
				String xmlns = dataElement.attributeValue("xmlns");
				excelData.setType(type);
				excelData.setXmlns(xmlns);
				excelData.setText(dataElement.getText());
				Element bElement = dataElement.element("B");
				Integer bold = null;
				Element fontElement = null;
				if (bElement != null) {
					fontElement = bElement.element("ExcelFont");
					bold = 1;
				}
				Element uElement = dataElement.element("U");
				if (uElement != null) {
					fontElement = uElement.element("ExcelFont");
				}
				if (fontElement == null) {
					fontElement = dataElement.element("ExcelFont");
				}
				if (fontElement != null) {
					ExcelFont excelFont
							= new ExcelFont();
					String face = fontElement.attributeValue("Face");
					if (face != null) {
						excelFont.setFace(face);
					}
					String charSet = fontElement.attributeValue("CharSet");
					if (charSet != null) {
						excelFont.setCharSet(charSet);
					}
					String color = fontElement.attributeValue("Color");
					if (color != null) {
						excelFont.setColor(color);
					}
					if (bold != null) {
						excelFont.setBold(bold);
					}
					excelFont.setText(fontElement.getText());
					excelData.setExcelFont(excelFont);
				}

				excelCell.setExcelData(excelData);
			}
			excelCells.add(excelCell);
		}
		return excelCells;
	}

	@SuppressWarnings("unchecked")
	private static List<ExcelColumn> getColumns(Element tableElement, String expandedRowCount, String defaultColumnWidth) {
		List<Element> columnElements = tableElement.elements("ExcelColumn");
		if (CollectionUtils.isEmpty(columnElements)) {
			return null;
		}
		if (ObjectUtils.isEmpty(expandedRowCount)) {
			return null;
		}
		int defaultWidth = 60;
		if (!ObjectUtils.isEmpty(defaultColumnWidth)) {
			defaultWidth = Double.valueOf(defaultColumnWidth).intValue();
		}
		List<ExcelColumn> excelColumns = new ArrayList<>();
		int indexNum = 0;
		for (int i = 0; i < columnElements.size(); i++) {
			ExcelColumn excelColumn = new ExcelColumn();
			Element columnElement = columnElements.get(i);
			String index = columnElement.attributeValue("Index");
			if (index != null) {
				if (indexNum < Integer.valueOf(index) - 1) {
					for (int j = indexNum; j < Integer.valueOf(index) - 1; j++) {
						excelColumn = new ExcelColumn();
						excelColumn.setIndex(indexNum);
						excelColumn.setWidth(defaultWidth);
						excelColumns.add(excelColumn);
						indexNum += 1;
					}
				}
				excelColumn = new ExcelColumn();
			}
			excelColumn.setIndex(indexNum);
			String autoFitWidth = columnElement.attributeValue("AutoFitWidth");
			if (autoFitWidth != null) {
				excelColumn.setAutoFitWidth(Double.valueOf(autoFitWidth).intValue());
			}
			String width = columnElement.attributeValue("Width");
			if (width != null) {
				excelColumn.setWidth(Double.valueOf(width).intValue());
			}
			excelColumns.add(excelColumn);
			indexNum += 1;
		}
		if (excelColumns.size() < Integer.valueOf(expandedRowCount)) {
			for (int i = excelColumns.size() + 1; i <= Integer.valueOf(expandedRowCount); i++) {
				ExcelColumn excelColumn
						= new ExcelColumn();
				excelColumn.setIndex(i);
				excelColumn.setWidth(defaultWidth);
				excelColumns.add(excelColumn);
			}
		}
		return excelColumns;
	}
}
