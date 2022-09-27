package com.cdfholding.notificationcenter.events;

import lombok.Data;

@Data
public class AllowedUserAppliedFailure {

  String adUser;

  String result;

  String reason;
}
