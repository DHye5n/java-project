package dh.javaproject.javaproject.dto.request.user;

import dh.javaproject.javaproject.domain.user.UserEntity;
import dh.javaproject.javaproject.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class UserSignUpReqDto {

    @NotBlank
    @Email(message = "이메일 형식에 맞춰주세요.")
    private String email;

    @NotBlank
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}", message = "비밀번호는 숫자, 소문자, 대문자를 각각 하나 이상 포함해야 합니다.")
    private String password;

    @NotBlank
    private String passwordCheck;

    @NotBlank
    @Size(min = 3, max = 10)
    private String username;

    @NotBlank
    @Pattern(regexp = "^[0-9]{11}$", message = "핸드폰 번호는 11자리입니다.")
    private String phone;

    @NotBlank
    private String zonecode;

    @NotBlank
    private String address;

    @NotBlank
    private String addressDetail;

    private Role role = Role.USER;

    private String verificationCode;

    @Builder
    public UserSignUpReqDto(String email, String password, String passwordCheck, String username, String phone,
                            String zonecode, String address, String addressDetail, Role role, String verificationCode) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.username = username;
        this.phone = phone;
        this.zonecode = zonecode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.role = role != null ? role : Role.USER;
        this.verificationCode = verificationCode;
    }

    public boolean isPasswordMatching() {
        return password.equals(passwordCheck);
    }

    public UserEntity toEntity(String encodedPassword) {
        return UserEntity.builder()
                .email(email)
                .password(encodedPassword)
                .username(username)
                .phone(phone)
                .zonecode(zonecode)
                .address(address)
                .addressDetail(addressDetail)
                .role(role != null ? role : Role.USER) // Default role as USER
                .build();
    }
}
