package com.example.application.services;

import com.example.application.data.GridInput;
import com.example.application.data.GridInputRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SampleGridService {

    private final GridInputRepository repository;

    public SampleGridService(GridInputRepository repository) {
        this.repository = repository;
    }

    public Optional<GridInput> get(Long id) {
        return repository.findById(id);
    }

    public GridInput update(GridInput entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<GridInput> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<GridInput> list(Pageable pageable, Specification<GridInput> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
