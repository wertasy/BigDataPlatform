package xinguan.naga.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "xinguan.naga.*")
@EntityScan(basePackages = "xinguan.naga.entity")
@EnableJpaRepositories(basePackages = "xinguan.naga.repository")
@Slf4j
@EnableScheduling
public class NagaApplication {
  public static void main(String[] args) {
    SpringApplication.run(NagaApplication.class);
  }
}
