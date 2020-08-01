package xinguan.naga.server.query;

import xinguan.naga.server.query.dataframe.DataFrame;

public interface ResultSetDataFrameWrapper<R> extends ResultSetWrapper<R, DataFrame> {

  public DataFrame wrapData(R result);
}
