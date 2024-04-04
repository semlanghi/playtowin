package com.example.application.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GridOutputAnnotatedRepository
        extends
            JpaRepository<GridOutputAnnotated, Long>,
            JpaSpecificationExecutor<GridOutputAnnotated> {

}
