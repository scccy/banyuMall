package com.origin.banyu.wechatWork.service.persistence;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.origin.banyu.wechatWork.entity.WechatworkUser;
import com.origin.banyu.wechatWork.mapper.WechatworkUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WechatworkUserPersistenceService extends ServiceImpl<WechatworkUserMapper, WechatworkUser> {
}


