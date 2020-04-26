package cn.henry.study.common.base;

import java.io.Serializable;
import java.util.Date;

/**
 * description: 数据表必备字段
 *
 * @author Hlingoes
 * @date 2020/4/3 20:12
 */
public class Metas implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Metas() {
    }

    @Override
    public String toString() {
        return "Metas{" +
                "id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
