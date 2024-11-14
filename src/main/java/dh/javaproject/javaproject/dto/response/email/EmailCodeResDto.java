package dh.javaproject.javaproject.dto.response.email;

import dh.javaproject.javaproject.domain.user.EmailEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EmailCodeResDto {

    private final String email;
    private final String verificationCode;
    private final LocalDateTime expiryDate;

    @Builder
    public EmailCodeResDto(String email, String verificationCode, LocalDateTime expiryDate) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.expiryDate = expiryDate;
    }

    public static EmailCodeResDto fromEntity(EmailEntity emailEntity) {
        return EmailCodeResDto.builder()
                .email(emailEntity.getEmail())
                .verificationCode(emailEntity.getVerificationCode())
                .expiryDate(emailEntity.getExpiryDate())
                .build();
    }
}
