package cn.henry.study.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * description: 文件服务的通用接口
 *
 * @Author Hlingoes
 * @Date 2019/12/21
 */
public interface FileService {

    /**
     * description: 下载文件
     *
     * @param fileName
     * @param localPath
     * @return java.io.File
     * @throws IOException
     */
    File download(String fileName, String localPath) throws IOException;
    
    /**
     * description: 上传文件的方式
     *
     * @param fileName
     * @param file
     * @return boolean
     * @throws IOException
     */
    boolean upload(String fileName, File file) throws IOException;

    /**
     * description: 上传文件流的方式
     *
     * @param fileName
     * @param inputStream
     * @return boolean
     * @throws IOException
     */
    boolean upload(String fileName, InputStream inputStream) throws IOException;

    /**
     * description: 上传字节码的方式
     *
     * @param fileName
     * @param bytes
     * @return boolean
     * @throws IOException
     */
    boolean upload(String fileName, byte[] bytes) throws IOException;

    /**
     * description: 删除文件
     *
     * @param fileName
     * @return boolean
     * @throws IOException
     */
    boolean delete(String fileName) throws IOException;

    /**
     * description: 过滤出符合条件的文件集合
     *
     * @param filePath
     * @param pattern
     * @return java.util.List<java.lang.String>
     */
    List<String> filter(String filePath, Pattern pattern) throws IOException;

    /**
     * description: 实体类的class，业务实现
     *
     * @return java.lang.Class<?>
     * @author Hlingoes 2019/12/21
     */
    Class<?> getEntityClazz();

}
