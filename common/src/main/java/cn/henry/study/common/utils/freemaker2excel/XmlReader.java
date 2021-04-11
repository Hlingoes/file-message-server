package cn.henry.study.common.utils.freemaker2excel;

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
public class XmlReader {

	// 获取样式
	@SuppressWarnings("rawtypes")
	public static Map<String, cn.henry.study.common.utils.freemaker2excel.excel.Style> getStyle(Document document) {
		// 创建一个LinkedHashMap用于存放style，按照id查找
		Map<String, cn.henry.study.common.utils.freemaker2excel.excel.Style> styleMap
				= new LinkedHashMap<String, cn.henry.study.common.utils.freemaker2excel.excel.Style>();
		// 新建一个Style类用于存放节点数据
		cn.henry.study.common.utils.freemaker2excel.excel.Style style = null;
		// 获取根节点
		Element root = document.getRootElement();
		// 获取根节点下的Styles节点
		Element styles = root.element("Styles");
		// 获取Styles下的Style节点
		List styleList = styles.elements("Style");
		Iterator<?> it = styleList.iterator();
		while (it.hasNext()) {
			// 新建一个Style类用于存放节点数据
			style = new cn.henry.study.common.utils.freemaker2excel.excel.Style();
			Element e = (Element) it.next();
			String id = e.attributeValue("ID").toString();
			// 设置style的id
			style.setId(id);
			if (e.attributeValue("Name") != null) {
				String name = e.attributeValue("Name").toString();
				// 设置style的name
				style.setName(name);
			}
			// 获取Style下的NumberFormat节点
			Element enumberFormat = e.element("NumberFormat");
			if (enumberFormat != null) {
				cn.henry.study.common.utils.freemaker2excel.excel.Style.NumberFormat numberFormat
						= new cn.henry.study.common.utils.freemaker2excel.excel.Style.NumberFormat();
				numberFormat.setFormat(enumberFormat.attributeValue("Format"));
				style.setNumberFormat(numberFormat);
			}
			cn.henry.study.common.utils.freemaker2excel.excel.Style.Alignment alignment
					= new cn.henry.study.common.utils.freemaker2excel.excel.Style.Alignment();
			// 获取Style下的Alignment节点
			Element ealignment = e.element("Alignment");
			if (ealignment != null) {
				// 设置aligment的相关属性，并且设置style的aliment属性
				alignment.setHorizontal(ealignment.attributeValue("Horizontal"));
				alignment.setVertical(ealignment.attributeValue("Vertical"));
				alignment.setWrapText(ealignment.attributeValue("WrapText"));
				style.setAlignment(alignment);
			}
			// 获取Style下的Borders节点
			Element Borders = e.element("Borders");
			if (Borders != null) {
				// 获取Borders下的Border节点
				List Border = Borders.elements("Border");
				// 用迭代器遍历Border节点
				Iterator<?> borderIterator = Border.iterator();
				List<cn.henry.study.common.utils.freemaker2excel.excel.Style.Border> lborders
						= new ArrayList<cn.henry.study.common.utils.freemaker2excel.excel.Style.Border>();
				while (borderIterator.hasNext()) {
					Element bd = (Element) borderIterator.next();
					cn.henry.study.common.utils.freemaker2excel.excel.Style.Border border
							= new cn.henry.study.common.utils.freemaker2excel.excel.Style.Border();
					border.setPosition(bd.attributeValue("Position"));
					if (bd.attribute("LineStyle") != null) {
						border.setLinestyle(bd.attributeValue("LineStyle"));
						int weight = Integer.parseInt(bd.attributeValue("Weight"));
						border.setWeight(weight);
						border.setColor(bd.attributeValue("Color"));
					}
					lborders.add(border);
				}
				style.setBorders(lborders);
			}

			// 设置font的相关属性，并且设置style的font属性
			cn.henry.study.common.utils.freemaker2excel.excel.Style.Font font
					= new cn.henry.study.common.utils.freemaker2excel.excel.Style.Font();
			Element efont = e.element("Font");
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
			style.setFont(font);
			// 设置Interior的相关属性，并且设置style的interior属性
			cn.henry.study.common.utils.freemaker2excel.excel.Style.Interior interior
					= new cn.henry.study.common.utils.freemaker2excel.excel.Style.Interior();
			if (e.element("Interior") != null) {
				Element einterior = e.element("Interior");
				interior.setColor(einterior.attributeValue("Color"));
				interior.setPattern(einterior.attributeValue("Pattern"));
			}
			style.setInterior(interior);
			if (e.element("Protection") != null) {
				Element protectione = e.element("Protection");
				cn.henry.study.common.utils.freemaker2excel.excel.Style.Protection protection
						= new cn.henry.study.common.utils.freemaker2excel.excel.Style.Protection();
				protection.setModifier(protectione.attributeValue("Protected"));
				style.setProtection(protection);
			}
			styleMap.put(id, style);

		}
		return styleMap;
	}

