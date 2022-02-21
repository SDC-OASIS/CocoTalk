package com.cocotalk.auth.dto.common.payload;

import com.cocotalk.auth.dto.common.response.ResponseStatus;
import com.cocotalk.auth.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

/**
 *
 * User의 프로필 정보를 profile 컬럼에 JSON 형태의 String으로 저장하는데,
 * profile 컬럼의 json과 매핑되는 Class입니다.
 *
 */
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
            throw new CustomException(ResponseStatus.PARSE_ERROR);
        }
    }

    public static ProfilePayload toObject(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonString, ProfilePayload.class);
        } catch (Exception e) {
            throw new CustomException(ResponseStatus.PARSE_ERROR);
        }
    }
}
