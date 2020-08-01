package xinguan.naga.repository.privilege;

import xinguan.naga.entity.privilege.ResourcePrivilege;
import xinguan.naga.entity.privilege.ResourceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcePrivilegeRepository extends JpaRepository<ResourcePrivilege, Long> {
  ResourcePrivilege findOneByTeamAndResourceType(String team, ResourceType resourceType);
}
