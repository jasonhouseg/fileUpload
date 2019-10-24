package com.file.controller;

import com.file.util.Constant;
import com.file.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description: 这段代码执行完毕之后，图片上传到了工程的根路径
 * 这里只是简单一个例子,请自行参考，融入到实际中可能需要大家自己做一些思考，比如：
 * 1、文件大小的限制； 2、文件名； 3、文件路径; 4、文件格式;
 *
 * @author jason
 * @date 2019-02-02 17:25
 */
@Controller
public class FileUploadAndDownController extends AbstractController {

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
                // 验证上传文件的合法性
                /*boolean isValid = CheckoutFileType.getUpFilelegitimacyFlag(file, ".jpg.gif.png.jpeg");
                if (!isValid) {
                    logger.info("上传图片不合法");
                    return "上传图片不合法";
                }*/
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
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch/upload", method = RequestMethod.POST)
    @ResponseBody
    public String handleFileBatchUpload(Integer uploadType, HttpServletRequest request) {
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
     * 支持一个或多个文件上传，按年月创建目录
     * @param request
     * @return
     */
    @RequestMapping(value = "/batch/uploadNew", method = RequestMethod.POST)
    @ResponseBody
    public List<String> handleFileBatchUploadNew(HttpServletRequest request) {
        //获取上传路径
        String uploadFilePath = constant.uploadPath;
        // 上传文件的存储路径（服务器文件系统上的绝对文件路径）
//        String uploadFilePath = request.getSession().getServletContext().getRealPath("upload" );
//        String uploadFilePath = "/opt/application/upload";
        logger.info("uploadFilePath:==========",uploadFilePath);
        // 存放目录
        String storePath;
        List<String> fIleUrlList = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String ymd = sdf.format(new Date());
        // 获取上传的文件列表
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        MultipartFile multipartFile;
        for (int i = 0; i < files.size(); ++i) {
            // 待上传文件
            multipartFile = files.get(i);
            if (!multipartFile.isEmpty()) {
                try {
                    storePath = uploadFilePath + "/" + ymd + "/";
                    // 创建文件夹
                    File fileDir = new File(storePath);
                    //判断文件路径下的目录是否存在，不存在则创建
                    if (!fileDir.exists()) {
                        fileDir.mkdirs();
                    }
                    fileName = multipartFile.getOriginalFilename();
                    // 构成新文件名
                    String newFileName = getNewFileName(fileName);
                    File uploadFile = new File(storePath + newFileName);
                    // 是否创建文件成功
                    boolean isCreateSuccess = uploadFile.createNewFile();
                    if (isCreateSuccess) {
                        // 上传方法一
                        multipartFile.transferTo(uploadFile);
                        // 上传方法二
//                        FileCopyUtils.copy(multipartFile.getBytes(), uploadFile);
                        storePath += newFileName;
                        logger.info("文件存储路径为：" + storePath);
                        fIleUrlList.add(storePath);
                    }
                } catch (Exception e) {
                    logger.error("failed to upload " + i + " => " + e.getMessage());
                }
            } else {
                logger.error("failed to upload " + i + ", because the file was empty.上传失败");
            }
        }

        return fIleUrlList;
    }

    @PostMapping(value = "/fileUpload")
    public String fileUpload(@RequestParam(value = "file") MultipartFile file, Model model, HttpServletRequest request) {
        if (file.isEmpty()) {
            System.out.println("文件为空空");
        }
        String fileName = file.getOriginalFilename();  // 文件名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));  // 后缀名
        String filePath = "F://upload//"; // 上传后的路径
        fileName = UUID.randomUUID() + suffixName; // 新文件名
        File dest = new File(filePath + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String filename = "/upload/" + fileName;
        logger.info("文件路径：{}", fileName);
        model.addAttribute("filename", filename);
        return "file";
    }

    /**
     * 上传及文件合法性验证
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/api_upload", method = RequestMethod.POST)
    @ResponseBody
    public String upload(HttpServletRequest request) throws IOException {
        //获取上传路径
        String uploadFilePath = constant.uploadPath;
        String storePath;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String ymd = sdf.format(new Date());
        storePath = uploadFilePath + "/" + ymd + "/";
        // 创建文件夹
        File fileDir = new File(storePath);
        //判断文件路径下的目录是否存在，不存在则创建
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 上传文件
            MultipartFile multipartFile = entity.getValue();
            fileName = multipartFile.getOriginalFilename();
            // 构成新文件名
            String newFileName = getNewFileName(fileName);
            File uploadFile = new File(storePath + newFileName);
            // 是否创建文件成功
            boolean isCreateSuccess = uploadFile.createNewFile();
            if (isCreateSuccess) {
                //将文件写入
                multipartFile.transferTo(uploadFile);
                try {
                    // 上传方法一：
                    FileCopyUtils.copy(multipartFile.getBytes(), uploadFile);

                    // 上传方法二：
                    // 使用下面的jar包
//                     FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), uploadFile);
                    storePath += newFileName;
                    logger.info("文件存储路径为：" + storePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return storePath;
    }

    /**
     * 生成新的文件名
     *
     * @param fileName
     * @return
     */
    private String getNewFileName(String fileName) {
        // 返回一个随机UUID
        String uuid = UUID.randomUUID().toString().replaceAll("\\-", "");
        String suffix = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf("."), fileName.length()) : null;
        // 构成新文件名
        String newFileName = uuid + (suffix != null ? suffix : "");
        return newFileName;
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

    /**
     * 文件下载
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/download")
    public String downloadFile(HttpServletRequest request, HttpServletResponse response) {
        // 设置文件名，根据业务需要替换成要下载的文件名
        String fileName = "1550066379578nginx.exenginx.exe";
        if (fileName != null) {
            //设置文件路径
            String realPath = "F:/prodemo/upload";
            File file = new File(realPath, fileName);
            if (file.exists()) {
                // 设置强制下载不打开
                response.setContentType("application/force-download");
                // 设置文件名
                response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    System.out.println("success");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null;
    }

}
