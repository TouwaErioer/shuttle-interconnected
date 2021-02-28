package com.example.hope.elasticsearch.repository;

import com.example.hope.model.entity.Store;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface StoreRepository extends ElasticsearchRepository<Store, Long> {

    List<Store> queryStoreByName(String name);
}
