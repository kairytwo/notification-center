package com.cdfholding.notificationcenter.kafka;

import com.cdfholding.notificationcenter.domain.User;
import com.cdfholding.notificationcenter.dto.AllowedUserApplyRequest;
import com.cdfholding.notificationcenter.events.AllowedUserAppliedEvent;
import com.cdfholding.notificationcenter.serialization.JsonSerdes;
import com.cdfholding.notificationcenter.service.LdapService;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NotificationTopology {

  public AllowedUserAppliedEvent event;

  @Autowired
  LdapService ldapService;

  @Autowired
  void pipeline(StreamsBuilder streamsBuilder) {
    KStream<String, AllowedUserApplyRequest> commandStream = streamsBuilder
        .stream("allowed-user-command",
            Consumed.with(Serdes.String(), JsonSerdes.AllowedUserApplyRequest()));

    commandStream.print(Printed.toSysOut());

    Map<String, KStream<String, AllowedUserApplyRequest>> branches = commandStream.split(
            Named.as("Branch-"))
        .branch((key, value) -> value.getType().equals("apply"), Branched.as("ApplyRequest"))
        .defaultBranch(Branched.as("Others"));

    KStream<String, AllowedUserApplyRequest> applyRequestKStream = branches.get(
        "Branch-ApplyRequest");
    KStream<String, AllowedUserApplyRequest> otherKStream = branches.get("Branch-Others");
    KStream<String, User> userKStream = applyRequestKStream
        .mapValues(allowedUserApplyRequest -> queryLdap(allowedUserApplyRequest.getAdUser()));

    Map<String, KStream<String, User>> userBranches = userKStream
        .split(Named.as("Branch2-"))
        .branch((key, value) -> value.getLdapInfo().getIsValid(), Branched.as("ValidUsers"))
        .branch((key, value) -> !value.getLdapInfo().getIsValid(), Branched.as("InvalidUsers"))
        .noDefaultBranch();

    KStream<String, User> validUsers = userBranches.get("Branch2-ValidUsers");
    KStream<String, User> invalidUsers = userBranches.get("Branch2-InvalidUsers");
    // allowed-user
    validUsers.to("allowed-user", Produced.with(Serdes.String(), JsonSerdes.User()));

    // event
    KStream<String, AllowedUserAppliedEvent> eventStream = userKStream.map((String, User) ->
        new KeyValue<>(String, allowedUserAppliedEvent(String, User)));

    eventStream.to("allowed-user-event",
        Produced.with(Serdes.String(), JsonSerdes.AllowedUserAppliedEvent()));

//    eventStream.toTable(
//        Materialized.<String, AllowedUserAppliedEvent, KeyValueStore<Bytes, byte[]>>as("eventTable")
//            .withKeySerde(Serdes.String()).withValueSerde(JsonSerdes.AllowedUserAppliedEvent()));

    KTable<String, AllowedUserAppliedEvent> eventTable = streamsBuilder.table("allowed-user-event",
        Consumed.with(Serdes.String(), JsonSerdes.AllowedUserAppliedEvent()),
        Materialized.as("eventTable"));


  }


  private AllowedUserAppliedEvent allowedUserAppliedEvent(String adUser, User user) {

    AllowedUserAppliedEvent appliedEvent = new AllowedUserAppliedEvent();
    appliedEvent.setAdUser(adUser);

    if (null != user) {
      appliedEvent.setResult("Success");
      appliedEvent.setReason(null);
    } else {
      appliedEvent.setResult("Failure");
      appliedEvent.setReason("error");
    }

    return appliedEvent;

  }

  private User queryLdap(String adUser) {
    User user = new User();
    user.setAdUser(adUser);
    user.setLdapInfo(ldapService.query(adUser));
    return user;
  }


}
