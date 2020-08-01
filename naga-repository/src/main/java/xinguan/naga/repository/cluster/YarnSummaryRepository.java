package xinguan.naga.repository.cluster;

import xinguan.naga.entity.cluster.YarnSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface YarnSummaryRepository extends JpaRepository<YarnSummary, Long> {
  YarnSummary findTop1ByIsTrashFalseAndCreateTimeLessThanEqualOrderByCreateTimeDesc(
      Integer selectTime);

  List<YarnSummary> findByIsTrashFalseAndCreateTimeBetweenOrderByCreateTimeAsc(
      Integer startTime, Integer endTime);
}
