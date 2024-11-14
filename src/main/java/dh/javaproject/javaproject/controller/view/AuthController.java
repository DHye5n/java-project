package dh.javaproject.javaproject.controller.view;

import dh.javaproject.javaproject.dto.request.user.UserSignInReqDto;
import dh.javaproject.javaproject.dto.response.ApiResponseDto;
import dh.javaproject.javaproject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "index";
    }

    @GetMapping("/sign-in")
    public String signInPage() {
        return "sign-in";
    }

    @PostMapping("/sign-in")
    public String signIn(@Valid UserSignInReqDto dto, Model model) {
        ApiResponseDto response = userService.signIn(dto);

        if (response.isSuccess()) {
            return "redirect:/auth/dashboard";
        } else {
            model.addAttribute("error", response.getMessage());
            return "sign-in";
        }
    }

    @GetMapping("/sign-up")
    public String signUpPage() {
        return "sign-up";
    }
}
