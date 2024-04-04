package com.example.application.services;

import com.example.application.data.StockInput;
import com.example.application.data.StockInputRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SampleStockService {

    private final StockInputRepository repository;

    public SampleStockService(StockInputRepository repository) {
        this.repository = repository;
    }

    public Optional<StockInput> get(Long id) {
        return repository.findById(id);
    }

    public StockInput update(StockInput entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<StockInput> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<StockInput> list(Pageable pageable, Specification<StockInput> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
