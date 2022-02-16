package com.cocotalk.push.service;

import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.dto.fcm.FCMMessage;
import com.cocotalk.push.dto.fcm.common.Data;
import com.cocotalk.push.dto.fcm.common.Message;
import com.cocotalk.push.dto.fcm.ios.Alert;
import com.cocotalk.push.dto.fcm.ios.Apns;
import com.cocotalk.push.dto.fcm.ios.Aps;
import com.cocotalk.push.dto.fcm.web.Webpush;
import com.cocotalk.push.dto.kafka.PushTopicDto;
import com.cocotalk.push.dto.kafka.RoomType;
import com.cocotalk.push.entity.Device;
import com.cocotalk.push.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static com.cocotalk.push.dto.common.response.ResponseStatus.*;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.c;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.title;

/**
 *
 * Firebase Cloud Messaging에 푸시 요청을 보내는데 필요한 메서드들이 포함된 클래스 입니다.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    @Value("${fcm.api-url}")
    private String apiUrl;
    @Value("${fcm.frontend-url}")
    private String frontUrl;

    private FCMMessage makeIOSMessage(String targetToken, PushTopicDto info) {

        // 룸 타입에 따라 subtitle 설정
        String subTitle;
        if(RoomType.PRIVATE.equals(info.getRoomType())){
            subTitle = "";
        } else{
            subTitle = info.getRoomname();
        }

        // FCMMessage 작성
        FCMMessage fcmMessage = FCMMessage.builder()
                .message(Message.builder()
                        .token(targetToken)
                        .data(Data.builder()
                                .roomId(info.getRoomId())
                                .build())
                        .apns(Apns.builder()
                                .payload(Apns.Payload.builder()
                                        .aps(Aps.builder()
                                                .alert(Alert.builder()
                                                        .title(info.getUsername())
                                                        .subtitle(subTitle)
                                                        .body(info.getMessage())
                                                        .build())
                                                .sound("chime.aiff")
                                                .build())
                                        .build())
                                .build())
                        .build()
                )
                .validate_only(false)
                .build();
        return fcmMessage;
    }

    private FCMMessage makeWebMessage(String targetToken, PushTopicDto info) {

        // 룸 타입에 따라 title 설정
        String title;
        if(RoomType.PRIVATE.equals(info.getRoomType())){
            title = info.getUsername();
        } else{
            title = "["+info.getRoomname()+"] " + info.getUsername();
        }

        // FCMMessage 작성
        FCMMessage fcmMessage = FCMMessage.builder()
                .message(Message.builder()
                        .token(targetToken)
                        .webpush(Webpush.builder()
                                .data(Webpush.Data.builder()
                                        .title(title)
                                        .body(info.getMessage())
                                        .url(frontUrl)
                                        .build())
                                .build())
                        .build())
                .validate_only(false)
                .build();
        return fcmMessage;
    }

    private FCMMessage makeMessage(ClientType type, String targetToken, PushTopicDto info) {
        if(ClientType.MOBILE.equals(type)){
            return makeIOSMessage(targetToken, info);
        } else{
            return makeWebMessage(targetToken, info);
        }
    }

    public void sendByDevices(Flux<Device> targets, PushTopicDto info) throws IOException {
        String authorization = "Bearer " + getAccessToken();
        long startTime = System.currentTimeMillis();
        WebClient webClient = WebClient.create();
        Flux<FCMMessage> fcmMessageFlux = targets
                .map(target -> {
                  ClientType type = ClientType.values()[target.getType()];
                    return makeMessage(type, target.getToken(), info);
                })
                .doOnError((e) ->{
                    System.err.println("Error : " + e.getMessage());
                    throw new CustomException(BAD_REQUEST, e);
                });

        fcmMessageFlux.subscribe(fcmTokenMessage -> webClient
                .post()
                .uri(apiUrl)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .header(org.springframework.http.HttpHeaders.AUTHORIZATION, authorization)
                .bodyValue(fcmTokenMessage)
                .retrieve()
                .onStatus(
                        HttpStatus::is4xxClientError,
                        response -> response.bodyToMono(String.class).map(Exception::new))
                .bodyToFlux(String.class)
                .subscribe(
                        res -> log.info("{}", res),
                        (e) -> {log.info(e.getMessage());}
                )
        );
        log.info("end: " + (System.currentTimeMillis() - startTime) + "sec");
    }


    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/cocotalk_firebase_service_key.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

