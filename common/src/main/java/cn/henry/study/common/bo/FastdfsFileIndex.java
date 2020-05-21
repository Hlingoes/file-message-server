package cn.henry.study.common.bo;

import java.io.Serializable;

/**
 * description: 存储到fastdfs后，记录在数据库中的文件缩影
 *
 * @author Hlingoes
 * @date 2020/4/25 20:54
 */
public class FastdfsFileIndex extends Metas implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 原始文件的path或者标定信息
     */
    private String rowKey;

    /**
     * fastdfs的分组
     */
    private String groupName;

    /**
     * fastdfs的文件storage文件地址
     */
    private String remoteFileName;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }

    @Override
    public String toString() {
        return "FastdfsFileIndex{" +
                "rowKey='" + rowKey + '\'' +
                ", groupName='" + groupName + '\'' +
                ", remoteFileName='" + remoteFileName + '\'' +
                "} " + super.toString();
    }
}
