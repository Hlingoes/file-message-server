package cn.henry.study.common.utils.freemaker2excel.excel;


import java.util.List;


/**
 * @project freemarker-excel
 * @description: 自定义解析excel的Style类
 * @author 大脑补丁
 * @create 2020-04-14 16:54
 */
public class Style {
    private String id;
    private String parent;
    private String name;
    private Alignment alignment;
    private List<Border> borders;
    private Font font;
    private Interior interior;
    private NumberFormat numberFormat;
    private Protection protection;

    public Style() {

    }

    public Style(String id, Alignment alignment, List<Border> borders, Font font, Interior interior) {
        this.id = id;
        this.alignment = alignment;
        this.borders = borders;
        this.font = font;
        this.interior = interior;
    }

    public Style(String id, NumberFormat numberFormat) {
        this.id = id;
        this.numberFormat = numberFormat;
    }

    public static class Alignment {
        private String horizontal;
        private String vertical;
        private String wrapText;

        public Alignment() {

        }

        public Alignment(String horizontal, String vertical, String wrapText) {
            this.horizontal = horizontal;
            this.vertical = vertical;
            this.wrapText = wrapText;
        }

        public String getHorizontal() {
            return horizontal;
        }

        public void setHorizontal(String horizontal) {
            this.horizontal = horizontal;
        }

        public String getVertical() {
            return vertical;
        }

        public void setVertical(String vertical) {
            this.vertical = vertical;
        }

        public String getWrapText() {
            return wrapText;
        }

        public void setWrapText(String wrapText) {
            this.wrapText = wrapText;
        }
    }

    public static class Border {
        private String position;
        private String linestyle;
        private int weight;
        private String color;

        public Border() {

        }

        public Border(String position, String linestyle, int weight, String color) {
            this.position = position;
            this.linestyle = linestyle;
            this.weight = weight;
            this.color = color;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getLinestyle() {
            return linestyle;
        }

        public void setLinestyle(String linestyle) {
            this.linestyle = linestyle;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class Font {
        private String fontName;
        private double size;
        private int bold;
        private String color;
        private Integer CharSet;

        public Font() {

        }

        public Font(String fontName, double size, int bold, String color, Integer charSet) {
            this.fontName = fontName;
            this.size = size;
            this.bold = bold;
            this.color = color;
            CharSet = charSet;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }

        public double getSize() {
            return size;
        }

        public void setSize(double size) {
            this.size = size;
        }

        public int getBold() {
            return bold;
        }

        public void setBold(int bold) {
            this.bold = bold;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getCharSet() {
            return CharSet;
        }

        public void setCharSet(Integer charSet) {
            CharSet = charSet;
        }
    }

    public static class Interior {
        private String color;
        private String pattern;

        public Interior() {

        }

        public Interior(String color, String pattern) {
            this.color = color;
            this.pattern = pattern;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
    }

    public static class NumberFormat {
        private String format;

        public NumberFormat() {

        }

        public NumberFormat(String format) {
            this.format = format;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    // 权限修饰
    public static class Protection {
        private String modifier;

        public Protection() {

        }

        public Protection(String modifier) {
            this.modifier = modifier;
        }

        public String getModifier() {
            return modifier;
        }

        public void setModifier(String modifier) {
            this.modifier = modifier;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public List<Border> getBorders() {
        return borders;
    }

    public void setBorders(List<Border> borders) {
        this.borders = borders;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Interior getInterior() {
        return interior;
    }

    public void setInterior(Interior interior) {
        this.interior = interior;
    }

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public Protection getProtection() {
        return protection;
    }

    public void setProtection(Protection protection) {
        this.protection = protection;
    }
}
