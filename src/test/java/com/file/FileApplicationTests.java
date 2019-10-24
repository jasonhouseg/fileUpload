package com.file;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileApplicationTests {

    /**
     * 上传到文件服务器
     * @author: yaco
     * @Date  : 2019年7月18日 下午4:26:57
     */
    @Test
    public static String uploadFile(byte[] file, String filePath, String fileName) throws Exception {
            File targetFile = new File(filePath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            Client client = new Client();
            WebResource resource = client.resource(filePath + fileName);
            resource.put(String.class, file);
            return filePath + fileName;
    }

}

