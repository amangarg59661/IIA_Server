package com.astro.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.astro.constant.AppConstant;
import com.astro.exception.ErrorDetails;
import com.astro.exception.FilesNotFoundException;
import com.astro.service.FileProcessingService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    @Value("${filePath}")
    private String basePath;

    final List<String> FILE_TYPE_LIST = Arrays.asList("Indent", "Tender", "CP", "INV","Material");

    private static final Logger log = LoggerFactory.getLogger(FileProcessingServiceImpl.class);

    @Override
    public List<String> fileList() {
        File dir = new File(basePath);
        File[] files = dir.listFiles();

        return files != null ? Arrays.stream(files).map(i -> i.getName()).collect(Collectors.toList()) : null;
    }

    @Override
    public String uploadFile(String fileType, MultipartFile multipartFile) {
        System.out.print("uday " +fileType +""+ multipartFile);
        if (!FILE_TYPE_LIST.contains(fileType)) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
        }

        String fileName = System.currentTimeMillis() + "_" + StringUtils.cleanPath(multipartFile.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
            }

            Path path = Path.of(basePath + fileType + "//" + fileName);
            Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_UPLOAD_ERROR, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "File upload error."));
        }

        log.info("Uploading file: {} of type: {}", multipartFile.getOriginalFilename(), fileType);

        return fileName;
    }

    @Override
    public Resource downloadFile(String fileType, String fileName) {

        if (!FILE_TYPE_LIST.contains(fileType)) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
        }

        File dir = new File(basePath + fileType + "//" + fileName);
        try {
            if (dir.exists()) {
                Resource resource = new UrlResource(dir.toURI());
                return resource;
            } else {
                throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, "File not found."));
            }
        } catch (Exception e) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_NOT_FOUND, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "File not found."));
        }
    }

  /*  @Override
    public Resource viewFile(String fileType, String fileName) {
        if (!FILE_TYPE_LIST.contains(fileType)) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
        }

        Resource file = downloadFile(fileType, fileName);

        if (file == null) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_NOT_FOUND,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "File not found."));
        }

        return file;
    }

   */
 /* @Override
  public Resource viewFile(String fileType, String fileName) {
      if (!FILE_TYPE_LIST.contains(fileType)) {
          throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE,
                  AppConstant.ERROR_TYPE_CODE_VALIDATION,
                  AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
      }

      Resource file = downloadFile(fileType, fileName);

      if (file == null) {
          throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_NOT_FOUND,
                  AppConstant.ERROR_TYPE_CODE_VALIDATION,
                  AppConstant.ERROR_TYPE_VALIDATION, "File not found."));
      }

      return file;
  }
*/
/*@Override
public Resource downloadFile(String fileType, String fileName) {
    if (!FILE_TYPE_LIST.contains(fileType)) {
        throw new FilesNotFoundException(new ErrorDetails(
                AppConstant.INVALID_FILE_TYPE,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid File type."));
    }

    // Decode filename if it's Base64-encoded
    String actualFileName = isBase64Encoded(fileName) ? decodeBase64(fileName) : fileName;

    File file = new File(basePath + fileType + "//" + actualFileName);

    try {
        if (file.exists()) {
            return new UrlResource(file.toURI());
        } else {
            throw new FilesNotFoundException(new ErrorDetails(
                    AppConstant.FILE_NOT_FOUND,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "File not found."));
        }
    } catch (Exception e) {
        throw new FilesNotFoundException(new ErrorDetails(
                AppConstant.FILE_NOT_FOUND,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "File not found."));
    }
}*/


    @Override
    public Resource viewFile(String fileType, String fileName) {
        if (!FILE_TYPE_LIST.contains(fileType)) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
        }

        // Decode only if file name is Base64-encoded
       // String actualFileName = isBase64Encoded(fileName) ? decodeBase64(fileName) : fileName;
        String actualFileName = isBase64Encoded(fileName) ? decodeBase64(fileName) : fileName;
        String contentType = getContentType(actualFileName);

        System.out.println("Actual file name: " + actualFileName);

        Resource file = downloadFile(fileType, actualFileName);

        if (file == null) {
            throw new FilesNotFoundException(new ErrorDetails(AppConstant.FILE_NOT_FOUND,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "File not found."));
        }

        return file;
    }

    private boolean isBase64Encoded(String str) {
        try {
            // Try decoding and re-encoding to see if it matches (basic check)
            return Base64.getEncoder().encodeToString(Base64.getDecoder().decode(str)).equals(str);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String decodeBase64(String encoded) {
        try {
            String urlDecoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
            return new String(Base64.getDecoder().decode(urlDecoded));
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode base64 filename", e);
        }
    }


    // Business logic to determine content type
    public String getContentType(String fileName) {
        if (fileName == null) return MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
        // if (fileName.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
            } else if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
        // } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        // } else if (fileName.endsWith(".txt")) {
        } else if (lower.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (lower.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lower.endsWith(".doc")) {
            return "application/msword";
        } else if (lower.endsWith(".txt")) {
            return MediaType.TEXT_PLAIN_VALUE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default
        }
    }
 public String getDecodedFileName(String fileName) {
        return isBase64Encoded(fileName) ? decodeBase64(fileName) : fileName;
    }

@Override
public void deleteFile(String fileType, String fileName) {
    if (!FILE_TYPE_LIST.contains(fileType)) {
        throw new FilesNotFoundException(new ErrorDetails(AppConstant.INVALID_FILE_TYPE,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION, "Invalid File type."));
    }
    try {
        Path path = Path.of(basePath + fileType + "//" + fileName);
        boolean deleted = Files.deleteIfExists(path);
        if (deleted) {
            log.info("Deleted file: {} of type: {}", fileName, fileType);
        } else {
            log.warn("File not found for deletion: {} of type: {}", fileName, fileType);
        }
    } catch (IOException e) {
        log.warn("Could not delete file: {} of type: {}", fileName, fileType);
        // swallow — don't break frontend flow
    }
}
}