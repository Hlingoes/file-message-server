package cn.henry.study.appication;

import cn.henry.study.base.DefaultFileService;
import cn.henry.study.pool.FtpClientPool;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: 使用apache common-net实现的文件操作
 *
 * @author Hlingoes
 * @date 2019/12/21 18:09
 */
@Service
public class FtpService extends DefaultFileService {

    private static Logger LOGGER = LoggerFactory.getLogger(FtpService.class);

    /**
     * FTP协议中规定的文件名编码为: iso-8859-1
     */
    private static String FTP_CHARSET = "ISO-8859-1";

    /**
     * 目录分隔符
     */
    private static String SEPARATOR = "/";

    @Override
    public Class<?> getEntityClazz() {
        return FTPClient.class;
    }

    @Autowired
    private FtpClientPool ftpClientPool;


    /**
     * 上传文件
     *
     * @param path      ftp服务器保存目录
     * @param fileName  上传到ftp的文件名
     * @param localPath 待上传文件的名称（绝对地址）
     * @return
     */
    public boolean upload(String path, String fileName, String localPath) {
        String remotePath = path + SEPARATOR + fileName;
        return upload(remotePath, localPath);
    }

    /**
     * description: 上传文件
     *
     * @param path     ftp服务器保存目录
     * @param fileName 上传到ftp的文件名
     * @param file     待上传文件
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    public boolean upload(String path, String fileName, File file) {
        String remotePath = path + SEPARATOR + fileName;
        return upload(remotePath, file);
    }

    /**
     * description: 上传文件
     *
     * @param path        ftp服务器保存目录
     * @param fileName    上传到ftp的文件名
     * @param inputStream 文件流
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    public boolean upload(String path, String fileName, InputStream inputStream) {
        String remotePath = path + SEPARATOR + fileName;
        return upload(remotePath, inputStream);
    }

    /**
     * description: 上传文件
     *
     * @param remotePath ftp服务器保存路径
     * @param localPath  上传的文件路径
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    public boolean upload(String remotePath, String localPath) {
        return upload(remotePath, new File(localPath));
    }

    /**
     * description: 上传文件
     *
     * @param remotePath ftp服务器保存路径
     * @param file       上传的文件
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    @Override
    public boolean upload(String remotePath, File file) {
        boolean success = false;
        try {
            success = upload(remotePath, FileUtils.openInputStream(file));
        } catch (IOException e) {
            LOGGER.error("上传文件[{}]失败", remotePath, e);
        }
        return success;
    }

    /**
     * description: 上传文件
     *
     * @param remotePath  ftp服务器保存路径
     * @param inputStream 文件输出流
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    @Override
    public boolean upload(String remotePath, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = null;
        // 格式化为linux的目录格式
        remotePath = remotePath.replace("\\", SEPARATOR).replaceAll("//", SEPARATOR);
        int index = remotePath.lastIndexOf(SEPARATOR);
        String path = remotePath.substring(0, index);
        String fileName = remotePath.substring(index + 1);
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("{}: 开始上传文件[{}]", Thread.currentThread().getName(), remotePath);
            if (makeAndChangeDirectory(path, ftpClient)) {
                flag = ftpClient.storeFile(encodingPath(fileName, ftpClient), inputStream);
                inputStream.close();
            }
        } catch (Exception e) {
            flag = false;
            LOGGER.error("{}: 上传文件[{}]失败", Thread.currentThread().getName(), remotePath, e);
        } finally {
            if (flag) {
                LOGGER.info("{}: 上传文件[{}]成功", Thread.currentThread().getName(), remotePath);
            }
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * description: 下载文件
     *
     * @param path      FTP服务器文件目录
     * @param fileName  文件名称
     * @param localPath 下载后的文件路径
     * @return
     */
    public boolean download(String path, String fileName, String localPath) {
        String remotePath = path + SEPARATOR + fileName;
        return download(remotePath, localPath);
    }

