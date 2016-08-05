package com.jmlw.repository.search;

import com.jmlw.domain.TypeValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the TypeValue entity.
 */
public interface TypeValueSearchRepository extends ElasticsearchRepository<TypeValue, Long> {
}
