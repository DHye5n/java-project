package dh.javaproject.javaproject.service;

import dh.javaproject.javaproject.domain.user.UserEntity;
import dh.javaproject.javaproject.dto.request.email.EmailCodeReqDto;
import dh.javaproject.javaproject.dto.request.user.UserSignInReqDto;
import dh.javaproject.javaproject.dto.request.user.UserSignUpReqDto;
import dh.javaproject.javaproject.dto.response.ApiResponseDto;
import dh.javaproject.javaproject.dto.response.user.UserResDto;
import dh.javaproject.javaproject.dto.response.user.UserSignInResDto;
import dh.javaproject.javaproject.exception.ErrorException;
import dh.javaproject.javaproject.repository.UserRepository;
import dh.javaproject.javaproject.service.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailCodeService emailCodeService;
    private final JwtService jwtService;

    /**
     *      회원가입
     * */
    @Transactional
    public ApiResponseDto signUp(UserSignUpReqDto dto) {
        log.info("회원가입 요청 - DTO: {}", dto);

        log.info("회원가입 요청 - 이메일: {}, 코드: {}", dto.getEmail(), dto.getVerificationCode());
        // 이메일 인증 코드 검증
        validateVerificationCode(dto.getEmail(), dto.getVerificationCode());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.error("이미 사용중인 이메일입니다. {}", dto.getEmail());
            throw new ErrorException("이미 사용중인 이메일입니다.", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            log.error("이미 사용 중인 사용자 이름입니다. {}", dto.getUsername());
            throw new ErrorException("이미 사용 중인 사용자 아이디입니다.", HttpStatus.CONFLICT);
        }

        if (!dto.isPasswordMatching()) {
            log.error("비밀번호가 일치하지 않습니다. {}", dto.getPassword());
            throw new ErrorException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        UserEntity user = dto.toEntity(encodedPassword);

        log.info("회원가입 유저 정보: {}", user);

        userRepository.save(user);

        return new ApiResponseDto(true, "회원가입이 완료되었습니다.", null);
    }
    
    /**
     *      인증 번호 검증
     * */
    private void validateVerificationCode(String email, String verificationCode) {

        log.info("회원가입 중 이메일: {}, 코드: {}", email, verificationCode);

        EmailCodeReqDto emailCodeRequestDto = EmailCodeReqDto.builder()
                .email(email)
                .verificationCode(verificationCode)
                .build();

        boolean isCodeValid = emailCodeService.verifyCode(emailCodeRequestDto);

        if (!isCodeValid) {
            log.error("잘못된 이메일 인증 코드입니다. 이메일: {}, 코드: {}", email, verificationCode);
            throw new ErrorException("잘못된 이메일 인증 코드입니다.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     *      이메일 중복 체크
     * */
    public ApiResponseDto checkEmailExists(String email) {

        boolean exists = userRepository.existsByEmail(email);

        String message = exists ? "이메일이 이미 사용 중입니다." : "이메일을 사용할 수 있습니다.";

        return new ApiResponseDto(!exists, message, null);
    }

    /**
     *      유저 아이디 중복 체크
     * */
    public ApiResponseDto checkUsername(String username) {

        boolean exists = userRepository.existsByUsername(username);

        String message = exists ? "사용자 이름이 이미 사용 중입니다." : "사용자 이름이 사용 가능합니다.";

        return new ApiResponseDto(!exists, message, null);
    }

    public ApiResponseDto findByEmail(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException("해당 이메일로 회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        return new ApiResponseDto(true, "회원 정보가 조회되었습니다.", UserResDto.fromEntity(user));
    }

    /**
     *      로그인
     * */
    @Transactional
    public ApiResponseDto signIn(UserSignInReqDto dto) {

        UserEntity user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ErrorException("아이디 또는 비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ErrorException("Invalid username or password.", HttpStatus.UNAUTHORIZED);
        }

        PrincipalDetails principalDetails = new PrincipalDetails(user);

        String accessToken = jwtService.generateAccessToken(principalDetails);

        UserSignInResDto response = new UserSignInResDto(accessToken);
        return new ApiResponseDto(true, "Login successful", response);
    }
}
