package cn.henry.study.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Date;
import java.util.Iterator;
import java.util.jar.JarFile;

/**
 * description: 基于apache commonIO封装的文件操作
 *
 * @author Hlingoes
 * @date 2020/1/15 22:46
 */
public class FileHelpUtils {

    private static Logger logger = LoggerFactory.getLogger(FileHelpUtils.class);

    public static String retryDir = "fail_upload_files";

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
                logger.error("Failed to delete file of :{}", file);
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

    /**
     * description: 将文件写入临时文件夹
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/26
     */
    public static void writeTempFile(String rowKey, byte[] bytes) {
        File file = FileHelpUtils.findTempleFailedFile(rowKey);
        // 同一文件，多次失败，不需要重复写入
        if (file.exists()) {
            return;
        }
        try {
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (IOException e) {
            logger.info("写临时文件失败: {}", rowKey, e);
        }
    }

    /**
     * description: 将文件写入临时文件夹
     *
     * @param
     * @return void
     * @author Hlingoes 2020/3/26
     */
    public static void writeTempFile(File file, byte[] bytes) {
        // 同一文件，多次失败，不需要重复写入
        if (file.exists()) {
            return;
        }
        try {
            FileUtils.writeByteArrayToFile(file, bytes);
        } catch (IOException e) {
            logger.info("写临时文件失败: {}", file, e);
        }
    }

    /**
     * description: 获取临时写入文件，将上传失败的文件写到磁盘
     *
     * @param rowKey
     * @return java.io.File
     * @author Hlingoes 2020/4/26
     */
    public static File findTempleFailedFile(String rowKey) {
        File dir = findHomeDir(FileHelpUtils.class);
        String fileName = StringUtils.substringAfterLast(rowKey, "/");
        return FileUtils.getFile(dir, retryDir, fileName);
    }

    public static File findHomeDir(Class clazz) {
        File source = findSource(FileHelpUtils.class);
        return findHomeDir(source).getParentFile();
    }

    public static File findHomeDir(File source) {
        File homeDir = source;
        homeDir = (homeDir == null ? findDefaultHomeDir() : homeDir);
        if (homeDir.isFile()) {
            homeDir = homeDir.getParentFile();
        }
        homeDir = (homeDir.exists() ? homeDir : new File("."));
        return homeDir.getAbsoluteFile();
    }

    private static File findDefaultHomeDir() {
        String userDir = System.getProperty("user.dir");
        return new File(StringUtils.isEmpty(userDir) ? userDir : ".");
    }

    private static File findSource(Class<?> sourceClass) {
        try {
            ProtectionDomain domain = (sourceClass == null ? null : sourceClass.getProtectionDomain());
            CodeSource codeSource = (domain == null ? null : domain.getCodeSource());
            URL location = (codeSource == null ? null : codeSource.getLocation());
            File source = (location == null ? null : findSource(location));
            if (source != null && source.exists() && !isUnitTest()) {
                return source.getAbsoluteFile();
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static File findSource(URL location) throws IOException {
        URLConnection connection = location.openConnection();
        if (connection instanceof JarURLConnection) {
            return getRootJarFile(((JarURLConnection) connection).getJarFile());
        }
        return new File(location.getPath());
    }

    private static boolean isUnitTest() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = stackTrace.length - 1; i >= 0; i--) {
                if (stackTrace[i].getClassName().startsWith("org.junit.")) {
                    return true;
                }
            }
        } catch (Exception ex) {
        }
        return false;
    }

    private static File getRootJarFile(JarFile jarFile) {
        String name = jarFile.getName();
        int separator = name.indexOf("!/");
        if (separator > 0) {
            name = name.substring(0, separator);
        }
        return new File(name);
    }
}
