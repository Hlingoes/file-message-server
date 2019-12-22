package cn.henry.study.appication;

import cn.henry.study.base.DefaultFileService;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;
import sun.net.ftp.impl.FtpClient;

/**
 * description: 使用apache common-net实现的文件操作
 *
 * @author Hlingoes
 * @date 2019/12/21 18:09
 */
@Service
public class FtpService extends DefaultFileService {

    @Override
    public Class<?> getEntityClazz() {
        return FTPClient.class;
    }

}
