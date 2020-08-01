package xinguan.naga.entity.privilege;

import lombok.Data;

import java.util.List;

@Data
public class HiveResource extends Resource {
  List<String> policys;
  private String database;
  private String table;
  private String columns = "*";
}
