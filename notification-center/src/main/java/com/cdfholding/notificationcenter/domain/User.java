package com.cdfholding.notificationcenter.domain;

import lombok.Data;

@Data
public class User {
    String adUser;
    LdapInfo ldapInfo;
}
