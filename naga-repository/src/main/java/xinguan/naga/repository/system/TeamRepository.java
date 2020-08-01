package xinguan.naga.repository.system;

import xinguan.naga.entity.system.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
  Team findOneByName(String name);
}
