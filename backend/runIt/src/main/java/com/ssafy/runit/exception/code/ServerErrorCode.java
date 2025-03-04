package com.ssafy.runit.exception.code;

import com.ssafy.runit.exception.ErrorCodeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ServerErrorCode implements ErrorCodeType {

    UNKNOWN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-001", "서버 내부 오류입니다"),
    METHOD_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "S-002", "HTTP 요청이 잘못되었습니다."),
    REDIS_RETRIEVAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S-003", "레디스 내부 오류입니다.");

    private final HttpStatus status;
    private final String errorCode;
    private final String message;

    @Override
    public HttpStatus httpStatus() {
        return this.status;
    }

    @Override
    public String message() {
        return this.message;
    }

    @Override
    public String errorCode() {
        return this.errorCode;
    }
}
