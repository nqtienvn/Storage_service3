package com.tien.storageservice_3.controller;

import com.tien.storageservice_3.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
public class CloudinaryControllerDebug {
    private final CloudinaryService cloudinaryService;

    //Upload 1 file
    @PostMapping()
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("typeOfFile") String typeOfFile) {
        try {
            String url = cloudinaryService.uploadFile(file, typeOfFile);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload thất bại: " + e.getMessage());
        }
    }
    @PostMapping("/multi-file")
    public ResponseEntity<?> uploadMultiFile(@RequestParam("files") MultipartFile[] files,
                                             @RequestParam("typeOfFile") String typeOfFile) {
        try {
            List<String> fileUrls = new ArrayList<>();
            for(MultipartFile file: files) {
                String url = cloudinaryService.uploadFile(file, typeOfFile);
                fileUrls.add(url);
            }
            return ResponseEntity.ok(fileUrls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload thất bại: " + e.getMessage());
        }
    }

    // Delete file theo publicId (không phải URL)
    @DeleteMapping()
    public ResponseEntity<?> deleteFile(@RequestParam String publicId) {
        try {
            String result = cloudinaryService.deleteFile(publicId);
            return ResponseEntity.ok(result); // Trả "ok" nếu xóa thành công
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Xóa thất bại: " + e.getMessage());
        }
    }

    //Update file (xóa ảnh cũ + upload ảnh mới)
    @PutMapping()
    public ResponseEntity<?> updateFile(@RequestParam("oldPublicId") String oldPublicId,
                                        @RequestParam("file") MultipartFile newFile,
                                        @RequestParam(value = "typeOfFile", required = false) String typeOfFile) {
        try {
            String newUrl = cloudinaryService.updateFile(oldPublicId, newFile, typeOfFile);
            return ResponseEntity.ok(newUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Cập nhật thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/file-publicId")
    public ResponseEntity<?> getFile(@RequestParam String publicId,
                                     @RequestParam(required = false) Integer width,
                                     @RequestParam(required = false) Integer height,
                                     @RequestParam(required = false) String cropMode,
                                     @RequestParam(required = false) String ratio) {
        if (ratio == null) {
            return ResponseEntity
                    .ok(cloudinaryService
                            .getTransformedImageUrl(publicId, width, height, cropMode));
        } else return ResponseEntity
                .ok(cloudinaryService
                        .getImageByRatio(publicId, ratio));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam(required = false) String fileName,
                                    @RequestParam(required = false) String typeOfFile,
                                    @RequestParam(required = false) String createDate,
                                    @RequestParam(required = false) String modifyDate,
                                    @RequestParam(required = false) String owner,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        Instant createDateInstant = null;
        if (createDate != null && !createDate.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime localDateTime = LocalDateTime.parse(createDate, formatter);
            createDateInstant = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        }
        Instant modifyDateInstant = null;
        if (modifyDate != null && !modifyDate.isBlank()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
            LocalDateTime localDateTime = LocalDateTime.parse(modifyDate, formatter);
            modifyDateInstant = localDateTime.atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toInstant();
        }
        return ResponseEntity.ok(cloudinaryService.search(fileName, typeOfFile, createDateInstant, modifyDateInstant, owner, page, size));
    }
}
