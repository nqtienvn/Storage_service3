package com.tien.storageservice_3.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetImageRequest {
    String publicId;
    Integer width;
    Integer height;
    String cropMode;
    String ratio;
}
