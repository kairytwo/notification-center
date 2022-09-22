package com.cdfholding.notificationcenter.serialization;

import com.cdfholding.notificationcenter.dto.AllowedUserApplyRequest;
import org.apache.kafka.common.serialization.Serializer;

public class AllowedUserApplyRequestSerializer implements Serializer<AllowedUserApplyRequest> {
    @Override
    public byte[] serialize(String topic, AllowedUserApplyRequest data) {
        JsonSerializer<AllowedUserApplyRequest> jsonSerializer = new JsonSerializer<>();
        return new byte[0];
    }
}
