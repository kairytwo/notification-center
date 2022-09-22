package com.cdfholding.notificationcenter.service;

import com.cdfholding.notificationcenter.domain.LdapInfo;
import org.springframework.stereotype.Service;

@Service
public class FakeLdapServiceImpl implements LdapService {
    @Override
    public LdapInfo query(String adUser) {
        LdapInfo info = new LdapInfo();
        info.setAdUser(adUser);
        info.setIsValid(System.currentTimeMillis() % 2 == 0);

        return info;
    }
}
