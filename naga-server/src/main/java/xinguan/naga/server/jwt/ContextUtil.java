package xinguan.naga.server.jwt;

import xinguan.naga.entity.system.User;

public class ContextUtil {
  private static ThreadLocal<User> local = new ThreadLocal<>();

  public static User getCurrentUser() {
    return local.get();
  }

  public static void setCurrentUser(User user) {
    local.set(user);
  }
}
