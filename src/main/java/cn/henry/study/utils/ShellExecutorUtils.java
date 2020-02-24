package cn.henry.study.utils;

import ch.ethz.ssh2.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * description: 远程操作linux服务器的工具类，包括命令下发，文件传输，获取回显的示例用法
 *
 * @author Hlingoes
 * @date 2020/2/24 22:10
 */
public class ShellExecutorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShellExecutorUtils.class);

    private String charset = Charset.defaultCharset().toString();

    private static final int TIME_OUT = 1000 * 5 * 60;

    /**
     * description: 登录获取连接
     *
     * @param
     * @return boolean
     * @author Hlingoes 2020/2/24
     */
    private Connection getConnection(String host, String user, String password) {
        Connection conn = null;
        try {
            conn = new Connection(host);
            conn.connect();
            if (conn.authenticateWithPassword(user, password)) {
                return null;
            }
        } catch (IOException e) {
            LOGGER.error("connect fail", e);
        }
        return conn;
    }

    /**
     * description: 获取数据流
     *
     * @param in
     * @param charset
     * @return java.lang.String
     * @author Hlingoes 2020/2/24
     */
    private String processStream(InputStream in, Charset charset) throws IOException {
        byte[] buf = new byte[1024];
        StringBuilder sb = new StringBuilder();
        while (in.read(buf) != -1) {
            sb.append(new String(buf, charset));
        }
        return sb.toString();
    }

    /**
     * description: 执行脚本
     *
     * @param cmds
     * @return int
     * @author Hlingoes 2020/2/24
     */
    public int exec(Connection conn, String cmds) throws IOException {
        InputStream stdOut = null;
        InputStream stdErr = null;
        int ret = -1;
        if (null == conn) {
            return ret;
        }
        try {
            Session session = conn.openSession();
            // 建立虚拟终端, 避免环境变量读取不全的问题
            session.requestPTY("bash");
            // 打开一个Shell
            session.startShell();
            stdOut = new StreamGobbler(session.getStdout());
            stdErr = new StreamGobbler(session.getStderr());
            // 准备输入命令
            PrintWriter out = new PrintWriter(session.getStdin());
            // 输入待执行命令
            out.println(cmds);
            out.println("exit");
            // 6. 关闭输入流
            out.close();
            // 7. 等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
            session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, TIME_OUT);
            LOGGER.info(processStream(stdOut, StandardCharsets.UTF_8));
            LOGGER.info(processStream(stdErr, StandardCharsets.UTF_8));
            ret = session.getExitStatus();
            session.close();
        } finally {
            conn.close();
            IOUtils.closeQuietly(stdOut);
            IOUtils.closeQuietly(stdErr);
        }
        return ret;
    }

    /**
     * description: 远程传输单个文件
     *
     * @param conn
     * @param localFile
     * @param remoteDir
     * @return void
     * @author Hlingoes 2020/2/25
     */
    public void transferFile(Connection conn, String localFile, String remoteDir) throws IOException {
        File file = new File(localFile);
        if (file.isDirectory()) {
            return;
        }
        String fileName = file.getName();
        SCPClient sCPClient = conn.createSCPClient();
        SCPOutputStream scpOutputStream = sCPClient.put(fileName, file.length(), remoteDir, "0600");
        scpOutputStream.write(FileUtils.readFileToByteArray(file));
        scpOutputStream.flush();
        scpOutputStream.close();
    }

    /**
     * 传输整个目录
     * P.S: 更好的方式，打成压缩包，传输，然后解压
     *
     * @param conn
     * @param localDir
     * @param remoteDir
     * @throws IOException
     */
    public void transferDirectory(Connection conn, String localDir, String remoteDir) throws IOException {
        File dir = new File(localDir);
        if (!dir.isDirectory()) {
            throw new RuntimeException(localDir + " is not directory");
        }
        String[] files = dir.list();
        for (String file : files) {
            if (file.startsWith(".")) {
                continue;
            }
            String fullName = localDir + "/" + file;
            if (new File(fullName).isDirectory()) {
                String subDir = remoteDir + "/" + file;
                transferDirectory(conn, fullName, subDir);
            } else {
                transferFile(conn, fullName, remoteDir);
            }
        }
    }
}
