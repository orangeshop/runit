package com.ssafy.runit.domain.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.runit.domain.auth.dto.request.UpdateJwtRequest;
import com.ssafy.runit.domain.auth.dto.request.UserLoginRequest;
import com.ssafy.runit.domain.auth.dto.request.UserRegisterRequest;
import com.ssafy.runit.domain.auth.dto.response.LoginResponse;
import com.ssafy.runit.domain.auth.service.AuthService;
import com.ssafy.runit.exception.CustomException;
import com.ssafy.runit.exception.code.AuthErrorCode;
import com.ssafy.runit.exception.code.ServerErrorCode;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "api/auth/";
    private static final String TEST_NUMBER = "1234";
    private static final String REFRESH_TOKEN = "refreshToken";

    @BeforeEach
    void setPort() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("[회원가입]-성공 검증")
    void register_Success() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .userName("testUser")
                .userNumber("1234")
                .userImageUrl("image")
                .build();

        doNothing().when(authService).registerUser(any(UserRegisterRequest.class));
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "register")
                .then()
                .log().all()
                .statusCode(200)
                .body("message", equalTo("회원가입에 성공했습니다"))
                .body("data", nullValue());
    }

    @Test
    @DisplayName("[회원가입] - 중복 사용자 검증")
    void registerUser_DuplicateUser_ThrowsException() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .userName("testUser")
                .userNumber(TEST_NUMBER)
                .userImageUrl("http://example.com/image.jpg")
                .build();
        doThrow(new CustomException(AuthErrorCode.DUPLICATED_USER_ERROR))
                .when(authService).registerUser(argThat(argument -> argument.getUserNumber().equals(TEST_NUMBER)));
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "register")
                .then()
                .log().all()
                .statusCode(AuthErrorCode.DUPLICATED_USER_ERROR.getStatus().value())
                .body("message", equalTo(AuthErrorCode.DUPLICATED_USER_ERROR.message()))
                .body("errorCode", equalTo(AuthErrorCode.DUPLICATED_USER_ERROR.errorCode()));
    }

    @Test
    @DisplayName("[회원가입] - 데이터 유효성 검증")
    void registerUser_Invalid_Data_Form_ThrowsException() throws Exception {
        UserRegisterRequest request = UserRegisterRequest.builder()
                .userName(null)
                .userNumber(TEST_NUMBER)
                .userImageUrl(null)
                .build();
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "register")
                .then()
                .log().all()
                .statusCode(ServerErrorCode.METHOD_ARGUMENT_ERROR.getStatus().value())
                .body("message", equalTo(ServerErrorCode.METHOD_ARGUMENT_ERROR.message()))
                .body("errorCode", equalTo(ServerErrorCode.METHOD_ARGUMENT_ERROR.errorCode()));
    }

    @Test
    @DisplayName("[로그인] - 성공 검증")
    void loginUser_Success() throws Exception {
        UserLoginRequest request = new UserLoginRequest(TEST_NUMBER);
        LoginResponse response = new LoginResponse("newAccessToken", "newRefreshToken");
        when(authService.login(ArgumentMatchers.any(UserLoginRequest.class)))
                .thenReturn(response);
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "login")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo("로그인에 성공했습니다"))
                .body("data.accessToken", equalTo(response.getAccessToken()))
                .body("data.refreshToken", equalTo(response.getRefreshToken()));
    }

    @Test
    @DisplayName("[로그인] - 가입하지 않은 사용자 검증")
    void loginUser_UNREGISTERED_USER_ThrowsException() throws Exception {
        UserLoginRequest request = new UserLoginRequest(TEST_NUMBER);
        doThrow(new CustomException(AuthErrorCode.UNREGISTERED_USER_ERROR))
                .when(authService).login(argThat(argument -> argument.getUserNumber().equals(request.getUserNumber())));
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "login")
                .then().log().all()
                .statusCode(AuthErrorCode.UNREGISTERED_USER_ERROR.getStatus().value())
                .body("message", equalTo(AuthErrorCode.UNREGISTERED_USER_ERROR.message()))
                .body("errorCode", equalTo(AuthErrorCode.UNREGISTERED_USER_ERROR.getErrorCode()));
    }

    @Test
    @DisplayName("[로그인] - 데이터 유효성 검증")
    void loginUser_Invalid_Data_Form_ThrowsException() throws Exception {
        UserLoginRequest request = new UserLoginRequest(null);
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "login")
                .then().log().all()
                .statusCode(ServerErrorCode.METHOD_ARGUMENT_ERROR.getStatus().value())
                .body("message", equalTo(ServerErrorCode.METHOD_ARGUMENT_ERROR.message()))
                .body("errorCode", equalTo(ServerErrorCode.METHOD_ARGUMENT_ERROR.errorCode()));
    }

    @Test
    @DisplayName("[Jwt 토큰] - 갱신 성공 검증")
    void updateJwtToken_Success() throws Exception {
        UpdateJwtRequest request = new UpdateJwtRequest(TEST_NUMBER);
        LoginResponse response = new LoginResponse("newAccessToken", "newRefreshToken");
        when(authService.getNewRefreshToken(ArgumentMatchers.any(UpdateJwtRequest.class)))
                .thenReturn(response);
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "token")
                .then().log().all()
                .statusCode(200)
                .body("message", equalTo("JWT Token 갱신에 성공했습니다"))
                .body("data.accessToken", equalTo(response.getAccessToken()))
                .body("data.refreshToken", equalTo(response.getRefreshToken()));
    }

    @Test
    @DisplayName("[Jwt 토큰] - 만료 검증")
    void updateJwtToken_InvalidRefreshToken_ThrowsException() throws Exception {
        UpdateJwtRequest request = new UpdateJwtRequest(TEST_NUMBER);
        doThrow(new CustomException(AuthErrorCode.EXPIRED_TOKEN_ERROR))
                .when(authService).getNewRefreshToken(argThat(argument -> argument.getRefreshToken().equals(request.getRefreshToken())));
        given()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsString(request))
                .when().post(BASE_URL + "token")
                .then().log().all()
                .statusCode(AuthErrorCode.EXPIRED_TOKEN_ERROR.getStatus().value())
                .body("message", equalTo(AuthErrorCode.EXPIRED_TOKEN_ERROR.message()))
                .body("errorCode", equalTo(AuthErrorCode.EXPIRED_TOKEN_ERROR.errorCode()));
    }
}