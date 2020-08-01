package xinguan.naga.server.service;

import xinguan.naga.entity.visual.ChartInfo;
import xinguan.naga.entity.visual.ChartType;
import xinguan.naga.server.visual.ChartInfoVo;
import xinguan.naga.server.visual.ChartSpecific;

import java.io.IOException;

public interface ChartService {
  // 多是对于图表的增删改查
  void saveChart(ChartInfoVo chartInfo) throws IOException;

  void saveChartInfo(ChartInfo chartInfo) throws IOException;

  ChartInfo getChartById(long id);

  void deleteChart(long id);

  void updateChart(ChartInfoVo chartInfo) throws IOException;

  ChartSpecific convertChartSpecific(String chartSpecific, ChartType chartType) throws IOException;

  // 获取图表chartspecific

}
