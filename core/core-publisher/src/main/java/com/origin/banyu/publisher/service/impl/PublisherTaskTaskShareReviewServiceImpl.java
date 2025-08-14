package com.origin.banyu.publisher.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.origin.banyu.base.service.BaseService;
import com.origin.banyu.common.dto.ResultData;
import com.origin.banyu.common.entity.ErrorCode;
import com.origin.banyu.common.entity.SysUser;
import com.origin.banyu.common.exception.BusinessException;
import com.origin.banyu.publisher.dto.request.ShareReviewListRequest;
import com.origin.banyu.publisher.dto.request.ShareReviewRequest;
import com.origin.banyu.publisher.dto.request.SubmitShareReviewListDTO;
import com.origin.banyu.publisher.dto.response.ShareReviewResponse;
import com.origin.banyu.publisher.entity.PublisherShareReview;
import com.origin.banyu.publisher.feign.UserFeignClient;
import com.origin.banyu.publisher.mapper.PublisherShareReviewMapper;
import com.origin.banyu.publisher.service.PublisherTaskShareReviewService;
import com.origin.banyu.publisher.service.PublisherTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 社群分享审核服务实现类
 * 作者: scccy
 * 创建时间: 2025-07-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PublisherTaskTaskShareReviewServiceImpl extends BaseService implements PublisherTaskShareReviewService {
    
    private final PublisherShareReviewMapper shareReviewMapper;
    private final UserFeignClient userFeignClient;
    private final PublisherTaskService publisherTaskService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitShareReview(ShareReviewRequest request) {
        log.info("提交分享审核，请求参数：{}", request);
      String  taskId =  shareReviewMapper.selectById(request.getShareReviewId()).getTaskId();
        PublisherShareReview review = new PublisherShareReview();
        review.setShareReviewId(request.getShareReviewId());
        review.setTaskId(taskId);
        review.setReviewComment(request.getReviewComment());
        review.setReviewStatusId(request.getReviewStatusId());
        shareReviewMapper.insertOrUpdate(review);

        return "成功";
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewShareReview(String shareReviewId, Integer reviewStatus, String reviewComment) {
        log.info("审核分享内容，ID：{}，审核状态：{}，审核意见：{}", shareReviewId, reviewStatus, reviewComment);
        
        PublisherShareReview review = shareReviewMapper.selectById(shareReviewId);
        if (review == null) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_REVIEW_NOT_FOUND);
        }
        
        if (review.getReviewStatusId() != 1) {
            throw new BusinessException(ErrorCode.PUBLISHER_TASK_REVIEW_STATUS_INVALID, "只有待审核状态的记录才能审核");
        }
        
        review.setReviewStatusId(reviewStatus);
        review.setReviewComment(reviewComment);
        
        shareReviewMapper.updateById(review);
        
        log.info("分享审核完成，ID：{}，审核结果：{}", shareReviewId, reviewStatus);
    }
    
    @Override
    public IPage<ShareReviewResponse> getShareReviewList(ShareReviewListRequest request) {

        Page<PublisherShareReview> pageParam = new Page<>(request.getPage(), request.getSize());
        IPage<PublisherShareReview> result = shareReviewMapper.selectByConditions(pageParam, request);

		// 批量查询微信昵称，减少远程调用
		List<String> ids = result.getRecords().stream()
				.map(PublisherShareReview::getCreatedBy)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList());

		log.debug("批量查询用户信息 - 用户ID列表: {}", ids);

		List<SysUser> users;
		if (ids.isEmpty()) {
			users = java.util.Collections.emptyList();
		} else {
			try {
				ResultData<List<SysUser>> batch = userFeignClient.getBatchUserInfo(ids);
				users = (batch != null && batch.getCode() == 200&& batch.getData() != null)
						? batch.getData()
						: java.util.Collections.emptyList();
				
				log.debug("批量查询用户信息结果 - 请求ID数量: {}, 返回用户数量: {}, 响应码: {}", 
						ids.size(), users.size(), batch != null ? batch.getCode() : "null");
				
				// 记录每个用户的昵称信息
				users.forEach(user -> {
					log.debug("用户信息 - userId: {}, wechatNickname: {}, wechatWorkNickname: {}, nickname: {}", 
							user.getUserId(), user.getWechatNickname(), user.getWechatWorkNickname(), user.getNickname());
				});
				
			} catch (Exception ex) {
				log.warn("批量获取用户信息失败, ids={}", ids, ex);
				users = java.util.Collections.emptyList();
			}
		}

		Map<String, SysUser> userMap = users.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(SysUser::getUserId, u -> u, (a, b) -> a));
		
		log.debug("用户映射表构建完成 - 映射表大小: {}", userMap.size());

        // 使用基础类的方法构建分页响应
        return buildPageResponse(result, r -> convertToResponse(r, userMap));
    }
    
    @Override
    public List<ShareReviewResponse> getShareReviewListAll(ShareReviewListRequest request) {
        log.info("获取分享审核列表（不分页，用于导出），请求: taskId={}, taskName={}, reviewStatus={}, userId={}, wechatNickname={}",
                request.getTaskId(), request.getTaskName(), request.getReviewStatus(), request.getUserId(), request.getWechatNickname());

        // 查询所有数据（不分页）
        List<PublisherShareReview> result = shareReviewMapper.selectAllByConditions(request);

        // 批量查询微信昵称，减少远程调用
        List<String> ids = result.stream()
                .map(PublisherShareReview::getCreatedBy)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        log.debug("批量查询用户信息(不分页) - 用户ID列表: {}", ids);

        List<SysUser> users;
        if (ids.isEmpty()) {
            users = java.util.Collections.emptyList();
        } else {
            try {
                ResultData<List<SysUser>> batch = userFeignClient.getBatchUserInfo(ids);
                users = (batch != null && batch.getCode()==200 && batch.getData() != null)
                        ? batch.getData()
                        : java.util.Collections.emptyList();
                
                log.debug("批量查询用户信息结果(不分页) - 请求ID数量: {}, 返回用户数量: {}, 响应码: {}", 
                        ids.size(), users.size(), batch != null ? batch.getCode() : "null");
                
                // 记录每个用户的昵称信息
                users.forEach(user -> {
                    log.debug("用户信息(不分页) - userId: {}, wechatNickname: {}, wechatWorkNickname: {}, nickname: {}", 
                            user.getUserId(), user.getWechatNickname(), user.getWechatWorkNickname(), user.getNickname());
                });
                
            } catch (Exception ex) {
                log.warn("批量获取用户信息失败, ids={}", ids, ex);
                users = java.util.Collections.emptyList();
            }
        }

        Map<String, SysUser> userMap = users.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(SysUser::getUserId, u -> u, (a, b) -> a));
        
        log.debug("用户映射表构建完成(不分页) - 映射表大小: {}", userMap.size());

        // 转换为响应对象
        return result.stream()
                .map(r -> convertToResponse(r, userMap))
                .collect(Collectors.toList());
    }

    @Override
    public String submitShareReviewList(SubmitShareReviewListDTO request) {
        LambdaUpdateWrapper<PublisherShareReview> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(PublisherShareReview::getReviewerId, request.getReviewList())
                .set(PublisherShareReview::getShareReviewId, request.getReviewStatusId())
                .set(PublisherShareReview::getReviewComment,request.getReviewComment())    ;
        return "ok";
    }

    private ShareReviewResponse convertToResponse(PublisherShareReview review, Map<String, SysUser> userMap) {
        ShareReviewResponse response = new ShareReviewResponse();
        BeanUtils.copyProperties(review, response);
        
        // 补充 userId（提交人），保证非空
        response.setUserId(review.getCreatedBy() == null ? "" : review.getCreatedBy());
        
        // 获取任务名称
        try {
            if (review.getTaskId() != null && !review.getTaskId().isEmpty()) {
                var taskDetail = publisherTaskService.getTaskDetail(review.getTaskId());
                if (taskDetail != null) {
                    response.setTaskName(taskDetail.getTaskName());
                }
            }
        } catch (Exception ex) {
            log.warn("获取任务名称失败, taskId: {}", review.getTaskId(), ex);
        }
        
        // 处理多链接与多图片：以逗号分隔，保证非空列表
        List<String> links = (review.getShareUrl() == null || review.getShareUrl().isEmpty())
                ? new java.util.ArrayList<>()
                : java.util.Arrays.stream(review.getShareUrl().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        response.setLinks(links);
        
        List<String> images = (review.getScreenshotUrl() == null || review.getScreenshotUrl().isEmpty())
                ? new java.util.ArrayList<>()
                : java.util.Arrays.stream(review.getScreenshotUrl().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        response.setImages(images);
        
        // 查询微信昵称（优先使用批量结果），保证非空
        String nickname = "";
        if (response.getUserId() != null && !response.getUserId().isEmpty() && userMap != null) {
            SysUser u = userMap.get(response.getUserId());
            if (u != null) {
                // 优先使用普通微信昵称，如果没有则使用企业微信昵称，最后使用通用昵称
                if (u.getWechatNickname() != null && !u.getWechatNickname().trim().isEmpty()) {
                    nickname = u.getWechatNickname();
                } else if (u.getWechatWorkNickname() != null && !u.getWechatWorkNickname().trim().isEmpty()) {
                    nickname = u.getWechatWorkNickname();
                } else if (u.getNickname() != null && !u.getNickname().trim().isEmpty()) {
                    nickname = u.getNickname();
                }
            }
            
            // 添加调试日志
            log.debug("用户昵称查询 - userId: {}, wechatNickname: {}, wechatWorkNickname: {}, nickname: {}, 最终结果: {}", 
                    response.getUserId(), u != null ? u.getWechatNickname() : "null", 
                    u != null ? u.getWechatWorkNickname() : "null", 
                    u != null ? u.getNickname() : "null", nickname);
        }
        
        response.setWechatNickname(nickname);
        
        return response;
    }
    


} 