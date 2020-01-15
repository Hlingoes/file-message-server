package cn.henry.study.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * description: 基于apache commonIO封装的文件操作
 *
 * @author Hlingoes
 * @date 2020/1/15 22:46
 */
public class FileHelpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelpUtils.class);

    /**
     * description: 删除过期文件
     *
     * @param dirPath
     * @param days
     * @return boolean
     * @author Hlingoes 2020/1/15
     */
    public static boolean deleteOutDateFiles(String dirPath, int days) {
        // 计算备份日期，备份该日期之前的文件
        Date pointDate = new Date();
        long timeInterval = pointDate.getTime() - convertDaysToMilliseconds(days);
        pointDate.setTime(timeInterval);

        // 设置文件过滤条件
        IOFileFilter timeFileFilter = FileFilterUtils.ageFileFilter(pointDate, true);
        IOFileFilter fileFiles = FileFilterUtils.and(FileFileFilter.FILE, timeFileFilter);

        // 删除符合条件的文件
        File deleteRootFolder = new File(dirPath);
        Iterator itFile = FileUtils.iterateFiles(deleteRootFolder, fileFiles, TrueFileFilter.INSTANCE);
        while (itFile.hasNext()) {
            File file = (File) itFile.next();
            boolean result = file.delete();
            if (!result) {
                LOGGER.error("Failed to delete file of :{}", file);
                return false;
            }
        }
        return true;
    }

    /**
     * description: 删除空目录
     *
     * @param dir
     * @return void
     * @author Hlingoes 2020/1/15
     */
    public static void deleteEmptyDir(File dir) {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            if (fs != null && fs.length > 0) {
                for (int i = 0; i < fs.length; i++) {
                    File tmpFile = fs[i];
                    if (tmpFile.isDirectory()) {
                        deleteEmptyDir(tmpFile);
                    }
                    if (tmpFile.isDirectory() && tmpFile.listFiles().length == 0) {
                        tmpFile.delete();
                    }
                }
            }
            if (dir.isDirectory() && dir.listFiles().length == 0) {
                dir.delete();
            }
        }
    }

    /**
     * description: 将天转换为毫秒
     *
     * @param days
     * @return long
     * @author Hlingoes 2020/1/15
     */
    public static long convertDaysToMilliseconds(int days) {
        return days * 24L * 3600 * 1000;
    }
}
