package xinguan.naga.server.controller;

import xinguan.naga.entity.privilege.ResourcePrivilege;
import xinguan.naga.entity.system.User;
import xinguan.naga.server.BaseController;
import xinguan.naga.server.jwt.ContextUtil;
import xinguan.naga.server.jwt.LoginRequired;
import xinguan.naga.server.service.PrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/naga/v1/ranger")
@CrossOrigin
public class PrivilegeController extends BaseController {

  @Autowired PrivilegeService privilegeService;

  @GetMapping(value = "privileges")
  @LoginRequired
  public Object getRangerPrivileges(
      @RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex,
      @RequestParam(name = "pageSize", required = true, defaultValue = "50") int pageSize) {
    User user = ContextUtil.getCurrentUser();
    Page<ResourcePrivilege> resourcePrivileges =
        privilegeService.listResourcePrivileges(
            user.getTeam(), pageIndex - 1, pageSize, null, null);
    Map<String, Object> pages = new HashMap<>();
    pages.put("pages", resourcePrivileges.getContent());
    pages.put("pageIndex", pageIndex);
    pages.put("pageSize", pageSize);
    pages.put("pageCount", resourcePrivileges.getTotalPages());
    return asNormalResponse(pages);
  }

  @PostMapping(value = "privilege")
  @LoginRequired
  public Object addRangerPrivilege(@RequestBody ResourcePrivilege resourcePrivilege) {
    privilegeService.addPrivilege(resourcePrivilege);
    return asNormalResponse(true);
  }

  @PutMapping(value = "privilege")
  @LoginRequired
  public Object updateRangerPrivilege(@RequestBody ResourcePrivilege resourcePrivilege) {
    privilegeService.updatePrivilege(resourcePrivilege);
    return asNormalResponse(true);
  }

  @DeleteMapping(value = "privilege")
  @LoginRequired
  public Object deleteRangerPrivilege(@RequestParam long id) {
    privilegeService.delPrivilege(id);
    return asNormalResponse(true);
  }
}
