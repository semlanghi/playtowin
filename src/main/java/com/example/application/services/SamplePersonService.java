package com.example.application.services;

import com.example.application.data.SampleInput;
import com.example.application.data.SampleInputRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class SamplePersonService {

    private final SampleInputRepository repository;

    public SamplePersonService(SampleInputRepository repository) {
        this.repository = repository;
    }

    public Optional<SampleInput> get(Long id) {
        return repository.findById(id);
    }

    public SampleInput update(SampleInput entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SampleInput> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<SampleInput> list(Pageable pageable, Specification<SampleInput> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
