package cn.henry.study.common.bo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: Excel导入的结果描述，包括成功，失败，重复等几个类型
 *
 * @author Hlingoes
 * @date 2020/5/3 19:38
 */
public class ExcelImportDescription {
    private static final long serialVersionUID = 1L;

    private int success;
    private int fail;
    private int repeat;
    private int total;
    private List<String> details = new ArrayList<>();

    public ExcelImportDescription() {
    }

    /**
     * description: 添加失败记录
     *
     * @param index
     * @return void
     * @author Hlingoes 2020/5/3
     */
    public void addFailMark(int index) {
        this.fail = this.fail + 1;
        this.total = this.total + 1;
        this.details.add("第" + index + "行数据，入库失败");
    }

    /**
     * description: 添加重复记录
     *
     * @param index
     * @return void
     * @author Hlingoes 2020/5/3
     */
    public void addRepeatMark(int index) {
        this.repeat = this.repeat + 1;
        this.total = this.total + 1;
        // 第一行是head，导入数据要从第二行算起
        this.details.add("第" + (index + 1) + "行数据，出现重复，入库失败");
    }

    /**
     * description: 添加成功记录
     *
     * @param index
     * @return void
     * @author Hlingoes 2020/5/3
     */
    public void addSuccessMark(int index) {
        this.success = this.success + 1;
        this.total = this.total + 1;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFail() {
        return fail;
    }

    public void setFail(int fail) {
        this.fail = fail;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "导入结果: " +
                "success: " + success + "条, " +
                "fail: " + fail + "条, " +
                "repeat: " + repeat + "条, " +
                "total: " + total + "条, " +
                "详情: [" + StringUtils.join(details, ",") + "]";
    }
}
