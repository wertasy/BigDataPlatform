package xinguan.naga.entity.var;

import xinguan.naga.entity.plugin.ParamType;

public interface VariableValue {

  public VariableType getVariableType();

  public Object getValue();

  public void setValue(Object valueString);

  public ParamType getParamType();

  public void applyUpdate(Object value);
}
