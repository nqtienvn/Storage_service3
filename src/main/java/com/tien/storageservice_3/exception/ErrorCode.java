package com.tien.storageservice_3.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ErrorCode {
    /*
    * invalid: 0
    * not found: 1
    * existed : 2
    * error everything: 3
    * access denied: 4
    * un authenticate: 5
    * token: 6
    * file: 7

    * */
    //nhung error code nay deu la nhưng instance cua thang ERROR CODE
    USER_NOT_FOUND(1, "USER NOT FOUND", HttpStatus.NOT_FOUND), //nhu la 1 doi tuong, nhung khong tao bang tu khao new
    PASS_INCORRECT(0, "PASS WORD INCORRECT", HttpStatus.BAD_REQUEST),
    ERROR_TOKEN(6, "TOKEN ERROR", HttpStatus.UNAUTHORIZED),
    ERROR_PUBLIC_ID(7, "NOT FOUND PUBLIC ID IN DATA BASE", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOAD_A_FILE_ERROR(7, "upload file failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UNKNOW_ERROR(3, "DON'T KNOW ERROR", HttpStatus.INTERNAL_SERVER_ERROR), //500
    NAME_EXIST(2, "NAME EXISTED", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(4, "ACCESSIONED", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(5, "NOT AUTHENTICATE USER", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(0, "OTP INVALID", HttpStatus.BAD_REQUEST), //do du lieu request sai, khong dung cu phap de su ly
    INVALID_EMAIL(0, "EMAIL INVALID", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN(0, "INVALID PASSWORD OR EMAIL", HttpStatus.BAD_REQUEST),
    EXPIRED_TOKEN(6, "TOKEN IS EXPIRED", HttpStatus.BAD_REQUEST),
    UNVERIFY_TOKEN(6, "TOKEN IS NOT VERIFY", HttpStatus.BAD_REQUEST),
    ERROR_UPLOAD_FILE(7, "ERROR UPLOAD FILE", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_DELETE_FILE(7, "DELETE FILE FAILED", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_UPDATE_FILE(7, "UPDATE FILE FAILED", HttpStatus.INTERNAL_SERVER_ERROR),
    ERROR_STRING_CONVERT_INSTANT(8, "String convert to instant, input is null", HttpStatus.BAD_REQUEST),
    NOT_MATCH_PASSWORD(0, "OLD PASS AND NEW PASS NOT MATCH", HttpStatus.BAD_REQUEST);
    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
