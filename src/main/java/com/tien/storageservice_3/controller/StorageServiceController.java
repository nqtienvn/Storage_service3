package com.tien.storageservice_3.controller;

import com.tien.storageservice_3.dto.response.ApiResponse;
import com.tien.storageservice_3.dto.response.FIleS2Response;
import com.tien.storageservice_3.exception.AppException;
import com.tien.storageservice_3.exception.ErrorCode;
import com.tien.storageservice_3.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.tien.storageservice_3.service.DateStringToInstantService.getInstant;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
@Tag(name = "Storage Controller")
public class StorageServiceController {
    private final CloudinaryService cloudinaryService;

    //Upload 1 file
    @Operation(summary = "upload a File",
            description = "upload a file and manage by cloudinary")
    @PostMapping()
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file,
                                          @RequestParam("typeOfFile") String typeOfFile) {
        try {
            String url = cloudinaryService.uploadFile(file, typeOfFile);
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("upload file successfully")
                    .result(url).build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    @Operation(summary = "upload multi File",
            description = "upload multi file and manage by cloudinary")
    @PostMapping("/multi-file")
    public ApiResponse<List<String>> uploadMultiFile(@RequestParam("files") MultipartFile[] files,
                                                     @RequestParam("typeOfFile") String typeOfFile) {
        try {
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String url = cloudinaryService.uploadFile(file, typeOfFile);
                fileUrls.add(url);
            }
            return ApiResponse.<List<String>>builder()
                    .code(200)
                    .message("update successfully")
                    .result(fileUrls)
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    // Delete file theo publicId (không phải URL)
    @Operation(summary = "delete a File",
            description = "delete a file in cloudinary and my database")
    @DeleteMapping()
    public ApiResponse<String> deleteFile(@RequestParam String publicId) {
        try {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("delete file successfully")
                    .result(cloudinaryService.deleteFile(publicId))
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_DELETE_FILE);
        }
    }

    //Update file (xóa ảnh cũ + upload ảnh mới)
    @Operation(summary = "update a File",
            description = "update a file and manage by cloudinary")
    @PutMapping()
    public ApiResponse<String> updateFile(@RequestParam("oldPublicId") String oldPublicId,
                                          @RequestParam("file") MultipartFile newFile,
                                          @RequestParam(value = "typeOfFile", required = false) String typeOfFile) {
        try {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("update file successfully")
                    .result(cloudinaryService.updateFile(oldPublicId, newFile, typeOfFile))
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    @Operation(summary = "get a File",
            description = "get a file and manage by cloudinary with radio or width, height")
    @GetMapping("/file-publicId")
    public ApiResponse<String> getFile(@RequestParam String publicId,
                                       @RequestParam(required = false) Integer width,
                                       @RequestParam(required = false) Integer height,
                                       @RequestParam(required = false) String cropMode,
                                       @RequestParam(required = false) String ratio) {
        if (ratio == null) {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("get file successfully")
                    .result(cloudinaryService
                            .getTransformedImageUrl(publicId, width, height, cropMode))
                    .build();
        } else return ApiResponse.<String>builder()
                .code(200)
                .message("get file successfully")
                .result(cloudinaryService
                        .getImageByRatio(publicId, ratio))
                .build();
    }

    @Operation(summary = "filter File",
            description = "filter file with filename, type, create date, modify date, owner")
    @GetMapping("/filter")
    public ApiResponse<Page<FIleS2Response>> filter(@RequestParam(required = false) String fileName,
                                                    @RequestParam(required = false) String typeOfFile,
                                                    @RequestParam(required = false) String createDate,
                                                    @RequestParam(required = false) String modifyDate,
                                                    @RequestParam(required = false) String owner,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size) {
        return ApiResponse.<Page<FIleS2Response>>builder()
                .code(200)
                .message("filter file successfully")
                .result(cloudinaryService.search(fileName, typeOfFile, getInstant(createDate), getInstant(modifyDate), owner, page, size))
                .build();
    }
}
