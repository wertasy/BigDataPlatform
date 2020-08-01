package xinguan.naga.core;

import com.google.common.base.Strings;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.client.HdfsAdmin;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HadoopClient {
  private final Configuration conf;
  private final String hiveMetaStoreUris;
  private final UserGroupInformation userGroupInformation;
  private final String proxyUser; // 最大权限用户代理真实用户访问hadoop

  public HadoopClient(String proxyUser, String hadoopConfPath, String hiveMetaStoreUris) {
    this.proxyUser = proxyUser;
    this.hiveMetaStoreUris = hiveMetaStoreUris;
    userGroupInformation = UserGroupInformation.createRemoteUser(proxyUser);
    this.conf = new Configuration();
    conf.addResource(new Path(String.format("%s/hdfs-site.xml", hadoopConfPath)));
    conf.addResource(new Path(String.format("%s/core-site.xml", hadoopConfPath)));
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    HadoopClient hadoopClient =
        new HadoopClient(
            "hadoop", "D:/tools/hadoop/etc/hadoop", "thrift://hadoop.canhui.wang:9083");

    FileSystem fileSystem = hadoopClient.getFileSystem(null, "hdfs://hadoop.canhui.wang:9000");

    //    FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/"));
    //    Arrays.stream(fileStatuses)
    //        .forEach(
    //            fileStatus -> {
    //              System.out.println(fileStatus.getPath().getName());
    //            });
    //
    //    List<String> db01 = hadoopClient.showTables("db01");
    //    System.out.println(db01);
    //
    //    List<Map<String, String>> tableSchemas = hadoopClient.getTableSchemas("db01", "log_dev2");
    //    System.out.println(tableSchemas);

    System.out.println("Create Database:");

    if (!fileSystem.exists(new Path("my_test_db1"))) {
      fileSystem.mkdirs(new Path("my_test_db1"));
    }

    hadoopClient.createDataBase("my_test_db1", null, "我的测试数据库", null);
  }

  public <T> T doPrivileged(PrivilegedExceptionAction<T> action, String realUser)
      throws IOException, InterruptedException {
    // 若传入的realUser为空则使用最大权限用户进行操作
    if (Strings.isNullOrEmpty(realUser)) {
      return userGroupInformation.doAs(action);
    }
    UserGroupInformation proxyUser =
        UserGroupInformation.createProxyUser(realUser, userGroupInformation);
    return proxyUser.doAs(action);
  }

  public <T> T doPrivileged(PrivilegedAction<T> action, String realUser)
      throws IOException, InterruptedException {
    if (Strings.isNullOrEmpty(realUser)) {
      return userGroupInformation.doAs(action);
    }
    UserGroupInformation proxyUser =
        UserGroupInformation.createProxyUser(realUser, userGroupInformation);
    return proxyUser.doAs(action);
  }

  public FileSystem getFileSystem(String realUser, String hdfsUri)
      throws IOException, InterruptedException {
    return doPrivileged(
        (PrivilegedExceptionAction<FileSystem>)
            () -> FileSystem.newInstance(URI.create(hdfsUri), conf),
        realUser);
  }

  // 设置配额需要使用HdfsAdmin
  public HdfsAdmin getHdfsAdmin(String hdfsUri) throws IOException, InterruptedException {
    return doPrivileged(
        (PrivilegedExceptionAction<HdfsAdmin>) () -> new HdfsAdmin(URI.create(hdfsUri), conf),
        null);
  }

  private HiveMetaStoreClient getHiveMetaStoreClient() throws MetaException {
    HiveConf hiveConf = new HiveConf();
    hiveConf.setIntVar(HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES, 3);
    hiveConf.setVar(HiveConf.ConfVars.METASTOREURIS, hiveMetaStoreUris);
    return new HiveMetaStoreClient(hiveConf);
  }

  // 创建hive数据库
  public Object createDataBase(String name, String dbPath, String desc, String realUser)
      throws IOException, InterruptedException {
    return doPrivileged(
        (PrivilegedExceptionAction<Object>)
            () -> {
              HiveMetaStoreClient hiveMetaStoreClient = null;
              try {
                hiveMetaStoreClient = getHiveMetaStoreClient();
                Database db = new Database();
                db.setName(name);
                db.setDescription(desc);
                db.setOwnerName(realUser);
                db.setOwnerType(PrincipalType.USER);
                db.setLocationUri(dbPath);
                hiveMetaStoreClient.createDatabase(db);
              } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
              } finally {
                if (hiveMetaStoreClient != null) {
                  hiveMetaStoreClient.close();
                }
              }
              return null;
            },
        realUser);
  }

  public List<String> showTables(String dbName) {
    HiveMetaStoreClient hiveMetaStoreClient = null;
    try {
      hiveMetaStoreClient = getHiveMetaStoreClient();
      return hiveMetaStoreClient.getAllTables(dbName);
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaStoreClient.close();
      }
    }
  }

  public List<Map<String, String>> getTableSchemas(String dbName, String tableName) {
    HiveMetaStoreClient hiveMetaStoreClient = null;
    try {
      hiveMetaStoreClient = getHiveMetaStoreClient();

      List<FieldSchema> schema = hiveMetaStoreClient.getSchema(dbName, tableName);
      return schema.stream()
          .map(
              col -> {
                Map<String, String> colInfo = new HashMap<>();
                colInfo.put("name", col.getName());
                colInfo.put("type", col.getType());
                colInfo.put("comment", col.getComment());
                return colInfo;
              })
          .collect(Collectors.toList());
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    } finally {
      if (hiveMetaStoreClient != null) {
        hiveMetaStoreClient.close();
      }
    }
  }
}
