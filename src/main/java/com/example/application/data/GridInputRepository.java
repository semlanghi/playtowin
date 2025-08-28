package com.example.application.data;


import com.example.application.polyflow.datatypes.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GridInputRepository
        extends
            JpaRepository<Tuple, Long>,
            JpaSpecificationExecutor<Tuple> {

}
