package xinguan.naga.server;

import xinguan.naga.core.exception.ErrorCodes;
import xinguan.naga.core.exception.NagaException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class BaseController {
  protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @ExceptionHandler
  @ResponseBody
  public Map<String, Object> exceptionHandle(Exception ex, HttpServletResponse response) {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Credentials", "true");
    // 检测exception是否为nagaexception
    ex.printStackTrace();
    if (NagaException.class.isAssignableFrom(ex.getClass())) {
      NagaException ng = (NagaException) ex;
      // 返回
      return asNormalResponse(ng.getErrorCode(), ng.getErrorMessage(), null);
    } else {
      // 返回
      return asErrorResponse(ErrorCodes.SYSTEM_EXCEPTION, ex.getMessage());
    }
  }

  protected Map<String, Object> asNormalResponse(Object data) {
    return asNormalResponse(null, data, null);
  }

  protected Map<String, Object> asPagedResponse(
      List dataList, int pageIndex, int offset, int count, boolean hasMore) {
    Map<String, Object> map = this.asNormalResponse(dataList);
    map.put("pageIndex", pageIndex);
    map.put("start", offset);
    map.put("stop", offset + count);
    map.put("hasMore", hasMore);
    return map;
  }

  protected Map<String, Object> asErrorResponse(int errCode, String errMsg) {
    return asNormalResponse(errCode, errMsg, null);
  }

  protected Map<String, Object> asNormalResponse(
      Integer code, Object data, Map<String, Object> extraMap) {
    String currentTime = sdf.format(new Date());
    HashMap<String, Object> result = new HashMap<>();
    result.put("currentTime", currentTime);
    if (code == null || code.equals(ErrorCodes.SYSTEM_SUCCESS)) {
      result.put("code", ErrorCodes.SYSTEM_SUCCESS);
      result.put("data", data);
    } else {
      result.put("code", code);
      result.put("msg", data);
    }

    if (extraMap != null && !extraMap.isEmpty()) {
      result.putAll(extraMap);
    }
    return result;
  }
}
