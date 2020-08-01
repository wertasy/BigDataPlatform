package xinguan.naga.server.visual;

import xinguan.naga.server.query.DataCacheUtil;
import xinguan.naga.server.query.EngineType;
import xinguan.naga.server.query.QueryObject;
import xinguan.naga.server.query.dataframe.ColumnInfo;
import xinguan.naga.server.query.dataframe.DataFrame;
import xinguan.naga.server.query.dataframe.Row;
import xinguan.naga.server.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class ChartDataLoaderImpl implements ChartDataLoader {

  @Autowired
  QueryService queryService;

  @Override
  public ChartData load(ChartSpecific chartSpecific)
      throws ExecutionException, InterruptedException {
    QueryObject queryObject =
        QueryObject.builder()
            .engineType(EngineType.PRESTO)
            .currentUser(chartSpecific.getCreator())
            .pageSize(DataCacheUtil.MaxSize)
            .sql(chartSpecific.getQuerySql())
            .build();
    DataFrame dataFrame = queryService.executeDataFrame(queryObject, 1);
    ChartData chartData = new ChartData();
    chartData.setColumns(chartSpecific.chartSetting.getColumns());
    Iterator<Row> iterator = dataFrame.iterator();
    List<Map<String, Object>> rows = new ArrayList<>();
    while (iterator.hasNext()) {
      Row next = iterator.next();
      Map<String, Object> row = new HashMap<>();
      for (String col : chartSpecific.getChartSetting().getColumns()) {
        row.put(col, next.getValueAs(col));
      }
      rows.add(row);
    }
    chartData.setRows(rows);
    return chartData;
  }

  @Override
  public ChartData loadData(String currentUser, String sql)
      throws ExecutionException, InterruptedException {
    QueryObject queryObject =
        QueryObject.builder()
            .engineType(EngineType.PRESTO)
            .currentUser(currentUser)
            .pageSize(DataCacheUtil.MaxSize)
            .sql(sql)
            .build();
    DataFrame dataFrame = queryService.executeDataFrame(queryObject, 1);
    ChartData chartData = new ChartData();
    List<String> columns =
        dataFrame.getMetaData().getColumns().stream()
            .map(ColumnInfo::getName)
            .collect(Collectors.toList());
    chartData.setColumns(columns);
    Iterator<Row> iterator = dataFrame.iterator();
    List<Map<String, Object>> rows = new ArrayList<>();
    while (iterator.hasNext()) {
      Row next = iterator.next();
      Map<String, Object> row = new HashMap<>();
      for (String col : columns) {
        row.put(col, next.getValueAs(col));
      }
      rows.add(row);
    }
    chartData.setRows(rows);
    return chartData;
  }
}
