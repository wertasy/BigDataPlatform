package xinguan.naga.core.client.azkaban.response;

import xinguan.naga.core.client.azkaban.model.Flow;
import lombok.Data;

import java.util.List;

@Data
public class FetchFlowsResponse extends BaseResponse {
  private String project;
  private String projectId;
  private List<Flow> flows;
}
