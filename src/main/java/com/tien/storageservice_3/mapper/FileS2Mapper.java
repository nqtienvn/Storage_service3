package com.tien.storageservice_3.mapper;

import com.tien.storageservice_3.dto.response.FIleS2Response;
import com.tien.storageservice_3.entity.FileS2;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileS2Mapper {
    FIleS2Response toFileS2Response(FileS2 fileS2);
}
