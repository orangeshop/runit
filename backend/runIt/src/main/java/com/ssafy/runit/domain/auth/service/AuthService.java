package com.ssafy.runit.domain.auth.service;

import com.ssafy.runit.domain.auth.dto.request.UpdateJwtRequest;
import com.ssafy.runit.domain.auth.dto.request.UserLoginRequest;
import com.ssafy.runit.domain.auth.dto.request.UserRegisterRequest;
import com.ssafy.runit.domain.auth.dto.response.LoginResponse;

public interface AuthService {
    void registerUser(UserRegisterRequest request);

    LoginResponse login(UserLoginRequest request);

    void saveRefreshToken(String userEmail, String refreshToken, String accessToken);

    LoginResponse getNewRefreshToken(UpdateJwtRequest request);

    LoginResponse createJwtToken(String userEmail);

    boolean existsByUserNumber(String userNumber);
}
