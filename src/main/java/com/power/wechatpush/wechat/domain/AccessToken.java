package com.power.wechatpush.wechat.domain;

public class AccessToken {
    private String accessToken;
    private int expiresIn = -1;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return String.format("{accessToken: %s, expiresIn: %d}", accessToken, expiresIn);
    }

}
