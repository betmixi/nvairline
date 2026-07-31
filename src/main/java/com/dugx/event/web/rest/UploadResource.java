package com.dugx.event.web.rest;

import com.dugx.event.config.ApplicationProperties;
import com.dugx.event.web.rest.errors.BadRequestAlertException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Nhan anh nguoi dung tai len tu may (banner su kien...), luu vao dia va tra
 * ve URL de gan vao cac truong kieu String nhu Event.banner.
 */
@RestController
@RequestMapping("/api/uploads")
public class UploadResource {

    private static final Logger LOG = LoggerFactory.getLogger(UploadResource.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private static final Map<String, String> CONTENT_TYPES = Map.of(
        "jpg",
        "image/jpeg",
        "jpeg",
        "image/jpeg",
        "png",
        "image/png",
        "gif",
        "image/gif",
        "webp",
        "image/webp"
    );

    private final ApplicationProperties applicationProperties;

    public UploadResource(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @PostMapping("/images")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestAlertException("File rong", "upload", "fileempty");
        }

        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestAlertException("Chi chap nhan anh JPG, PNG, GIF hoac WEBP", "upload", "invalidtype");
        }

        String filename = UUID.randomUUID() + "." + extension;

        try {
            Path uploadDir = Paths.get(applicationProperties.getUpload().getDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);

            Path target = uploadDir.resolve(filename);
            file.transferTo(target);

            LOG.debug("Saved uploaded image to {}", target);

            return ResponseEntity.ok(Map.of("url", "/api/uploads/images/" + filename));
        } catch (IOException e) {
            throw new BadRequestAlertException("Khong the luu anh", "upload", "saveerror");
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        // Chan path traversal: chi cho phep ten file da duoc sinh boi server (uuid.ext)
        if (filename.contains("/") || filename.contains("\\") || filename.contains("..")) {
            return ResponseEntity.badRequest().build();
        }

        String extension = extensionOf(filename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return ResponseEntity.badRequest().build();
        }

        Path uploadDir = Paths.get(applicationProperties.getUpload().getDir()).toAbsolutePath().normalize();
        Path target = uploadDir.resolve(filename).normalize();

        if (!target.startsWith(uploadDir) || !Files.exists(target)) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaType.parseMediaType(CONTENT_TYPES.getOrDefault(extension, "application/octet-stream"));

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
            .body(new FileSystemResource(target));
    }

    private String extensionOf(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
