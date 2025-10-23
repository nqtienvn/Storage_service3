package com.tien.storageservice_3.controller;

import com.tien.storageservice_3.dto.request.FileFilterRequest;
import com.tien.storageservice_3.dto.request.GetImageRequest;
import com.tien.storageservice_3.dto.request.GetProfileRequest;
import com.tien.storageservice_3.dto.request.UpdateFileRequest;
import com.tien.storageservice_3.dto.response.ApiResponse;
import com.tien.storageservice_3.dto.response.FIleS2Response;
import com.tien.storageservice_3.exception.AppException;
import com.tien.storageservice_3.exception.ErrorCode;
import com.tien.storageservice_3.repository.FileS2Repository;
import com.tien.storageservice_3.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;

import static com.tien.storageservice_3.service.DateStringToInstantService.getInstant;

@RestController
@RequestMapping("/api/cloudinary")
@RequiredArgsConstructor
@Tag(name = "Storage Controller")
public class StorageServiceController {
    private final CloudinaryService cloudinaryService;
    private final FileS2Repository fileS2Repository;

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
    public ApiResponse<String> updateFile(@RequestParam MultipartFile newFile,
                                          @ModelAttribute UpdateFileRequest updateFileRequest) {
        try {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("update file successfully")
                    .result(cloudinaryService.updateFile(updateFileRequest.getOldPublicId(),
                            newFile,
                            updateFileRequest.getTypeOfFile()))
                    .build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    @Operation(summary = "get a File",
            description = "get a file and manage by cloudinary with radio or width, height")
    @GetMapping("/file-publicId")
    public ApiResponse<String> getFile(@ModelAttribute GetImageRequest getImageRequest) {
        if (getImageRequest.getRatio() == null) {
            return ApiResponse.<String>builder()
                    .code(200)
                    .message("get file successfully")
                    .result(cloudinaryService
                            .getTransformedImageUrl(getImageRequest.getPublicId(),
                                    getImageRequest.getWidth(),
                                    getImageRequest.getHeight(),
                                    getImageRequest.getCropMode()))
                    .build();
        } else return ApiResponse.<String>builder()
                .code(200)
                .message("get file successfully")
                .result(cloudinaryService
                        .getImageByRatio(getImageRequest.getPublicId(),
                                getImageRequest.getRatio()))
                .build();
    }

    @Operation(summary = "get my Profile",
            description = "get my profile with ratio, width, height")
    @GetMapping(value = "/profile")
    ResponseEntity<ApiResponse<?>> getProfile(@ModelAttribute GetProfileRequest getProfileRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof Jwt jwtToken) {
            username = jwtToken.getClaimAsString("preferred_username"); //jwt
        } else {
            username = authentication.getName(); //xt cơ bản
        }
        String publicId = fileS2Repository.findByCreatedBy(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)).getPublicId();
        //tim trong preferredUserName xem co ten do khong
        //tim ra public id
        if (getProfileRequest.getRatio() == null) {
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .code(200)
                    .message("get profile successfully")
                    .result(cloudinaryService
                            .getTransformedImageUrl(publicId,
                                    getProfileRequest.getWidth(),
                                    getProfileRequest.getHeight(),
                                    getProfileRequest.getCropMode()))
                    .build());
        } else return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("get profile successfully")
                .result(cloudinaryService
                        .getImageByRatio(publicId,
                                getProfileRequest.getRatio()))
                .build());
    }

    @Operation(summary = "filter File",
            description = "filter file with filename, type, create date, modify date, owner")
    @GetMapping("/filter")
    public ApiResponse<Page<FIleS2Response>> filter(@ModelAttribute FileFilterRequest filterRequest) {
        return ApiResponse.<Page<FIleS2Response>>builder()
                .code(200)
                .message("filter file successfully")
                .result(cloudinaryService.search(filterRequest.getFileName(),
                        filterRequest.getTypeOfFile(),
                        getInstant(filterRequest.getCreateDate()),
                        getInstant(filterRequest.getModifyDate()),
                        filterRequest.getOwner(),
                        filterRequest.getPage(),
                        filterRequest.getSize()))
                .build();
    }
}
