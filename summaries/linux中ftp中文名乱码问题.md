##### 问题触发环境
##### 1. java中使用org.apache.commons.net.ftp.FTPClient包
##### 2. 通过chrome浏览器的file标签上传文件
##### 3. 在windows上部署的FileZilla服务上传的文件名正常显示，在linux上的vsftpd服务文件名显示乱码
##### 4. 直接chrome浏览器访问linux的ftp目录(chrome的默认编码是UTF-8)，正常显示
##### 5. 乱码出现后，尝试了各种方式编码处理，造成了环境的各种不可追溯
##### 解决过程
##### 1. 查询资料:FTP协议规定文件名编码为iso-8859-1，所以上传的文件目录或文件名需要转码
##### 2. 参考FileZilla的流程，会向FTP服务器发送OPTS UTF8 ON命令，开启服务器对UTF-8的支持。
##### 3. 仿照FileZilla的方式，可向服务器发送该指令，如果服务器支持UTF-8则使用UTF-8，否则使用本地编码(GBK)处理中文名
##### 4. 起作用的java关键代码
````/** 本地字符编码 **/ 
private static String LOCAL_CHARSET = "GBK";

// FTP协议中规定的文件名编码为: iso-8859-1
private static String FTP_CHARSET = "ISO-8859-1";

...
if (ftpClient.login(user, pwd)) {
	if (FTPReply.isPositiveCompletion(ftpClient.sendCommand("OPTS UTF8", "ON"))) {
		// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8，否则使用本地编码(GBK)
		LOCAL_CHARSET = "UTF-8";
	}
	ftpClient.setControlEncoding(LOCAL_CHARSET);
	// 设置被动模式
	ftpClient.setLocalPassiveMode();
	...
}

// 上传的时候使用
ftpClient.storeFile(encodingServerPath(fileName), inputStream);

/**
 * 编码文件路径
 */
private static String encodingServerPath(String path) throws UnsupportedEncodingException {
	// FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码，replace处理文件路径
	return new String(path.replace("\\", "/").replaceAll("//", "/").getBytes(LOCAL_CHARSET), FTP_CHARSET);
}

// 下载的时候使用
ftpClient.retrieveFile(encodingServerPath(fileName), outputStream);

// 打印ftp工作目录时需要切换转码
System.out.println("当前工作目录："+ new String(ftp.printWorkingDirectory().getBytes("iso-8859-1"), LOCAL_CHARSET));

````

###### 个人理解的转码和解码过程如下：

````
filePath 的原始编码: origin_charset => page
java后台: new String(filePath.getBytes(Local_CHARSET), "ISO-8859-1") => step_1
FTP Protocol: new String(filePath.getBytes("ISO-8859-1"), FTP_CHARSET) => step_2
FTP 服务器的默认编码: new String(filePath.getBytes(FTP_CHARSET), server_charset) => end
if(Local_CHARSET == FTP_CHARSET) upload正常, else upload异常(无法创建和切换目录)
if(origin_charset == server_charset) 文件名在FTP服务器上显示正常, else 文件在FTP服务器显示乱码，download异常
如果filePath的编码与上传是的编码不一致，download异常
````
###### web项目开发中出现乱码，要从传值开始分析编码，逐步排查,确保编码一致。要注意浏览器和某些方法的默认行为(场景较多，需要时查询即可,不在此处罗列). 

