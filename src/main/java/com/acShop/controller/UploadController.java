package com.acShop.controller;

import com.acShop.pojo.Result;
import com.acShop.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
@Tag(name = "Upload", description = "Image upload endpoints")
public class UploadController {

    private final S3Service s3Service;

    public UploadController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping("/image")
    @Operation(summary = "Upload an image to S3",
               description = "Accepts jpg/png/webp/gif, max 5MB. Returns the public S3 URL.")
    public Result upload(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return Result.error("No file selected.");
        }
        String url = s3Service.uploadImage(file);
        return Result.success(url);
    }
}
