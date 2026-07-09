package com.HiveGroup.HiveRH.Common.Security.Config;

import com.HiveGroup.HiveRH.Common.Utils.Enums.RolEnum;
import com.HiveGroup.HiveRH.Common.Utils.Enums.StatusEnum;
import com.HiveGroup.HiveRH.Features.Account.AccountEntity;
import com.HiveGroup.HiveRH.Features.Account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "hiverh.bootstrap-admin", name = "enabled", havingValue = "true")
public class BootstrapAdminConfiguration {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${hiverh.bootstrap-admin.user:admin}")
    private String user;

    @Value("${hiverh.bootstrap-admin.email:admin@hiverh.local}")
    private String email;

    @Value("${hiverh.bootstrap-admin.password:}")
    private String password;

    @Bean
    public CommandLineRunner bootstrapAdmin() {
        return args -> {
            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                        "BOOTSTRAP_ADMIN_PASSWORD es obligatorio cuando BOOTSTRAP_ADMIN_ENABLED=true"
                );
            }

            if (accountRepository.findByUserOrEmail(user, email).isPresent()) {
                return;
            }

            AccountEntity admin = AccountEntity.builder()
                    .user(user)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .rol(RolEnum.ADMIN)
                    .statusEnum(StatusEnum.ACTIVE)
                    .build();

            accountRepository.save(admin);
        };
    }
}
