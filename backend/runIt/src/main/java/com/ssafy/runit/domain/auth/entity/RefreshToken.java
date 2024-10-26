package com.ssafy.runit.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refreshToken;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private User user;

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
