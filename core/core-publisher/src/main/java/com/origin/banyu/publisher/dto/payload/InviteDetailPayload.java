package com.origin.banyu.publisher.dto.payload;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class InviteDetailPayload {
    // 根级别字段（数据库当前JSON为下划线命名）
    @JSONField(name = "wechat_work_nickname", alternateNames = {"wechatWorkNickname"})
    private String wechatWorkNickname;

    @JSONField(name = "invite_start_time", alternateNames = {"inviteStartTime"})
    private String inviteStartTime;

    @JSONField(name = "invited_user_id", alternateNames = {"invitedUserId"})
    private String invitedUserId;

    @JSONField(name = "inviter_user_id", alternateNames = {"inviterUserId"})
    private String inviterUserId;

    // 兼容旧结构：嵌套 invitedUser 对象
    @JSONField(name = "invitedUser")
    private InvitedUser invitedUser;

    @Data
    public static class InvitedUser {
        @JSONField(name = "userId")
        private String userId;
        @JSONField(name = "wechatNickname")
        private String wechatNickname;
    }
}


