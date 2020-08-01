package xinguan.naga.repository.query;

import xinguan.naga.entity.query.SavedSql;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSqlRepository extends JpaRepository<SavedSql, Long> {
  List<SavedSql> findByCreator(String creator);
}
