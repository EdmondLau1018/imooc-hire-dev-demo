package com.imooc.controller;

import com.imooc.MinIOConfig;
import com.imooc.MinIOUtils;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RequestMapping("/file")
@RestController
public class FileController {

    public static final String HOST = "http://192.168.32.1:8000/";

    private final MinIOConfig minIOConfig;

    public FileController(MinIOConfig minIOConfig) {
        this.minIOConfig = minIOConfig;
    }

    /**
     * 用户上传头像的接口
     *
     * @return
     */
    @PostMapping("/uploadFaceLocal")
    public GraceJSONResult uploadFaceLocal(@RequestParam("file") MultipartFile file,
                                           @RequestParam("userFileId") String userFileId,
                                           HttpServletRequest request) throws IOException {

        //  获取文件原名
        String originalFilename = file.getOriginalFilename();
        //  获取文件后缀的名称 取最后一个 . 之后的内容
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //  生成新的用户头像图片名称
        String newFileName = userFileId + suffixName;
        //  设置文件存储路径
        String rootPath = "\\F:\\personal_codes_for_git\\imgs" + File.separator;
        //  设置文件上传的全路径
        String filePath = rootPath + File.separator + "\\face" + File.separator + newFileName;
        //  根据全路径创建文件对象
        File newFile = new File(filePath);
        //  判断文件对象父路径是否存在，如果不存在创建文件对象父路径
        if (!newFile.getParentFile().exists()) {
            //  递归创建目录
            newFile.getParentFile().mkdirs();
        }
        //  将用户传递在服务器内存中的文件对象写入到磁盘指定位置
        file.transferTo(newFile);

        // 生成图片被访问地址（上传文件后生成文件的静态资源访问地址）
        String userFaceUrl = HOST + "static/face/" + newFileName;
        return GraceJSONResult.ok(userFaceUrl);
    }

    /**
     * minio 接收用户上传头像
     *
     * @param file
     * @param userFileId
     * @return
     * @throws IOException
     */
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
                                      @RequestParam("userFileId") String userFileId) throws Exception {

        if (StringUtils.isBlank(userFileId)) {
            //  上传 用户头像的 id 为空 ，抛出文件上传异常错误
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        //  获得文件的原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        //  构建新的文件名称  在 windows 中 File.separator 代表的是 \ (反斜杠)
        //  这里想的是将用户 id 作为目录存储子在桶中 所以这里相当于在拼接图片在桶中的存储路径
        filename = userFileId + "/" + filename;
        //  使用 构建的 minio 工具类上传文件的代码
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), filename, file.getInputStream());

        String imageUrl = minIOConfig.getFileHost()
                + "/" +
                minIOConfig.getBucketName()
                + "/" +
                filename;

        return GraceJSONResult.ok(imageUrl);
    }
}
