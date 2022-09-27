package com.cdfholding.notificationcenter.controller;

import com.cdfholding.notificationcenter.dto.AllowedUserApplyRequest;
import com.cdfholding.notificationcenter.dto.AllowedUserApplyResponse;
import com.cdfholding.notificationcenter.events.AllowedUserAppliedEvent;
import com.cdfholding.notificationcenter.kafka.KafkaStreamsConfig;
import com.cdfholding.notificationcenter.kafka.NotificationTopology;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

  KafkaTemplate<String, AllowedUserApplyRequest> kafkaTemplate;

  @Autowired
  StreamsBuilderFactoryBean factoryBean;

  public AdminController(KafkaTemplate<String, AllowedUserApplyRequest> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }


  @PostMapping(path = "/apply")
  public AllowedUserApplyResponse apply(@RequestBody AllowedUserApplyRequest request) {
    request.setType("apply");
    kafkaTemplate.send("allowed-user-command", request.getAdUser(), request);

    KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();

    ReadOnlyKeyValueStore<String, AllowedUserAppliedEvent> keyValueStore = kafkaStreams.store(
        StoreQueryParameters.fromNameAndType("eventTable", QueryableStoreTypes.keyValueStore()));

    AllowedUserAppliedEvent value = keyValueStore.get(request.getAdUser());

    System.out.println(value);

    KeyValueIterator<String, AllowedUserAppliedEvent> range = keyValueStore.all();

    return new AllowedUserApplyResponse("cdfh3593", "success", "none");
  }
}
