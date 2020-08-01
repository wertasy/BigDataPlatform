package xinguan.naga.server.var.model;

import xinguan.naga.entity.plugin.ParamType;

import java.util.List;
import java.util.Map;

public class CheckPointValue extends AbstractVariableValue {

  private Object value;
  private ParamType paramType;

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public void setValue(Object valueString) {
    this.value = valueString;
  }

  @Override
  public ParamType getParamType() {
    return paramType;
  }

  public void setParamType(ParamType paramType) {
    this.paramType = paramType;
  }

  @Override
  public void applyUpdate(Object value) {
    if (paramType.equals(ParamType.ARRAY)) {
      List list = (List) value;
      if (list.size() == 0) {
        return;
      }
    } else if (paramType.equals(ParamType.MAP)) {
      Map map = (Map) value;
      if (map.size() == 0) {
        return;
      }
    }
    this.value = value;
  }
}
