package com.file.controller;

import com.file.util.Constant;
import com.file.util.DateUtils;
import com.file.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Description:
 *
 * @author jason
 * @date 2019-02-02 17:25
 */
@Controller
public class FileUploadController extends AbstractController{

    /**
     * 上传文件保存的路径
     */
    protected String uploadPath;

    /**
     * 存放路径上下文
     */
    protected String fileContextPath;

    /**
     * 上传文件类型
     */
    protected String fileType;

    /**
     * 上传文件名称
     */
    protected String fileName;

    @RequestMapping("/file")
    public String file() {
        return "/file";
    }

    /**
     * 单个文件上传
     *
     * @param file
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                /*
                 * 这段代码执行完毕之后，图片上传到了工程的跟路径
                 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如：
                 * 1、文件大小的限制； 2、文件名； 3、文件路径; 4、文件格式;
                 */
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(constant.uploadPath, file.getOriginalFilename())));
                out.write(file.getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "上传失败," + e.getMessage();
            }
            return "上传成功";
        } else {
            return "上传失败，因为文件是空的.";
        }
    }

    @RequestMapping("/mutifile")
    public String mutifile() {
        return "/mutifile";
    }

    /**
     * 多个文件上传
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileUpload(Integer uploadType, HttpServletRequest request) {
        // 存放路径上下文
        fileContextPath = constant.fileContextPath;
        // 上传文件的存储路径（服务器文件系统上的绝对文件路径）
//        String uploadFilePath = request.getSession().getServletContext().getRealPath("upload" );
        // 获取表单中的所有文件
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile file = null;
        BufferedOutputStream stream = null;
        for (int i = 0; i < files.size(); ++i) {
            file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    fileName = file.getOriginalFilename();
                    // 自定义的文件名称
                    String trueFileName = getTrueFileName(fileName, uploadType);
                    byte[] bytes = file.getBytes();
//                    stream = new BufferedOutputStream(new FileOutputStream(new File(uploadFilePath, file.getOriginalFilename())));
                    stream = new BufferedOutputStream(new FileOutputStream(new File(constant.uploadPath, trueFileName + fileName)));
                    stream.write(bytes);
                    stream.close();
                } catch (Exception e) {
                    stream = null;
                    return "You failed to upload " + i + " => " + e.getMessage();
                }
            } else {
                return "You failed to upload " + i + ", because the file was empty.上传失败";
            }
        }
        return "batchUpload successful=》批量上传成功";
    }

    /**
     * 自定义上传文件的名称
     *
     * @param fileName   上传文件的名称
     * @param uploadType 上传文件类型(不同保存的文件夹就不同)
     * @return String
     */
    private String getTrueFileName(String fileName, Integer uploadType) {
        StringBuffer bf = new StringBuffer();
        if (null == uploadType) {
            return bf.append(String.valueOf(System.currentTimeMillis()) + fileName).toString();
        } else if (uploadType == Constant.UploadType.adminAvatar.getValue()) {
            bf.append("adminAvatar" + File.separator);
        } else if (uploadType == Constant.UploadType.other.getValue()) {
            bf.append("other" + File.separator);
        } else {

        }
        return bf.append(DateUtils.format(new Date(), Constant.uploadSavePathFormat) + File.separator
                + String.valueOf(System.currentTimeMillis()) + fileName).toString();
    }

}
