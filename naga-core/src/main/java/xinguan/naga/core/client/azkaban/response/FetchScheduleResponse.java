package xinguan.naga.core.client.azkaban.response;

import xinguan.naga.core.client.azkaban.model.Schedule;
import lombok.Data;

@Data
public class FetchScheduleResponse extends BaseResponse {
  private Schedule schedule;
}
