package xinguan.naga.server.query;

public interface ResultSetWrapper<R, T> {

  public T wrapData(R result);
}
