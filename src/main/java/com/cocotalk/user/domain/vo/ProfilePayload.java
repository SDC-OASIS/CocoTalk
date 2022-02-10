package com.cocotalk.user.domain.vo;

import com.cocotalk.user.exception.CustomError;
import com.cocotalk.user.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
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
            throw new CustomException(CustomError.JSON_PARSE);
        }
    }

    public static ProfilePayload toObject(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, ProfilePayload.class);
        } catch (Exception e) {
            throw new CustomException(CustomError.JSON_PARSE);
        }
    }
}
