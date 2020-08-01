package xinguan.naga.repository.system;

import xinguan.naga.entity.system.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findOneByName(String name);
}
