package xinguan.naga.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import xinguan.naga.entity.cluster.HdfsSummary;
import xinguan.naga.entity.cluster.QueueMetrics;
import xinguan.naga.entity.cluster.YarnSummary;
import xinguan.naga.server.BaseController;
import xinguan.naga.server.jwt.LoginRequired;
import xinguan.naga.server.schedule.YarnStatusCache;
import xinguan.naga.server.service.MonitorService;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/naga/v1/monitor")
@CrossOrigin
public class MonitorController extends BaseController {

  @Autowired MonitorService monitorService;

  @Autowired YarnStatusCache yarnStatusCache;

  @GetMapping(value = "/cluster/metrics")
  @LoginRequired
  public Object getMetrics() throws ExecutionException {
    return asNormalResponse(yarnStatusCache.getLoadingCache().get("metrics"));
  }

  @GetMapping(value = "/cluster/scheduler")
  @LoginRequired
  public Object getScheduler() throws ExecutionException {
    return asNormalResponse(yarnStatusCache.getLoadingCache().get("scheduler"));
  }

  @LoginRequired
  @GetMapping(value = "/cluster/apps")
  public Object getApps(
      @RequestParam(defaultValue = "RUNNING") String states,
      @RequestParam(defaultValue = "1000") Integer limit)
      throws ExecutionException {
    long start = System.currentTimeMillis();

    String key = String.format("apps_%s_%s", states, limit);
    JSONObject result = (JSONObject) yarnStatusCache.getLoadingCache().get(key);
    long stop = System.currentTimeMillis();
    result.put("startTimeStamp", start);
    result.put("returnTimeStamp", stop);
    result.put("elapseMillisecond", stop - start);
    return asNormalResponse(result);
  }

  @LoginRequired
  @GetMapping(value = "/storage")
  public Object getHdfsSummary() {
    return asNormalResponse(monitorService.findHdfsSummary(DateUtil.toIntSecond(new Date())));
  }

  @LoginRequired
  @GetMapping(value = "/calc")
  public Object getYarnSummary() {
    return asNormalResponse(monitorService.findYarnSummary(DateUtil.toIntSecond(new Date())));
  }

  @LoginRequired
  @GetMapping(value = "/storage/chart")
  public Object getHdfsSummaryList() {
    long current = System.currentTimeMillis();
    long zero = current - TimeZone.getDefault().getRawOffset();
    List<HdfsSummary> hdfsSummaryBetween =
        monitorService.findHdfsSummaryBetween((int) (zero / 1000), (int) (current / 1000));
    List<String> columns =
        Arrays.stream(FieldUtils.getAllFields(HdfsSummary.class))
            .map(Field::getName)
            .collect(Collectors.toList());
    Map<String, Object> data = new HashMap<>();
    data.put("rows", hdfsSummaryBetween);
    data.put("columns", columns);
    return asNormalResponse(data);
  }

  @LoginRequired
  @GetMapping(value = "/calc/chart")
  public Object getYarnSummaryList() {
    long current = System.currentTimeMillis();
    long zero = current - TimeZone.getDefault().getRawOffset();

    List<YarnSummary> yarnSummaryBetween =
        monitorService.findYarnSummaryBetween((int) (zero / 1000), (int) (current / 1000));

    List<String> columns =
        Arrays.stream(FieldUtils.getAllFields(YarnSummary.class))
            .map(Field::getName)
            .collect(Collectors.toList());

    Map<String, Object> data = new HashMap<>();
    data.put("columns", columns);
    data.put("rows", yarnSummaryBetween);
    return asNormalResponse(data);
  }

  @LoginRequired
  @GetMapping(value = "/calc/queue")
  public Object getQueueMetrics() {
    Date now = new Date();
    now.setSeconds(0);
    List<QueueMetrics> queueMetrics = monitorService.findQueueMetrics((int) (now.getTime() / 1000));
    List<String> columns =
        Arrays.stream(FieldUtils.getAllFields(QueueMetrics.class))
            .map(Field::getName)
            .collect(Collectors.toList());
    Map<String, Object> data = new HashMap<>();
    data.put("rows", queueMetrics);
    data.put("columns", columns);
    return asNormalResponse(data);
  }
}
