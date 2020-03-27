package cn.henry.study.service;

import cn.henry.study.base.DefaultFileService;
import cn.henry.study.exceptions.DataSendFailRetryException;
import cn.henry.study.pool.FtpClientPool;
import cn.henry.study.base.RetryMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
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
     * @return boolean
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
        boolean success = false;
        try {
            success = upload(path, fileName, FileUtils.openInputStream(file));
        } catch (Exception e) {
            LOGGER.error("上传文件[{}]失败", path + SEPARATOR + fileName, e);
        }
        return success;
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
        boolean flag = false;
        FTPClient ftpClient = null;
        // 格式化为linux的目录格式
        path = path.replace("\\", SEPARATOR).replaceAll("//", SEPARATOR);
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始上传文件[{}]", path + SEPARATOR + fileName);
            if (makeAndChangeDirectory(path, ftpClient)) {
                flag = ftpClient.storeFile(encodingPath(fileName, ftpClient), inputStream);
                inputStream.close();
            } else {
                LOGGER.error("切换目录失败", path);
            }
        } catch (Exception e) {
            flag = false;
            LOGGER.error("上传文件[{}]失败", path + SEPARATOR + fileName, e);
            String rowKey = path + SEPARATOR + fileName;
            try {
                byte[] content = IOUtils.toByteArray(inputStream);
                RetryMessage failMessage = new RetryMessage(this, rowKey, content);
                throw new DataSendFailRetryException(this, failMessage);
            } catch (IOException ex) {
                LOGGER.info("文件流读取失败", ex);
            }
        } finally {
            if (flag) {
                LOGGER.info("上传文件[{}]成功", path + SEPARATOR + fileName);
            }
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
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
        int index = remotePath.lastIndexOf(SEPARATOR);
        String path = remotePath.substring(0, index);
        String fileName = remotePath.substring(index + 1);
        return upload(path, fileName, inputStream);
    }

    /**
     * description: 下载文件
     *
     * @param path      FTP服务器文件目录
     * @param fileName  文件名称
     * @param localPath 下载后的文件路径
     * @return boolean
     */
    public boolean download(String path, String fileName, String localPath) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始下载文件[{}]", path + SEPARATOR + fileName);
            //切换FTP目录
            if (ftpClient.changeWorkingDirectory(encodingPath(path, ftpClient))) {
                File localFile = new File(localPath);
                OutputStream outputStream = FileUtils.openOutputStream(localFile);
                flag = ftpClient.retrieveFile(encodingPath(fileName, ftpClient), outputStream);
                IOUtils.closeQuietly(outputStream);
            } else {
                LOGGER.error("切换目录失败", path);
            }
        } catch (Exception e) {
            LOGGER.info("下载文件[{}]到[{}]失败", path + SEPARATOR + fileName, localPath);
        } finally {
            if (flag) {
                LOGGER.info("下载文件[{}]到[{}]成功", path + SEPARATOR + fileName, localPath);
            } else {
                LOGGER.info("下载文件[{}]到[{}]失败", path + SEPARATOR + fileName, localPath);
            }
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
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
        int index = remotePath.lastIndexOf(SEPARATOR);
        String path = remotePath.substring(0, index);
        String fileName = remotePath.substring(index + 1);
        return download(path, fileName, localPath);
    }

    /**
     * 删除文件
     *
     * @param remotePath FTP服务器的文件名称
     * @return boolean
     */
    @Override
    public boolean delete(String remotePath) {
        int index = remotePath.lastIndexOf(SEPARATOR);
        String path = remotePath.substring(0, index);
        String fileName = remotePath.substring(index + 1);
        return delete(path, fileName);
    }

    /**
     * description:
     *
     * @param path     文件目录
     * @param fileName 文件名
     * @return boolean
     * @author Hlingoes 2019/12/29
     */
    public boolean delete(String path, String fileName) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始删除文件[{}]", path + SEPARATOR + fileName);
            //切换FTP目录
            if (ftpClient.changeWorkingDirectory(encodingPath(path, ftpClient))) {
                flag = ftpClient.deleteFile(encodingPath(fileName, ftpClient));
            } else {
                LOGGER.error("切换目录失败", path);
            }
        } catch (Exception e) {
            LOGGER.error("删除文件失败[{}]", path + SEPARATOR + fileName, e);
        } finally {
            if (flag) {
                LOGGER.info("删除文件成功[{}]", path + SEPARATOR + fileName);
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
    public FTPFile[] retrieveFtpFiles(String remotePath) throws IOException {
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
        FTPFile[] ftpFiles = retrieveFtpFiles(remotePath);
        if (null == ftpFiles || ftpFiles.length == 0) {
            return new ArrayList<>();
        }
        return Arrays.stream(ftpFiles).filter(Objects::nonNull)
                .map(FTPFile::getName).collect(Collectors.toList());
    }

    /**
     * description: 编码文件路径，FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码
     *
     * @param path      路径
     * @param ftpClient 客户端
     * @return java.lang.String
     * @author Hlingoes 2019/12/28
     */
    private String encodingPath(String path, FTPClient ftpClient) throws UnsupportedEncodingException {
        return new String(path.getBytes(ftpClient.getControlEncoding()), "ISO-8859-1");
    }

    /**
     * description: 创建多层目录文件，如果ftp服务器上已存在该文件，则不创建，如果无，则创建
     *
     * @param remotePath 路径
     * @param ftpClient  客户端
     * @return boolean
     * @author Hlingoes 2019/12/28
     */
    public synchronized boolean makeAndChangeDirectory(String remotePath, FTPClient ftpClient) throws IOException {
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
