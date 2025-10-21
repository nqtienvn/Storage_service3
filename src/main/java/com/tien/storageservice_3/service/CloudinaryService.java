package com.tien.storageservice_3.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.tien.storageservice_3.dto.response.FIleS2Response;
import com.tien.storageservice_3.entity.FileS2;
import com.tien.storageservice_3.exception.AppException;
import com.tien.storageservice_3.exception.ErrorCode;
import com.tien.storageservice_3.mapper.FileS2Mapper;
import com.tien.storageservice_3.repository.FileS2Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {
    private final FileS2Repository fileS2Repository;
    private final Cloudinary cloudinary;
    private final FileS2Mapper fileS2Mapper;

    public String uploadFile(MultipartFile file, String typeOfFolder) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
        String publicValue = generatePublicValue(originalFilename);
        String extension = getFileExtension(originalFilename);
        File fileUpload = convert(file, publicValue, extension);

        try {
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap(
                            "public_id", publicValue,
                            "resource_type", "image",
                            "folder", typeOfFolder
                    )
            );
            FileS2 fileS2 = new FileS2();
            fileS2.setFileName(originalFilename);
            fileS2.setUrl(cloudinary.url()
                    .resourceType("image")
                    .generate(typeOfFolder + "/" + publicValue + "." + extension));
            fileS2.setPublicId(typeOfFolder + "/" + publicValue);
            fileS2.setType(typeOfFolder);
            fileS2Repository.save(fileS2);
            return cloudinary.url()
                    .resourceType("image")
                    .generate(typeOfFolder + "/" + publicValue + "." + extension);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        } finally {
            cleanDisk(fileUpload);
        }
    }

    public String update(MultipartFile file, String typeOfFile, String oldPublicId) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
        String publicValue = generatePublicValue(originalFilename);//
        String extension = getFileExtension(originalFilename);
        File fileUpload = convert(file, publicValue, extension);
        FileS2 fileS2 = fileS2Repository.findByPublicId(oldPublicId).orElseThrow(() -> new AppException(ErrorCode.ERROR_UPLOAD_FILE));
        String type = typeOfFile == null ? fileS2.getType() : typeOfFile;
        try {
            cloudinary.uploader().upload(
                    fileUpload,
                    ObjectUtils.asMap(
                            "public_id", publicValue,
                            "resource_type", "image",
                            "folder", type
                    )
            );
            fileS2.setType(type);
            fileS2.setFileName(originalFilename);
            fileS2.setUrl(cloudinary.url()
                    .resourceType("image")
                    .generate(type + "/" + publicValue + "." + extension));
            fileS2.setPublicId(type + "/" + publicValue);
            fileS2Repository.save(fileS2);
            return cloudinary.url()
                    .resourceType("image")
                    .generate(type + "/" + publicValue + "." + extension);
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        } finally {
            cleanDisk(fileUpload);
        }
    }

    public String deleteFile(String publicId) throws IOException {
        FileS2 fileS2 = fileS2Repository.findByPublicId(publicId).orElseThrow(() -> new AppException(ErrorCode.ERROR_PUBLIC_ID));
        fileS2Repository.delete(fileS2);
        Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        return result.get("result").toString();
    }

    public String updateFile(String oldPublicId, MultipartFile newFile, String typeOfFile) throws IOException {
        //minh nen thim theo cai cu

        cloudinary.uploader().destroy(oldPublicId, ObjectUtils.emptyMap());
        return update(newFile, typeOfFile, oldPublicId);
    }

    private File convert(MultipartFile multipartFile, String publicValue, String extension) {
        try {
            File tempFile = File.createTempFile(publicValue, "." + extension);
            try (InputStream in = multipartFile.getInputStream()) {
                Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return tempFile;
        } catch (IOException e) {
            throw new AppException(ErrorCode.ERROR_UPLOAD_FILE);
        }
    }

    private void cleanDisk(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            log.error("Error deleting temp file: {}", file.getName(), e);
        }
    }

    private String generatePublicValue(String originalFilename) {
        String fileName = getBaseName(originalFilename);
        return UUID.randomUUID() + "_" + fileName;
    }

    private String getBaseName(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }

    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public String getTransformedImageUrl(String publicId, Integer width, Integer height, String cropMode) {
        Transformation transformation = new Transformation()
                .width(width)
                .height(height)
                .crop(cropMode != null ? cropMode : "fill");

        return cloudinary.url()
                .transformation(transformation)
                .generate(publicId);
    }

    public String getImageByRatio(String publicId, String ratio) {
        return cloudinary.url()
                .transformation(new Transformation()
                        .aspectRatio(ratio)
                        .crop("fill"))
                .generate(publicId);
    }

    public Page<FIleS2Response> search(String fileName, String typeOfFile, Instant createDate, Instant modifyDate,
                                          String owner,
                                          int page,
                                          int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FileS2> pageFile = fileS2Repository.search(fileName,typeOfFile,createDate,modifyDate,owner, pageable);
        List<FIleS2Response> listFilter = pageFile.getContent().stream().map(fileS2Mapper::toFileS2Response).collect(Collectors.toList());
        return new PageImpl<>(listFilter, pageable, pageFile.getTotalElements());
    }
}

