package com.jmlw.repository.search;

import com.jmlw.domain.EmailAddress;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the EmailAddress entity.
 */
public interface EmailAddressSearchRepository extends ElasticsearchRepository<EmailAddress, Long> {
}
