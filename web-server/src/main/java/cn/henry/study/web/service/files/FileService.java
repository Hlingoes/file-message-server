package cn.henry.study.web.service.files;

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
     * @param remotePath
     * @param localPath
     * @return boolean
     * @throws IOException
     */
    boolean download(String remotePath, String localPath);
    
    /**
     * description: 上传文件的方式
     *
     * @param remotePath
     * @param file
     * @return boolean
     * @throws IOException
     */
    boolean upload(String remotePath, File file);

    /**
     * description: 上传文件流的方式
     *
     * @param remotePath
     * @param inputStream
     * @return boolean
     * @throws IOException
     */
    boolean upload(String remotePath, InputStream inputStream);

    /**
     * description: 删除文件
     *
     * @param remotePath
     * @return boolean
     * @throws IOException
     */
    boolean delete(String remotePath);

    /**
     * description: 过滤出符合条件的文件集合
     *
     * @param filePath
     * @param pattern
     * @return java.util.List<java.lang.String>
     */
    List<String> filter(String filePath, Pattern pattern);

    /**
     * description: 实体类的class，业务实现
     *
     * @return java.lang.Class<?>
     * @author Hlingoes 2019/12/21
     */
    Class<?> getEntityClazz();

}
