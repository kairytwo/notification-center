package com.cdfholding.notificationcenter.serialization;

import com.cdfholding.notificationcenter.dto.AllowedUserApplyRequest;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class JsonSerdes {

    public static Serde<AllowedUserApplyRequest> AllowedUserApplyRequest() {
        JsonSerializer<AllowedUserApplyRequest> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<AllowedUserApplyRequest> jsonDeserializer = new JsonDeserializer<>(AllowedUserApplyRequest.class);

        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }
}
