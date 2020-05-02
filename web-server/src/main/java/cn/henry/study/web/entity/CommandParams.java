package cn.henry.study.web.entity;

/**
 * description: 服务器命令参数
 *
 * @author Hlingoes
 * @date 2020/5/2 16:24
 */
public class CommandParams {
    private static final long serialVersionUID = 1L;
    private String ip;
    private String userName;
    private String pass;
    private String checkPass;
    private String fileName;
    private String filePath;
    private String commandLine;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCheckPass() {
        return checkPass;
    }

    public void setCheckPass(String checkPass) {
        this.checkPass = checkPass;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(String commandLine) {
        this.commandLine = commandLine;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "CommandParams{" +
                "ip='" + ip + '\'' +
                ", userName='" + userName + '\'' +
                ", pass='" + pass + '\'' +
                ", checkPass='" + checkPass + '\'' +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", commandLine='" + commandLine + '\'' +
                '}';
    }
}
