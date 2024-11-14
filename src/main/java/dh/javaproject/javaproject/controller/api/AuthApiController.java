package dh.javaproject.javaproject.controller.api;

import dh.javaproject.javaproject.dto.request.email.EmailCodeReqDto;
import dh.javaproject.javaproject.dto.request.user.UserSignInReqDto;
import dh.javaproject.javaproject.dto.request.user.UserSignUpReqDto;
import dh.javaproject.javaproject.dto.response.ApiResponseDto;
import dh.javaproject.javaproject.service.EmailCodeService;
import dh.javaproject.javaproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthApiController {

    private final UserService userService;
    private final EmailCodeService emailCodeService;



    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponseDto> signIn(@Valid @RequestBody UserSignInReqDto dto) {
        ApiResponseDto response = userService.signIn(dto);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponseDto> signUp(@Valid @RequestBody UserSignUpReqDto dto) {
        ApiResponseDto response = userService.signUp(dto);
        return ResponseEntity.status(201).body(response);
    }

    /**
     *      인증 번호 발송
     * */
    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponseDto> sendVerificationCode(@RequestParam String email) {
        emailCodeService.sendNewVerificationCode(email);
        return ResponseEntity.ok(new ApiResponseDto(true, "새로운 인증 코드가 발송되었습니다.", null));
    }


    /**
     *      인증 번호 검증
     * */
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponseDto> verifyCode(@RequestBody EmailCodeReqDto dto) {
        emailCodeService.verifyCode(dto);
        return ResponseEntity.ok(new ApiResponseDto(true, "인증 코드가 확인되었습니다.", null));
    }

    /**
     *      이메일 중복 체크
     * */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponseDto> checkEmailExists(@RequestParam String email) {
        ApiResponseDto response = userService.checkEmailExists(email);
        return ResponseEntity.ok(response);
    }

    /**
     *      유저 아이디 중복 체크
     * */
    @GetMapping("/username/{username}/exists")
    public ResponseEntity<ApiResponseDto> checkUsernameExists(@PathVariable String username) {
        ApiResponseDto response = userService.checkUsername(username);
        return ResponseEntity.ok(response);
    }
}