	public static List<cn.henry.study.common.utils.freemaker2excel.excel.Worksheet> getWorksheet(Document document) {
		List<cn.henry.study.common.utils.freemaker2excel.excel.Worksheet> worksheets = new ArrayList<>();
		Element root = document.getRootElement();
		// 读取根节点下的Worksheet节点
		List<Element> sheets = root.elements("Worksheet");
		if (CollectionUtils.isEmpty(sheets)) {
			return worksheets;
		}

		for (Element sheet : sheets) {
			cn.henry.study.common.utils.freemaker2excel.excel.Worksheet worksheet
					= new cn.henry.study.common.utils.freemaker2excel.excel.Worksheet();
			String name = sheet.attributeValue("Name");
			worksheet.setName(name);
			cn.henry.study.common.utils.freemaker2excel.excel.Table table = getTable(sheet);
			worksheet.setTable(table);
			worksheets.add(worksheet);
		}
		return worksheets;
	}

	private static cn.henry.study.common.utils.freemaker2excel.excel.Table getTable(Element sheet) {
		Element tableElement = sheet.element("Table");
		if (tableElement == null) {
			return null;
		}
		cn.henry.study.common.utils.freemaker2excel.excel.Table table
				= new cn.henry.study.common.utils.freemaker2excel.excel.Table();
		String expandedColumnCount = tableElement.attributeValue("ExpandedColumnCount");
		if (expandedColumnCount != null) {
			table.setExpandedColumnCount(Integer.parseInt(expandedColumnCount));
		}
		String expandedRowCount = tableElement.attributeValue("ExpandedRowCount");
		if (expandedRowCount != null) {
			table.setExpandedRowCount(Integer.parseInt(expandedRowCount));
		}
		String fullColumns = tableElement.attributeValue("FullColumns");
		if (fullColumns != null) {
			table.setFullColumns(Integer.parseInt(fullColumns));
		}

		String fullRows = tableElement.attributeValue("FullRows");
		if (fullRows != null) {
			table.setFullRows(Integer.parseInt(fullRows));
		}
		String defaultColumnWidth = tableElement.attributeValue("DefaultColumnWidth");
		if (defaultColumnWidth != null) {
			table.setDefaultColumnWidth(Double.valueOf(defaultColumnWidth).intValue());
		}

		String defaultRowHeight = tableElement.attributeValue("DefaultRowHeight");
		if (defaultRowHeight != null) {
			table.setDefaultRowHeight(Double.valueOf(defaultRowHeight).intValue());
		}
		// 读取列
		List<cn.henry.study.common.utils.freemaker2excel.excel.Column> columns = getColumns(tableElement, expandedColumnCount, defaultColumnWidth);
		table.setColumns(columns);

		// 读取行
		List<cn.henry.study.common.utils.freemaker2excel.excel.Row> rows = getRows(tableElement);
		table.setRows(rows);
		return table;
	}

	@SuppressWarnings("unchecked")
	private static List<cn.henry.study.common.utils.freemaker2excel.excel.Row> getRows(Element tableElement) {
		List<Element> rowElements = tableElement.elements("Row");
		if (CollectionUtils.isEmpty(rowElements)) {
			return null;
		}
		List<cn.henry.study.common.utils.freemaker2excel.excel.Row> rows = new ArrayList<>();
		for (Element rowElement : rowElements) {
			cn.henry.study.common.utils.freemaker2excel.excel.Row row =
					new cn.henry.study.common.utils.freemaker2excel.excel.Row();
			String height = rowElement.attributeValue("Height");
			if (height != null) {
				row.setHeight(Double.valueOf(height).intValue());
			}

			String index = rowElement.attributeValue("Index");
			if (index != null) {
				row.setIndex(Integer.valueOf(index));
			}
			List<cn.henry.study.common.utils.freemaker2excel.excel.Cell> cells = getCells(rowElement);
			row.setCells(cells);
			rows.add(row);
		}
		return rows;
	}

