package dh.javaproject.javaproject.repository;

import dh.javaproject.javaproject.domain.user.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailCodeRepository extends JpaRepository<EmailEntity, Long> {

    Optional<EmailEntity> findByEmail(String email);

}
