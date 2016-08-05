package com.jmlw.repository.search;

import com.jmlw.domain.Phone;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Phone entity.
 */
public interface PhoneSearchRepository extends ElasticsearchRepository<Phone, Long> {
}
