package xinguan.naga.core.client.ranger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.auth.BasicAuthRequestInterceptor;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import xinguan.naga.core.client.ranger.api.PolicyApis;
import xinguan.naga.core.client.ranger.api.PolicyFeignClient;
import xinguan.naga.core.client.ranger.api.UserApis;
import xinguan.naga.core.client.ranger.api.UserFeignClient;
import xinguan.naga.core.client.ranger.config.RangerClientConfig;
import xinguan.naga.core.client.ranger.util.RangerErrorDecoder;
import xinguan.naga.core.client.ranger.util.RangerHeadersInterceptor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RangerClient {

  private static final ObjectMapper mapper =
      new ObjectMapper()
          .setSerializationInclusion(JsonInclude.Include.NON_NULL)
          .configure(SerializationFeature.INDENT_OUTPUT, true)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  private static final JacksonEncoder encoder = new JacksonEncoder(mapper);
  private static final JacksonDecoder decoder = new JacksonDecoder(mapper);
  @Getter private UserApis users;
  @Getter private PolicyApis policies;
  @Setter private RangerClientConfig clientConfig;
  public RangerClient(RangerClientConfig rangerClientConfig) {
    this.clientConfig = rangerClientConfig;
    this.initialize();
  }

  public void initialize() {
    users = new UserApis(feignBuilder().target(UserFeignClient.class, clientConfig.getUrl()));
    policies =
        new PolicyApis(feignBuilder().target(PolicyFeignClient.class, clientConfig.getUrl()));
  }

  private Feign.Builder feignBuilder() {
    return Feign.builder()
        .logger(new Logger.JavaLogger())
        .logLevel(clientConfig.getLogLevel())
        .options(
            new Request.Options(
                clientConfig.getConnectTimeoutMillis(), clientConfig.getReadTimeoutMillis()))
        .encoder(encoder)
        .decoder(decoder)
        .client(new OkHttpClient())
        .errorDecoder(new RangerErrorDecoder())
        .requestInterceptor(new RangerHeadersInterceptor())
        .requestInterceptor(
            new BasicAuthRequestInterceptor(
                clientConfig.getAuthConfig().getUsername(),
                clientConfig.getAuthConfig().getPassword()));
  }
}
