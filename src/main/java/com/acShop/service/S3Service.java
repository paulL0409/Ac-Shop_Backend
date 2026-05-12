package com.acShop.service;

import com.acShop.config.S3Properties;
import com.acShop.exception.InvalidFileTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class S3Service {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final S3Client s3Client;
    private final S3Properties props;

    public S3Service(S3Client s3Client, S3Properties props) {
        this.s3Client = s3Client;
        this.props = props;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException(
                    "Unsupported file type. Allowed: jpg, png, webp, gif");
        }

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";

        String key = "images/" + UUID.randomUUID() + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(props.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        return "https://" + props.getBucketName()
                + ".s3." + props.getRegion()
                + ".amazonaws.com/" + key;
    }
}
