package com.techlabs.app.controller;

import com.techlabs.app.entity.FileItem;
import com.techlabs.app.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/fortuneLife/file")
public class FileController {

    @Autowired
    private FileService fileService;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping(value = { "/upload" }, consumes = { "multipart/form-data" })
    public ResponseEntity<FileItem> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("Uploading a file");
        FileItem fileItem = fileService.saveFileAndReturnItem(file);
        return new ResponseEntity<>(fileItem, HttpStatus.CREATED);
    }

    @GetMapping("/view/{name}")
    public ResponseEntity<?> viewFileByName(@PathVariable String name) {
        logger.info("Fetching file with name: {}", name);
        FileItem fileItem = fileService.getFileByUUIDName(name);
        // Redirect to Cloudinary URL
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(fileItem.getLocation()))
                .build();
    }
}
