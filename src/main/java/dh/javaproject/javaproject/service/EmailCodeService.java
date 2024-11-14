package dh.javaproject.javaproject.service;

import dh.javaproject.javaproject.domain.user.EmailEntity;
import dh.javaproject.javaproject.dto.request.email.EmailCodeReqDto;
import dh.javaproject.javaproject.exception.ErrorException;
import dh.javaproject.javaproject.repository.EmailCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class EmailCodeService {

    private final EmailCodeRepository emailCodeRepository;
    private final EmailService emailService;
    private static final int CODE_LENGTH = 6;

    /**
     *      인증 코드 생성
     * */
    public String generateVerificationCode() {

        Random random = new Random();

        int code = random.nextInt((int) Math.pow(10, CODE_LENGTH));

        return String.format("%0" + CODE_LENGTH + "d", code);
    }

    /**
     *      인증 코드 저장
     * */
    @Transactional
    public void saveVerificationCode(String email, String verificationCode) {

        EmailEntity emailEntity = EmailEntity.builder()
                .email(email)
                .verificationCode(verificationCode)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();

        emailCodeRepository.save(emailEntity);
    }

    /**
     *      인증 번호 검증
     * */
    @Transactional
    public boolean verifyCode(EmailCodeReqDto emailCodeReqDto) {
        
        Optional<EmailEntity> optionalEmailEntity = emailCodeRepository.findByEmail(emailCodeReqDto.getEmail());

        if (optionalEmailEntity.isEmpty()) {
            throw new ErrorException("등록되지 않은 이메일입니다.", HttpStatus.NOT_FOUND);
        }

        EmailEntity emailEntity = optionalEmailEntity.get();

        if (LocalDateTime.now().isAfter(emailEntity.getExpiryDate())) {
            throw new ErrorException("인증 코드가 만료되었습니다.", HttpStatus.UNAUTHORIZED);
        }

        if (!emailEntity.getVerificationCode().equals(emailCodeReqDto.getVerificationCode())) {
            throw new ErrorException("인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        return true;
    }

    /**
     *      인증 번호 재생성
     * */
    @Transactional
    public void sendNewVerificationCode(String email) {
        String code = generateVerificationCode();
        saveVerificationCode(email, code);
        emailService.sendVerificationEmail(email, code);
    }
}
