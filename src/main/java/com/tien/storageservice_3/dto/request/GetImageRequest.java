package com.tien.storageservice_3.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetImageRequest {
    String publicId;
    Integer width;
    Integer height;
    String cropMode;
    String ratio;
}
