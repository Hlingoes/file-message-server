package cn.henry.study.base;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

/**
 * description: 默认使用RestTemplate处理http下载服务
 *
 * @author Hlingoes
 * @date 2019/12/21 23:59
 */
@Service
public class DefaultFileService implements FileService {
    @Override
    public File download(String fileName, String localPath) throws IOException {
        return null;
    }

    @Override
    public boolean upload(String fileName, File file) throws IOException {
        return false;
    }

    @Override
    public boolean upload(String fileName, InputStream inputStream) throws IOException {
        return false;
    }

    @Override
    public boolean upload(String fileName, byte[] bytes) throws IOException {
        return false;
    }

    @Override
    public boolean delete(String fileName) throws IOException {
        return false;
    }

    @Override
    public List<String> filter(String filePath, Pattern pattern) throws IOException {
        return null;
    }

    @Override
    public Class<?> getEntityClazz() {
        return null;
    }
}
