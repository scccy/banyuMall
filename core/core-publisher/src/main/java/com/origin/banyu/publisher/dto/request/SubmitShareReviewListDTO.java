package com.origin.banyu.publisher.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class SubmitShareReviewListDTO {
    private List<String> reviewList ;
    private Integer reviewStatusId;
    private String reviewComment;

}
