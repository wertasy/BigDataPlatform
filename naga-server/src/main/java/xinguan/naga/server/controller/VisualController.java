package xinguan.naga.server.controller;

import cn.hutool.core.date.DateUtil;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import xinguan.naga.entity.visual.ChartInfo;
import xinguan.naga.entity.visual.Dashboard;
import xinguan.naga.server.BaseController;
import xinguan.naga.server.jwt.ContextUtil;
import xinguan.naga.server.jwt.LoginRequired;
import xinguan.naga.server.log.OperationObj;
import xinguan.naga.server.log.OperationRecord;
import xinguan.naga.server.service.ChartService;
import xinguan.naga.server.service.DashBoardService;
import xinguan.naga.server.visual.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/naga/v1/visual")
@CrossOrigin
public class VisualController extends BaseController {
  @Autowired DashBoardService dashBoardService;
  @Autowired ChartService chartService;
  @Autowired
  ChartDataLoader chartDataLoader;

  @ResponseBody
  @GetMapping("dashboards")
  @LoginRequired
  public Object listDashboards(
      @RequestParam(name = "pageIndex", required = true, defaultValue = "1") int pageIndex,
      @RequestParam(name = "pageSize", required = true, defaultValue = "20") int pageSize) {
    Page<Dashboard> dashboards =
        dashBoardService.listDashboards(pageIndex - 1, pageSize, null, null);
    Map<String, Object> pages = new HashMap<>();
    pages.put("pages", dashboards.getContent());
    pages.put("pageIndex", pageIndex);
    pages.put("pageSize", pageSize);
    pages.put("pageCount", dashboards.getTotalPages());
    return asNormalResponse(pages);
  }

  @ResponseBody
  @GetMapping("dashboard/name")
  @LoginRequired
  public Object listDashboardNames() {
    List<Map<String, Object>> dashboardNames = dashBoardService.listDashboardNames();
    return asNormalResponse(dashboardNames);
  }

  @ResponseBody
  @PostMapping("dashboard")
  @LoginRequired
  @OperationRecord("添加仪表盘")
  public Object createDashboard(@RequestBody @OperationObj Dashboard dashboard) {
    dashboard.setCreateTime(DateUtil.toIntSecond(new Date()));
    dashboard.setTrash(false);
    dashboard.setCreator(ContextUtil.getCurrentUser().getName());
    dashBoardService.createDashBoard(dashboard);
    return asNormalResponse(true);
  }

  @ResponseBody
  @PutMapping("dashboard")
  @LoginRequired
  public Object updateDashboard(@RequestBody Dashboard dashboard) {
    dashboard.setCreateTime(DateUtil.toIntSecond(new Date()));
    dashBoardService.updateDashBoard(dashboard);
    return asNormalResponse(true);
  }

  @ResponseBody
  @GetMapping("dashboard")
  public Object getDashboard(@RequestParam Long id) {
    Map<String, Object> layout = new HashMap<>();
    layout.put("layout", dashBoardService.getDashBoard(id));

    return asNormalResponse(layout);
  }

  @ResponseBody
  @DeleteMapping("dashboard")
  @LoginRequired
  @OperationRecord("删除仪表盘")
  public Object delDashboard(@RequestParam @OperationObj Long id) {
    dashBoardService.deleteDashBoard(id);
    return asNormalResponse(true);
  }

  @ResponseBody
  @PostMapping("chart")
  @LoginRequired
  @OperationRecord("添加图表")
  public Object createChart(@RequestBody @OperationObj ChartInfoVo chartInfoVo) throws IOException {
    chartService.saveChart(chartInfoVo);
    return asNormalResponse(true);
  }

  @ResponseBody
  @PutMapping("chart")
  @LoginRequired
  public Object updateChart(@RequestBody ChartInfoVo chartInfoVo) throws IOException {
    chartService.updateChart(chartInfoVo);
    return asNormalResponse(true);
  }

  @ResponseBody
  @PutMapping("chart/size")
  @LoginRequired
  public Object updateChartSize(@RequestParam long id, @RequestParam int w, @RequestParam int h)
      throws IOException {
    ChartInfo chartById = chartService.getChartById(id);
    chartById.getLocation().setWidgetW(w);
    chartById.getLocation().setWidgetH(h);
    chartService.saveChartInfo(chartById);
    return asNormalResponse(true);
  }

  @ResponseBody
  @PutMapping("chart/location")
  @LoginRequired
  public Object updateChartLocation(@RequestParam long id, @RequestParam int x, @RequestParam int y)
      throws IOException {
    ChartInfo chartById = chartService.getChartById(id);
    chartById.getLocation().setWidgetX(x);
    chartById.getLocation().setWidgetY(y);
    chartService.saveChartInfo(chartById);
    return asNormalResponse(true);
  }

  @ResponseBody
  @GetMapping("chart")
  public Object getChart(@RequestParam Long id) throws Exception {
    ChartInfo chartById = chartService.getChartById(id);
    ChartSpecific chartSpecific =
        chartService.convertChartSpecific(chartById.getChartSpecific(), chartById.getChartType());
    ChartData chartData = chartDataLoader.load(chartSpecific);

    Map<String, Object> data = new HashMap<>();
    data.put("chartInfo", chartById);
    data.put("chartData", chartData);
    data.put("chartSettings", chartSpecific.getChartSetting());
    return asNormalResponse(data);
  }

  @ResponseBody
  @GetMapping("chart/data")
  @LoginRequired
  public Object getChartData(@RequestParam String sql) throws Exception {
    ChartData chartData = chartDataLoader.loadData(ContextUtil.getCurrentUser().getName(), sql);
    return asNormalResponse(chartData);
  }

  @ResponseBody
  @GetMapping("chart/setting")
  @LoginRequired
  public Object getChartSetting(@RequestParam String type) throws Exception {
    Class emptyChartSetting = EmptyChartSettingFactory.getEmptyChartSetting(type);
    List<Map<String, Object>> columnFields =
        Arrays.stream(FieldUtils.getAllFields(emptyChartSetting))
            .map(
                field -> {
                  Map<String, Object> column = new HashMap<>();
                  column.put("name", field.getName());
                  column.put("isArray", field.getType().isArray());
                  column.put("value", field.getType().isArray() ? new ArrayList<>() : "");
                  return column;
                })
            .collect(Collectors.toList());
    return asNormalResponse(columnFields);
  }

  @ResponseBody
  @DeleteMapping("chart")
  @LoginRequired
  @OperationRecord("删除图表")
  public Object delChart(@RequestParam @OperationObj Long id) {
    chartService.deleteChart(id);
    return asNormalResponse(true);
  }
}
