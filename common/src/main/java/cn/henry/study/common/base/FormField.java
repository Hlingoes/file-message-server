package cn.henry.study.common.base;

import java.util.List;

/**
 * description: 表单的实体
 *
 * @author Hlingoes
 * @date 2019/12/22 14:22
 */
public class FormField {
    String prop;
    String label;
    String type;
    boolean required;
    String alias;
    List<String> value;

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FormField{" +
                "label='" + label + '\'' +
                ", type='" + type + '\'' +
                ", required=" + required +
                ", alias='" + alias + '\'' +
                ", value=" + value +
                '}';
    }
}
