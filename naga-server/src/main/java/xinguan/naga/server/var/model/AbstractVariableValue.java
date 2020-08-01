package xinguan.naga.server.var.model;

import xinguan.naga.entity.var.VariableType;
import xinguan.naga.entity.var.VariableValue;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public abstract class AbstractVariableValue implements VariableValue {

  protected VariableType variableType;
}
