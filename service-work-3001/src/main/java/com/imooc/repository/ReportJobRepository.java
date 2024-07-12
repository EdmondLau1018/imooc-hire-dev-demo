package com.imooc.repository;

import com.imooc.pojo.mo.ReportMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportJobRepository extends MongoRepository<ReportMO, String> {

    public ReportMO findByReportUserIdAndJobId(String reportUserId, String jobId);
}
