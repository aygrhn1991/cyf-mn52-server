package com.cyf.mn52.suit.util;

import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class UtilFile {
    public static File multipartFileToFile(MultipartFile mFile) throws Exception {
        File file = new File(mFile.getOriginalFilename());
        FileUtils.copyInputStreamToFile(mFile.getInputStream(), file);
        return file;
    }
}
