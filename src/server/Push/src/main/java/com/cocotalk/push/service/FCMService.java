package com.cocotalk.push.service;

import com.cocotalk.push.dto.common.ClientType;
import com.cocotalk.push.dto.fcm.FCMMessage;
import com.cocotalk.push.dto.fcm.common.Data;
import com.cocotalk.push.dto.fcm.common.Message;
import com.cocotalk.push.dto.fcm.ios.Alert;
import com.cocotalk.push.dto.fcm.ios.Apns;
import com.cocotalk.push.dto.fcm.ios.Aps;
import com.cocotalk.push.dto.fcm.web.Webpush;
import com.cocotalk.push.dto.kafka.MessageType;
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

    /**
     * 정보를 FCM 요청에 담을 body 형태로 가공합니다.
     * iOS버전으로 subtitle과 data가 들어갑니다.
     *
     * @param targetToken push를 보낼 대상
     * @param info 푸시 생성에 필요한 정보가 담긴 모델
     * @return FCM 요청 body에 담을 FCMMEssage
     */
    private FCMMessage makeIOSMessage(String targetToken, PushTopicDto info) {

        // 룸 타입에 따라 subtitle 설정
        String subTitle;
        if(RoomType.PRIVATE.equals(info.getRoomType())){
            subTitle = "";
        } else{
            subTitle = info.getRoomname();
        }

        // push 메시지 내용
        String content = makePushMessageContent(info.getMessageType(), info.getMessage());

        // message에 담을 iOS 설정
        Apns apns = Apns.builder()
                .payload(Apns.Payload.builder()
                        .aps(Aps.builder()
                                .alert(Alert.builder()
                                        .title(info.getUsername())
                                        .subtitle(subTitle)
                                        .body(content)
                                        .build())
                                .sound("chime.aiff")
                                .build())
                        .build())
                .build();

        // message에 담을 Data
        Data data = Data.builder()
                .roomId(info.getRoomId())
                .build();

        // FCMMessage에 담을 message
        Message message = Message.builder()
                .token(targetToken)
                .apns(apns)
                .data(data)
                .build();

        // FCMMessage 작성
        return FCMMessage.builder()
                .message(message)
                .validate_only(false)
                .build();
    }


    /**
     * 정보를 FCM 요청에 담을 body 형태로 가공합니다.
     * Web버전으로 Notification을 담지 않고 Data에 정보를 담아 Web에서 별도로 커스텀 푸시를 제작하도록 했습니다.
     *
     * @param targetToken push를 보낼 대상
     * @param info 푸시 생성에 필요한 정보가 담긴 모델
     * @return FCM 요청 body에 담을 FCMMEssage
     */
    private FCMMessage makeWebMessage(String targetToken, PushTopicDto info) {

        // 룸 타입에 따라 title 설정
        String title;
        if(RoomType.PRIVATE.equals(info.getRoomType())){
            title = info.getUsername();
        } else{
            title = "["+info.getRoomname()+"] " + info.getUsername();
        }

        // push 메시지 내용
        String content = makePushMessageContent(info.getMessageType(), info.getMessage());

        // message에 담을 web 설정
        Webpush webpush = Webpush.builder()
                .data(Webpush.Data.builder()
                        .title(title)
                        .body(content)
                        .url(frontUrl)
                        .build())
                .build();

        // FCMMessage 작성
        return FCMMessage.builder()
                .message(Message.builder()
                        .token(targetToken)
                        .webpush(webpush)
                        .build())
                .validate_only(false)
                .build();
    }

    /**
     * 정보를 FCM 요청에 담을 body 형태로 가공해줍니다.
     * type에 따라서 makeIOSMessage 함수 or makeWebMessage 함수를 호출해줍니다.
     *
     * @param type push를 받을 클라이언트의 타입 (MOBILE or WEB)
     * @param targetToken push를 보낼 대상
     * @param info 푸시 생성에 필요한 정보가 담긴 모델
     * @return FCM 요청 body에 담을 FCMMEssage
     */
    private FCMMessage makeMessage(ClientType type, String targetToken, PushTopicDto info) {
        if(ClientType.MOBILE.equals(type)){
            return makeIOSMessage(targetToken, info);
        } else{
            return makeWebMessage(targetToken, info);
        }
    }

    private String makePushMessageContent(MessageType messageType, String messageContent){
        String content;
        if(MessageType.PHOTO.equals(messageType)){
            content = "사진을 보냈습니다.";
        } else if(MessageType.VIDEO.equals(messageType)){
            content = "동영상을 보냈습니다.";
        } else if(MessageType.FILE.equals(messageType)) {
            content = "파일을 보냈습니다.";
        } else {
            content = messageContent;
        }
        return content;
    }

    /**
     * 요청으로 받은 타켓들에게 info에 담긴 정보로 푸시를 보내줍니다.
     *
     * @param targets push를 받을 target device들
     * @param info 푸시 생성에 필요한 정보가 담긴 모델
     */
    public void sendByDevices(Flux<Device> targets, PushTopicDto info) throws IOException {
        String authorization = "Bearer " + getAccessToken();
        WebClient webClient = WebClient.create();

        // 요청 body에 담을 FCMMessage 작성
        Flux<FCMMessage> fcmMessageFlux = targets
                .map(target -> {
                  ClientType type = ClientType.values()[target.getType()];
                  log.info("["+target.getUserId()+"/"+type+"] : " +target.getToken());
                    return makeMessage(type, target.getToken(), info);
                })
                .doOnError((e) ->{
                    System.err.println("Error : " + e.getMessage());
                    throw new CustomException(BAD_REQUEST, e);
                });

        // FCM에 푸시 요청
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
                        (e) -> {log.info("[webClient] ::" + e.getMessage());}
                )
        );
    }

    /**
     * FCM 요청 헤더에 담을 accessToken을 발급받습니다.
     *
     * @return 발급받은 token
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/cocotalk_firebase_service_key.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}

