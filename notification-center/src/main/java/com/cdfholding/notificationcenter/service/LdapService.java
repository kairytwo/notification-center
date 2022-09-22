package com.cdfholding.notificationcenter.service;

import com.cdfholding.notificationcenter.domain.LdapInfo;

public interface LdapService {

    LdapInfo query(String adUser);
}
