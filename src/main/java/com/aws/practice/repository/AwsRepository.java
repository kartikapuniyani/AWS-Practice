package com.aws.practice.repository;

import com.aws.practice.domain.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwsRepository extends JpaRepository<ServiceRecord, Long> {

    ServiceRecord findByNameAndType(String name, String type);
}
