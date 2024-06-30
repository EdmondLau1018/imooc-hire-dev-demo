package com.imooc.utils;

import com.imooc.exceptions.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import sun.misc.BASE64Decoder;

import java.io.*;

public class Base64ToFile {
    // 传入base64编码字符以及保存路径
    public static void Base64ToFile(String base64, String filePath) throws IOException {
        // base64编码字符必须不能包含base64的前缀，否则会报错
        // filePath需要为具体到文件的名称和格式，如111.txt
        // 文件路径需要双斜杠转义，如:  D:\\files\\111.txt
        if (base64 == null && filePath == null) {
//            return "生成文件失败，未传入参数!";
            GraceException.displayException(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }
        // 判断是否base64是否包含data:image/png;base64等前缀，如果有则去除
        if (base64.contains("data:image/png;base64")) {
            base64 = base64.substring(22);
            System.out.println("包含png"+base64);
        }
        if (base64.contains("data:image/jpeg;base64")) {
            base64 = base64.substring(23);
            System.out.println("包含jpeg"+base64);
        }
        if (base64.contains("data:application/pdf;base64")) {
            base64 = base64.substring(28);
            System.out.println("包含pdf"+base64);
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(base64);
        for (int i = 0; i<bytes.length; ++i) {
            // 调整异常数据
            if (bytes[i] < 0) {
                bytes[i] +=256;
            }
        }
        OutputStream outputStream = null;
        InputStream inputStream = new ByteArrayInputStream(bytes);
        // 此处判断文件夹是否存在，不存在则创建除文件外的父级文件夹
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("上级目录"+file.getParentFile());
            file.getParentFile().mkdirs();
        }
        try {
            // 生成指定格式文件
            outputStream = new FileOutputStream(filePath);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
//        return "生成文件成功!";
    }
}

