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
 * description: 连接远程服务器的工具类
 *
 * @author Hlingoes
 * @date 2020/2/25 22:47
 */
public class ShellExecutor {
    private static Logger logger = LoggerFactory.getLogger(ShellExecutor.class);

    private String charset = Charset.defaultCharset().toString();

    private static final int TIME_OUT = 1000 * 5 * 60;
    private Connection connection;
    private Session session;
    private BufferedReader stdout;
    public PrintWriter printWriter;
    private BufferedReader stderr;

    /**
     * description: 登录获取连接
     *
     * @param
     * @return boolean
     * @author Hlingoes 2020/2/24
     */
    public void initConnection(String host, String user, String password) throws IOException {
        connection = new Connection(host);
        connection.connect();
        if (!connection.authenticateWithPassword(user, password)) {
            throw new RuntimeException("Authentication failed. Please check hostName, userName and password");
        }
    }

    /**
     * description: 获取服务器的session连接
     *
     * @param hostName
     * @param userName
     * @param password
     * @return void
     * @author Hlingoes 2020/2/26
     */
    public void initSession(String hostName, String userName, String password) throws IOException {
        //根据主机名先获取一个远程连接
        connection = new Connection(hostName);
        //发起连接
        connection.connect();
        //认证账号密码
        boolean authenticateWithPassword = connection.authenticateWithPassword(userName, password);
        //如果账号密码有误抛出异常
        if (!authenticateWithPassword) {
            throw new RuntimeException("Authentication failed. Please check hostName, userName and password");
        }
        //开启一个会话
        session = connection.openSession();
        session.requestDumbPTY();
        session.startShell();
        //获取标准输出
        stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), charset));
        //获取标准错误输出
        stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), charset));
        //获取标准输入
        printWriter = new PrintWriter(session.getStdin());
    }

    /**
     * description: 执行指令
     *
     * @param cmd
     * @return int
     * @author Hlingoes 2020/2/24
     */
    public int execCommand(String cmd) throws IOException {
        int ret = -1;
        try {
            Session session = connection.openSession();
            // 建立虚拟终端, 避免环境变量读取不全的问题
            session.requestPTY("bash");
            // 打开一个Shell
            session.startShell();
            //获取标准输出
            stdout = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()), StandardCharsets.UTF_8));
            //获取标准错误输出
            stderr = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()), StandardCharsets.UTF_8));
            //获取标准输入
            printWriter = new PrintWriter(session.getStdin());
            // 输入待执行命令
            printWriter.println(cmd);
            printWriter.println("exit");
            // 6. 关闭输入流
            printWriter.close();
            // 7. 等待，除非1.连接关闭；2.输出数据传送完毕；3.进程状态为退出；4.超时
            session.waitForCondition(ChannelCondition.CLOSED | ChannelCondition.EOF | ChannelCondition.EXIT_STATUS, TIME_OUT);
            printProcessStream(stdout);
            printProcessStream(stderr);
            ret = session.getExitStatus();
        } finally {
            close();
        }
        return ret;
    }

    /**
     * description: 关闭资源方法
     *
     * @param
     * @return void
     * @author Hlingoes 2020/2/25
     */
    public void close() {
        IOUtils.closeQuietly(stdout);
        IOUtils.closeQuietly(stderr);
        IOUtils.closeQuietly(printWriter);
        if (null != session) {
            session.close();
        }
        if (null != connection) {
            connection.close();
        }
    }

    /**
     * description: 远程传输单个文件
     *
     * @param localFile
     * @param remoteDir
     * @return void
     * @author Hlingoes 2020/2/25
     */
    public void transferFile(String localFile, String remoteDir) throws IOException {
        File file = new File(localFile);
        if (file.isDirectory()) {
            return;
        }
        String fileName = file.getName();
        SCPClient scpClient = connection.createSCPClient();
        SCPOutputStream scpOutputStream = scpClient.put(fileName, file.length(), remoteDir, "0600");
        scpOutputStream.write(FileUtils.readFileToByteArray(file));
        scpOutputStream.flush();
        scpOutputStream.close();
        close();
    }

    /**
     * 传输整个目录
     * P.S: 更好的方式，打成压缩包，传输，然后解压
     *
     * @param localDir
     * @param remoteDir
     * @throws IOException
     */
    public void transferDirectory(String localDir, String remoteDir) throws IOException {
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
                transferDirectory(fullName, subDir);
            } else {
                transferFile(fullName, remoteDir);
            }
        }
    }

    /**
     * description: 获取数据流
     *
     * @param reader
     * @return java.lang.String
     * @author Hlingoes 2020/2/24
     */
    private String printProcessStream(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            logger.info(line);
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
}
