package com.cdfholding.notificationcenter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@AllArgsConstructor
public class AllowedUserApplyResponse {

    @Getter
    String adUser;

    @Getter
    String result;

    @Getter
    String reason;
}
