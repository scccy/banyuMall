package com.origin.banyu.publisher.dto.payload;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class NormalDetailPayload {
    @JSONField(name = "wechat_nickname")
    private String wechatNickname;

    @JSONField(name = "wechatNickname")
    private String wechatNicknameAlt;

    @JSONField(name = "user")
    private UserSummary user;

    @Data
    public static class UserSummary {
        @JSONField(name = "wechatNickname")
        private String wechatNickname;
    }

    public String resolveWechatNickname() {
        if (wechatNickname != null && !wechatNickname.isEmpty()) {
            return wechatNickname;
        }
        if (wechatNicknameAlt != null && !wechatNicknameAlt.isEmpty()) {
            return wechatNicknameAlt;
        }
        if (user != null && user.getWechatNickname() != null && !user.getWechatNickname().isEmpty()) {
            return user.getWechatNickname();
        }
        return null;
    }
}