	@SuppressWarnings("unchecked")
	private static List<cn.henry.study.common.utils.freemaker2excel.excel.Cell> getCells(Element rowElement) {
		List<Element> cellElements = rowElement.elements("Cell");
		if (CollectionUtils.isEmpty(cellElements)) {
			return null;
		}
		List<cn.henry.study.common.utils.freemaker2excel.excel.Cell> cells = new ArrayList<>();
		for (Element cellElement : cellElements) {
			cn.henry.study.common.utils.freemaker2excel.excel.Cell cell = new cn.henry.study.common.utils.freemaker2excel.excel.Cell();
			String styleID = cellElement.attributeValue("StyleID");
			if (styleID != null) {
				cell.setStyleID(styleID);
			}
			String mergeAcross = cellElement.attributeValue("MergeAcross");
			if (mergeAcross != null) {
				cell.setMergeAcross(Integer.valueOf(mergeAcross));
			}

			String mergeDown = cellElement.attributeValue("MergeDown");
			if (mergeDown != null) {
				cell.setMergeDown(Integer.valueOf(mergeDown));
			}

			String index = cellElement.attributeValue("Index");
			if (index != null) {
				cell.setIndex(Integer.valueOf(index));
			}
			Element commentElement = cellElement.element("Comment");
			if (commentElement != null) {
				cn.henry.study.common.utils.freemaker2excel.excel.Comment comment
						= new cn.henry.study.common.utils.freemaker2excel.excel.Comment();
				String author = commentElement.attributeValue("Author");
				Element fontElement = commentElement.element("Font");
				Element dataElement = commentElement.element("Data");
				if (dataElement != null) {
					cn.henry.study.common.utils.freemaker2excel.excel.Data data
							= new cn.henry.study.common.utils.freemaker2excel.excel.Data();
					data.setText(dataElement.getStringValue());
					comment.setData(data);
				}
				if (fontElement != null) {
					cn.henry.study.common.utils.freemaker2excel.excel.Font font
							= new cn.henry.study.common.utils.freemaker2excel.excel.Font();
					font.setText(fontElement.getText());
					font.setBold(1);
					String color = fontElement.attributeValue("Color");
					if (color != null) {
						font.setColor(color);
					}
					comment.setFont(font);
				}
				comment.setAuthor(author);
				cell.setComment(comment);
			}

			Element dataElement = cellElement.element("Data");
			if (dataElement != null) {
				cn.henry.study.common.utils.freemaker2excel.excel.Data data
						= new cn.henry.study.common.utils.freemaker2excel.excel.Data();
				String type = dataElement.attributeValue("Type");
				String xmlns = dataElement.attributeValue("xmlns");
				data.setType(type);
				data.setXmlns(xmlns);
				data.setText(dataElement.getText());
				Element bElement = dataElement.element("B");
				Integer bold = null;
				Element fontElement = null;
				if (bElement != null) {
					fontElement = bElement.element("Font");
					bold = 1;
				}
				Element uElement = dataElement.element("U");
				if (uElement != null) {
					fontElement = uElement.element("Font");
				}
				if (fontElement == null) {
					fontElement = dataElement.element("Font");
				}
				if (fontElement != null) {
					cn.henry.study.common.utils.freemaker2excel.excel.Font font
							= new cn.henry.study.common.utils.freemaker2excel.excel.Font();
					String face = fontElement.attributeValue("Face");
					if (face != null) {
						font.setFace(face);
					}
					String charSet = fontElement.attributeValue("CharSet");
					if (charSet != null) {
						font.setCharSet(charSet);
					}
					String color = fontElement.attributeValue("Color");
					if (color != null) {
						font.setColor(color);
					}
					if (bold != null) {
						font.setBold(bold);
					}
					font.setText(fontElement.getText());
					data.setFont(font);
				}

				cell.setData(data);
			}
			cells.add(cell);
		}
		return cells;
	}

	@SuppressWarnings("unchecked")
	private static List<cn.henry.study.common.utils.freemaker2excel.excel.Column> getColumns(Element tableElement, String expandedRowCount, String defaultColumnWidth) {
		List<Element> columnElements = tableElement.elements("Column");
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
		List<cn.henry.study.common.utils.freemaker2excel.excel.Column> columns = new ArrayList<>();
		int indexNum = 0;
		for (int i = 0; i < columnElements.size(); i++) {
			cn.henry.study.common.utils.freemaker2excel.excel.Column column = new cn.henry.study.common.utils.freemaker2excel.excel.Column();
			Element columnElement = columnElements.get(i);
			String index = columnElement.attributeValue("Index");
			if (index != null) {
				if (indexNum < Integer.valueOf(index) - 1) {
					for (int j = indexNum; j < Integer.valueOf(index) - 1; j++) {
						column = new cn.henry.study.common.utils.freemaker2excel.excel.Column();
						column.setIndex(indexNum);
						column.setWidth(defaultWidth);
						columns.add(column);
						indexNum += 1;
					}
				}
				column = new cn.henry.study.common.utils.freemaker2excel.excel.Column();
			}
			column.setIndex(indexNum);
			String autoFitWidth = columnElement.attributeValue("AutoFitWidth");
			if (autoFitWidth != null) {
				column.setAutofitwidth(Double.valueOf(autoFitWidth).intValue());
			}
			String width = columnElement.attributeValue("Width");
			if (width != null) {
				column.setWidth(Double.valueOf(width).intValue());
			}
			columns.add(column);
			indexNum += 1;
		}
		if (columns.size() < Integer.valueOf(expandedRowCount)) {
			for (int i = columns.size() + 1; i <= Integer.valueOf(expandedRowCount); i++) {
				cn.henry.study.common.utils.freemaker2excel.excel.Column column
						= new cn.henry.study.common.utils.freemaker2excel.excel.Column();
				column.setIndex(i);
				column.setWidth(defaultWidth);
				columns.add(column);
			}
		}
		return columns;
	}
}
