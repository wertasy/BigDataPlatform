package xinguan.naga.repository.system;

import xinguan.naga.entity.system.PrivilegeType;
import xinguan.naga.entity.system.SystemPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemPrivilegeRepository extends JpaRepository<SystemPrivilege, Long> {
  SystemPrivilege findOneByTeamAndPrivilegeType(String team, PrivilegeType privilegeType);
}
