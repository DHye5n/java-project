package dh.javaproject.javaproject.dto.response.user;

import dh.javaproject.javaproject.domain.user.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResDto {

    private final Long userId;
    private final String email;
    private final String username;
    private final String phone;

    @Builder
    public UserResDto(Long userId, String email, String username, String phone) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.phone = phone;

    }

    public static UserResDto fromEntity(UserEntity user) {
        return UserResDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
    }
}
