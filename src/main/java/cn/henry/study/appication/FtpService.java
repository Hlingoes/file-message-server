package cn.henry.study.appication;

import cn.henry.study.base.DefaultFileService;
import cn.henry.study.pool.FtpClientPool;
import org.apache.commons.io.FileUtils;
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

    @Override
    public Class<?> getEntityClazz() {
        return FTPClient.class;
    }

    @Autowired
    private FtpClientPool ftpClientPool;

    /**
     * 上传文件
     *
     * @param pathname       ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param originFilename 待上传文件的名称（绝对地址）
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, String originFilename) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始上传文件");
            createDirecroty(pathname, ftpClient);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, FileUtils.openInputStream(new File(originFilename)));
            flag = true;
            LOGGER.info("上传文件成功");
        } catch (Exception e) {
            LOGGER.error("上传文件失败");
        } finally {
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * 上传文件
     *
     * @param pathname    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFile(String pathname, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始上传文件");
            createDirecroty(pathname, ftpClient);
            ftpClient.makeDirectory(pathname);
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.storeFile(fileName, inputStream);
            inputStream.close();
            flag = true;
            LOGGER.info("上传文件成功");
        } catch (Exception e) {
            LOGGER.error("上传文件失败");
        } finally {
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localPath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localPath) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始下载文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localPath + "/" + file.getName());
                    ftpClient.retrieveFile(file.getName(), FileUtils.openOutputStream(localFile));
                }
            }
            flag = true;
            LOGGER.info("下载文件成功");
        } catch (Exception e) {
            LOGGER.error("下载文件失败");
        } finally {
            ftpClientPool.returnObject(ftpClient);
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param pathname FTP服务器保存目录
     * @param filename 要删除的文件名称
     * @return
     */
    public boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        FTPClient ftpClient = null;
        try {
            ftpClient = ftpClientPool.borrowObject();
            LOGGER.info("开始删除文件");
            //切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            LOGGER.info("删除文件成功");
        } catch (Exception e) {
            LOGGER.error("删除文件失败, pathname: {}, filename: {}", pathname, filename, e);
        } finally {
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
            files = ftpClient.listFiles(encodingPath(remotePath + "/"),
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
     * 编码文件路径
     */
    private static String encodingPath(String path) throws UnsupportedEncodingException {
        // FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码
        return new String(path.replaceAll("//", "/").getBytes("GBK"), "iso-8859-1");
    }

    /**
     * 创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
     *
     * @param remote
     * @param ftpClient
     * @return
     * @throws IOException
     */
    public boolean createDirecroty(String remote, FTPClient ftpClient) throws IOException {
        boolean success = true;
        String separator = "/";
        String directory = remote + separator;
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase(separator) && !changeWorkingDirectory(new String(directory), ftpClient)) {
            int start = 0;
            int end = 0;
            if (directory.startsWith(separator)) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf(separator, start);
            String path = "";
            String paths = "";
            while (true) {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                path = path + separator + subDirectory;
                if (!existFile(path, ftpClient)) {
                    if (makeDirectory(subDirectory, ftpClient)) {
                        changeWorkingDirectory(subDirectory, ftpClient);
                    } else {
                        LOGGER.info("创建目录[{}]失败", subDirectory);
                        changeWorkingDirectory(subDirectory, ftpClient);
                    }
                } else {
                    changeWorkingDirectory(subDirectory, ftpClient);
                }
                paths = paths + separator + subDirectory;
                start = end + 1;
                end = directory.indexOf(separator, start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    /**
     * 改变目录路径
     *
     * @param directory
     * @param ftpClient
     * @return
     */
    public boolean changeWorkingDirectory(String directory, FTPClient ftpClient) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                LOGGER.info("进入文件夹: {}, 成功！", directory);
            } else {
                LOGGER.info("进入文件夹: {}, 失败！开始创建文件夹", directory);
            }
        } catch (IOException e) {
            LOGGER.info("进入文件夹: {}, 失败", e);
        }
        return flag;
    }

    /**
     * description: 判断ftp服务器文件是否存在
     *
     * @param path
     * @param ftpClient
     * @return boolean
     * @author Hlingoes 2019/12/22
     */
    public boolean existFile(String path, FTPClient ftpClient) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * description: 创建目录
     *
     * @param dir
     * @param ftpClient
     * @return boolean
     * @author Hlingoes 2019/12/22
     */
    public boolean makeDirectory(String dir, FTPClient ftpClient) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                LOGGER.info("创建文件夹: {}, 成功！", dir);
            } else {
                LOGGER.info("创建文件夹: {}, 失败！", dir);
            }
        } catch (Exception e) {
            LOGGER.error("创建文件夹: {}, 失败！", dir, e);
        }
        return flag;
    }
}
