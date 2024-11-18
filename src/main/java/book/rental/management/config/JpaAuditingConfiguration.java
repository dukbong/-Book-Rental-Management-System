package book.rental.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/***
 * Controller TestCode를 위해 분리
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration {
}
