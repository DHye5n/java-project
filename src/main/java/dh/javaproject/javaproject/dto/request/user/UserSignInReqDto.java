package dh.javaproject.javaproject.dto.request.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class UserSignInReqDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;


    @Builder
    public UserSignInReqDto(String username, String password) {
        this.username = username;
        this.password = password;


    }

}