    /**
     * description: 下载文件
     *
     * @param remotePath FTP服务器文件
     * @param localPath  下载后的文件路径
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    @Override
    public boolean download(String remotePath, String localPath) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始下载文件[{}]", remotePath);
            //切换FTP目录
            if (ftpClient.changeWorkingDirectory(encodingPath(remotePath, ftpClient))) {
                File localFile = new File(localPath);
                String fileName = StringUtils.substringAfterLast(remotePath, SEPARATOR);
                flag = ftpClient.retrieveFile(encodingPath(fileName, ftpClient), FileUtils.openOutputStream(localFile));
            }
        } catch (Exception e) {
            LOGGER.info("下载文件[{}]到[{}]失败", remotePath, localPath);
        } finally {
            if (flag) {
                LOGGER.info("下载文件[{}]到[{}]成功", remotePath, localPath);
            }
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param remotePath FTP服务器的文件名称
     * @return
     */
    public boolean deleteFile(String remotePath) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始删除文件");
            //切换FTP目录
            int index = remotePath.lastIndexOf(SEPARATOR);
            String path = remotePath.substring(0, index);
            String fileName = remotePath.substring(index + 1);
            ftpClient.changeWorkingDirectory(encodingPath(path, ftpClient));
            flag = ftpClient.deleteFile(encodingPath(fileName, ftpClient));
        } catch (Exception e) {
            LOGGER.error("删除文件失败[{}]", remotePath, e);
        } finally {
            if (flag) {
                LOGGER.info("删除文件成功[{}]", remotePath);
            }
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * 获取指定路径下FTP文件
     *
     * @param remotePath 路径
     * @return FTPFile数组
     * @throws IOException
     */
    public FTPFile[] retrieveFTPFiles(String remotePath) throws IOException {
        FTPClient ftpClient = null;
        FTPFile[] files = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            files = ftpClient.listFiles(encodingPath(remotePath, ftpClient),
                    file -> file != null && file.getSize() > 0);
        } catch (Exception e) {
            LOGGER.error("获取指定路径下FTP文件失败: {}", remotePath, e);
        } finally {
            ftpClientPool.returnObject(ftpClient);
        }
        return files;
    }

    /**
     * 获取指定路径下FTP文件名称
     *
     * @param remotePath 路径
     * @return ftp文件名称列表
     * @throws IOException
     */
    public List<String> retrieveFileNames(String remotePath) throws IOException {
        FTPFile[] ftpFiles = retrieveFTPFiles(remotePath);
        if (null == ftpFiles || ftpFiles.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ftpFiles).filter(Objects::nonNull)
                .map(FTPFile::getName).collect(Collectors.toList());
    }

    /**
     * description: 编码文件路径，FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码
     *
     * @param path
     * @param ftpClient
     * @return java.lang.String
     * @author Hlingoes 2019/12/28
     */
    private String encodingPath(String path, FTPClient ftpClient) throws UnsupportedEncodingException {
        return new String(path.getBytes(ftpClient.getControlEncoding()), FTP_CHARSET);
    }

    /**
     * description: 创建多层目录文件，如果ftp服务器上已存在该文件，则不创建，如果无，则创建
     *
     * @param remotePath
     * @param ftpClient
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    public boolean makeAndChangeDirectory(String remotePath, FTPClient ftpClient) throws IOException {
        // 切换到上传目录
        if (!ftpClient.changeWorkingDirectory(encodingPath(remotePath, ftpClient))) {
            //如果目录不存在就创建目录
            String[] dirs = remotePath.split(SEPARATOR);
            String path = "";
            for (String dir : dirs) {
                if (StringUtils.isNotEmpty(dir)) {
                    path = path + SEPARATOR + dir;
                    if (!ftpClient.changeWorkingDirectory(encodingPath(path, ftpClient))) {
                        LOGGER.info("进入文件夹: [{}]失败！开始创建文件夹", path);
                        if (!ftpClient.makeDirectory(encodingPath(path, ftpClient))) {
                            LOGGER.info("创建目录[{}]失败", path);
                            return false;
                        } else {
                            ftpClient.changeWorkingDirectory(encodingPath(path, ftpClient));
                            LOGGER.info("进入文件夹: [{}]  成功", path);
                        }
                    }
                }
            }
        }
        return true;
    }

}
