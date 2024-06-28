package com.imooc.controller;

import com.imooc.grace.result.GraceJSONResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RequestMapping("/file")
@RestController
public class FileController {

    /**
     * 用户上传头像的接口
     *
     * @return
     */
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam("file") MultipartFile file,
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

        // 生成图片被访问地址
        String userFaceUrl = "static/face/" + newFileName;
        return GraceJSONResult.ok(userFaceUrl);
    }
}
