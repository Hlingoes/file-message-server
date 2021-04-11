package cn.henry.study.common.utils.freemaker2excel.excel;

/**
 * @project cne-power-operation-web
 * @description: 单元格注释
 * @author 大脑补丁
 * @create: 2020-08-11 17:34
 */
public class Comment {

	private String author;

	private Data data;

	private Font font;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}