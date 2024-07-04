package com.imooc.controller;

import com.imooc.MinIOConfig;
import com.imooc.MinIOUtils;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.bo.Base64FileBO;
import com.imooc.utils.Base64ToFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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

    /**
     * 用户更换头像接口，接收前端传递的 base64 字符串
     * 将 这个字符串转换成 临时文件存储在本地
     * 上传到 minio 所在的服务器上
     *
     * @param base64FileBO
     * @return
     */
    @PostMapping("/uploadAdminFace")
    public GraceJSONResult uploadAdminFace(@RequestBody @Valid Base64FileBO base64FileBO) throws Exception {

        //  获取文件生成的 base64 字符串
        String base64 = base64FileBO.getBase64File();

        String suffix = ".png";    //   文件统一使用 png 格式作为文件后缀
        //  随机生成文件名称
        String fileName = UUID.randomUUID().toString();
        //  拼接对象存储名称
        String objectName = fileName + suffix;

        //  将上传的文件先存储在本地临时目录中
        String rootPath = "F:\\personal_codes_for_git\\imgs" + File.separator;
        //  拼接 对象名称和临时文件存储库路径 组成存储在本地的文件路径
        String filePath = rootPath + File.separator + "adminFace" + File.separator + objectName;

        //  将前端传递的 base64 字符串转换成 临时文件存储在本地
        Base64ToFile.Base64ToFile(base64, filePath);

        //  调用 minio 工具类，将本地的临时文件上传到对象存储中的指定位置
        MinIOUtils.uploadFile(minIOConfig.getBucketName(), objectName, filePath);

        //  构建静态资源文件访问的 url 返回给前端
        String imageUrl = minIOConfig.getFileHost()
                + "/" +
                minIOConfig.getBucketName()
                + "/" +
                objectName;

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传企业 logo 接口
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadLogo")
    public GraceJSONResult uploadLogo(@RequestParam("file") MultipartFile file) throws Exception {

        //  获取文件的 原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        //  拼接 向 minio 中上传的文件路径
        filename = "company/logo/" + dealFilename("", filename);

        //   向 minio 服务器中 上传文件返回对应的 url
        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传企业营业执照（也相当于上传图片）
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadBizLicense")
    public GraceJSONResult uploadBizLicense(@RequestParam("file") MultipartFile file) throws Exception {

        //  获取文件原名
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename))
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);

        //  拼接上传文件的路径
        filename = "company/bizLicense/" + dealFilename("", filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传企业认证授权证书接口
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadAuthLetter")
    public GraceJSONResult uploadAuthLetter(@RequestParam("file") MultipartFile file) throws Exception {

        //  获取文件的原始名称
        String filename = file.getOriginalFilename();
        if (StringUtils.isBlank(filename)) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        //  拼接新的文件名称
        filename = "/company/AuthLetter/" + dealFilename("", filename);

        String imageUrl = MinIOUtils.uploadFile(minIOConfig.getBucketName(),
                filename,
                file.getInputStream(),
                true);

        return GraceJSONResult.ok(imageUrl);
    }

    /**
     * 上传文件的文件名处理方法
     * 通常情况下是唯一的 companyId + 文件名后缀
     *
     * @param companyId
     * @param fileName
     * @return
     */
    private String dealFilename(String companyId, String fileName) {

        //  获取文件的后缀名称
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //  获取文件的名称（不含后缀）
        String fname = fileName.substring(0, fileName.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString();

        fileName = fname + "-" + uuid + suffixName;
        return fileName;
    }
}
