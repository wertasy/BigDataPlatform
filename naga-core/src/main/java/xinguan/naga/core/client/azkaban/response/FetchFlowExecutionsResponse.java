package xinguan.naga.core.client.azkaban.response;

import xinguan.naga.core.client.azkaban.model.Execution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchFlowExecutionsResponse extends BaseResponse {
  private String project;
  private String projectId;
  private String flow;
  private Integer from;
  private Integer length;
  private Integer total;
  private List<Execution> executions;
}
