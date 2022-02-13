package com.cocotalk.user.domain.vo;

import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePayload {
    private String profile;
    private String background;
    private String message;

    public static String toJSON(ProfilePayload profilePayload){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(profilePayload);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ProfilePayload/toJSON] : profilePayload를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }

    public static ProfilePayload toObject(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, ProfilePayload.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[ProfilePayload/toObject] : jsonString를 파싱하는 도중 문제가 발생했습니다.");
            throw new CustomException(CustomError.JSON_PARSE, e);
        }
    }
}
