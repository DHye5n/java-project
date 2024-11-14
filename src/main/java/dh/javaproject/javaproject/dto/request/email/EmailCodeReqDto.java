package dh.javaproject.javaproject.dto.request.email;

import dh.javaproject.javaproject.domain.user.EmailEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCodeReqDto {

    private String email;
    private String verificationCode;
    private LocalDateTime expiryDate;

    @Builder
    public EmailCodeReqDto(String email, String verificationCode, LocalDateTime expiryDate) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDate = expiryDate;
    }

    public EmailEntity toEntity() {
        return EmailEntity.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expiryDate(expiryDate)
                .build();
    }

}
