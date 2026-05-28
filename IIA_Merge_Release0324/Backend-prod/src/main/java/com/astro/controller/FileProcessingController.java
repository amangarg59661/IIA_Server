package com.astro.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import com.astro.service.FileProcessingService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileProcessingController {

    @Autowired
    private FileProcessingService fileProcessingService;

    @GetMapping("/list")
    public ResponseEntity<?> getFileList() {
        return new ResponseEntity<>(fileProcessingService.fileList(), HttpStatus.OK);
    }

  /*  @GetMapping(value = "/download/{fileType}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadFile(@PathVariable(value = "fileType") String fileType, @PathVariable(value = "fileName") String fileName) {
        Resource file = fileProcessingService.downloadFile(fileType, fileName);
        if (file == null) {
            return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
        }

    }*/
  @GetMapping(value = "/download/{fileType}/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<?> downloadFile(@PathVariable(value = "fileType") String fileType, @PathVariable(value = "fileName") String fileName) {
      Resource file = fileProcessingService.downloadFile(fileType, fileName);
      if (file == null) {
          return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.NOT_FOUND);
      } else {
          return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
      }

  }


    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam String fileType, @RequestParam(name = "file") MultipartFile file) {
        Map<String, String> map = new HashMap<>();
        String fileName = fileProcessingService.uploadFile(fileType, file);
        map.put("fileName", fileName);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(map), HttpStatus.CREATED);
    }
 
@DeleteMapping("/delete")
public ResponseEntity<?> deleteFile(@RequestParam String fileType, @RequestParam String fileName) {
    fileProcessingService.deleteFile(fileType, fileName);
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(), HttpStatus.OK);
}
/*

    @GetMapping(value = "/view/{fileType}/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileType") String fileType,
                                             @PathVariable("fileName") String fileName) {
        Resource file = fileProcessingService.downloadFile(fileType, fileName);

        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String contentType;
        try {
            contentType = Files.probeContentType(Path.of(file.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        } catch (IOException e) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
    */
  /*  @GetMapping(value = "/view/{fileType}/{fileName}")
    public ResponseEntity<?> viewFile(@PathVariable("fileType") String fileType,
                                             @PathVariable("fileName") String fileName) {
        Resource file = fileProcessingService.viewFile(fileType, fileName);
        if (file == null) {
            return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(), HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(file);
        }
    }

   */

    @GetMapping(value = "/view/{fileType}/{fileName:.+}")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileType") String fileType,
                                             @PathVariable("fileName") String fileName) {
        Resource file = fileProcessingService.viewFile(fileType, fileName);

        if (file == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // // Get content type from service
        // String contentType = fileProcessingService.getContentType(fileName);

        // Use decoded filename for accurate content-type detection
        String decodedFileName = fileProcessingService.getDecodedFileName(fileName);
        String contentType = fileProcessingService.getContentType(decodedFileName);

        // Inline for PDF/images so browser can display them; attachment for Office docs
        String disposition = (contentType.startsWith("image/") || contentType.equals(MediaType.APPLICATION_PDF_VALUE))
                ? "inline; filename=\"" + decodedFileName + "\""
                : "attachment; filename=\"" + decodedFileName + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,disposition)
                .body(file);
    }

}
