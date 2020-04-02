package cn.henry.study.utils;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * description: fastDFS文件服务的客户端工具类
 * 代码来源：纯洁的微笑 http://www.ityouknow.com/
 *
 * @author Hlingoes
 * @citation https://github.com/ityouknow/spring-boot-examples/tree/master/spring-boot-fastDFS
 * @date 2020/4/2 10:01
 */
public class FastDFSClientUtils {

    private static Logger logger = LoggerFactory.getLogger(FastDFSClientUtils.class);

    static {
        try {
            String propFilePath = "fastdfs-client.properties";
            ClientGlobal.initByProperties(propFilePath);
        } catch (Exception e) {
            logger.error("FastDFS Client Init Fail!", e);
        }
    }

    /**
     * description: 客户端连接信息
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/4/2
     */
    public static String getClientConfigInfo() {
        return ClientGlobal.configInfo();
    }

    /**
     * description: 文件上传，返回的是分组地groupName和文件的地址索引fieId
     *
     * @param rowKey
     * @param bytes
     * @return java.lang.String[]
     * @author Hlingoes 2020/4/2
     */
    public static String[] upload(String rowKey, byte[] bytes) {
        String fileName = StringUtils.substringAfterLast(rowKey, "/");
        String ext = StringUtils.substringAfterLast(fileName, ".");
        logger.info("upload rowKey: {}, File Length: {}", rowKey, bytes.length);
        // 若有需要，可以加上自定义信息
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", "Hlingoes");

        long startTime = System.currentTimeMillis();
        String[] uploadResults = null;
        StorageClient storageClient = null;
        try {
            storageClient = getTrackerClient();
            uploadResults = storageClient.upload_file(bytes, ext, meta_list);
        } catch (IOException e) {
            logger.error("IO Exception when uploading the file: {}", rowKey, e);
        } catch (Exception e) {
            logger.error("Not IO Exception when uploading the file: {}", rowKey, e);
        }
        logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

        if (uploadResults == null && storageClient != null) {
            logger.error("upload file fail, error code:" + storageClient.getErrorCode());
        }
        String groupName = uploadResults[0];
        String remoteFileName = uploadResults[1];

        logger.info("upload file success，group_name: {}, remoteFileName: {}", groupName, remoteFileName);
        return uploadResults;
    }

    /**
     * description: 获取文件
     *
     * @param groupName
     * @param remoteFileName
     * @return org.csource.fastdfs.FileInfo
     * @author Hlingoes 2020/4/2
     */
    public static FileInfo getFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * description: 获取文件
     *
     * @param groupName
     * @param remoteFileName
     * @return java.io.InputStream
     * @author Hlingoes 2020/4/2
     */
    public static InputStream downFile(String groupName, String remoteFileName) {
        try {
            StorageClient storageClient = getTrackerClient();
            byte[] fileByte = storageClient.download_file(groupName, remoteFileName);
            InputStream ins = new ByteArrayInputStream(fileByte);
            return ins;
        } catch (IOException e) {
            logger.error("IO Exception: Get File from Fast DFS failed", e);
        } catch (Exception e) {
            logger.error("Non IO Exception: Get File from Fast DFS failed", e);
        }
        return null;
    }

    /**
     * description: 删除文件
     *
     * @param groupName
     * @param remoteFileName
     * @return void
     * @author Hlingoes 2020/4/2
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        StorageClient storageClient = getTrackerClient();
        int i = storageClient.delete_file(groupName, remoteFileName);
        logger.info("delete file success: {}", i);
    }

    /**
     * description: 获取文件保存服务器
     *
     * @param groupName
     * @return org.csource.fastdfs.StorageServer[]
     * @author Hlingoes 2020/4/2
     */
    public static StorageServer[] getStoreStorages(String groupName) throws IOException, MyException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        return trackerClient.getStoreStorages(trackerServer, groupName);
    }

    /**
     * description: 获取服务器信息
     *
     * @param groupName
     * @param remoteFileName
     * @return org.csource.fastdfs.ServerInfo[]
     * @author Hlingoes 2020/4/2
     */
    public static ServerInfo[] getFetchStorages(String groupName, String remoteFileName) throws IOException, MyException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        return trackerClient.getFetchStorages(trackerServer, groupName, remoteFileName);
    }

    /**
     * description: 获取tracker服务地址url
     *
     * @param
     * @return java.lang.String
     * @author Hlingoes 2020/4/2
     */
    public static String getTrackerUrl() throws IOException {
        return "http://" + getTrackerServer().getInetSocketAddress().getHostString() + ":" + ClientGlobal.getG_tracker_http_port() + "/";
    }

    /**
     * description: 获取存储服务客户端连接
     *
     * @param
     * @return org.csource.fastdfs.StorageClient
     * @author Hlingoes 2020/4/2
     */
    private static StorageClient getTrackerClient() throws IOException {
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    /**
     * description: 获取文件索引服务器连接
     *
     * @param
     * @return org.csource.fastdfs.TrackerServer
     * @author Hlingoes 2020/4/2
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        return trackerServer;
    }

}
