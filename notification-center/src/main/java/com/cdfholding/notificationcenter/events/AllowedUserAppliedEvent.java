package com.cdfholding.notificationcenter.events;

import lombok.Data;

@Data
public class AllowedUserAppliedEvent {

  String adUser;

  String result;

  String reason;

}
