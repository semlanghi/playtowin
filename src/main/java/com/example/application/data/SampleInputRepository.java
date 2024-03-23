package com.example.application.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SampleInputRepository
        extends
            JpaRepository<SampleInput, Long>,
            JpaSpecificationExecutor<SampleInput> {

}
