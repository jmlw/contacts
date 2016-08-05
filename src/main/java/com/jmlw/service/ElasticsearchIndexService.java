package com.jmlw.service;

import com.codahale.metrics.annotation.Timed;
import com.jmlw.domain.*;
import com.jmlw.repository.*;
import com.jmlw.repository.search.*;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    @Inject
    private AddressRepository addressRepository;

    @Inject
    private AddressSearchRepository addressSearchRepository;

    @Inject
    private ContactRepository contactRepository;

    @Inject
    private ContactSearchRepository contactSearchRepository;

    @Inject
    private EmailAddressRepository emailAddressRepository;

    @Inject
    private EmailAddressSearchRepository emailAddressSearchRepository;

    @Inject
    private PhoneRepository phoneRepository;

    @Inject
    private PhoneSearchRepository phoneSearchRepository;

    @Inject
    private TypeValueRepository typeValueRepository;

    @Inject
    private TypeValueSearchRepository typeValueSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserSearchRepository userSearchRepository;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Address.class, addressRepository, addressSearchRepository);
        reindexForClass(Contact.class, contactRepository, contactSearchRepository);
        reindexForClass(EmailAddress.class, emailAddressRepository, emailAddressSearchRepository);
        reindexForClass(Phone.class, phoneRepository, phoneSearchRepository);
        reindexForClass(TypeValue.class, typeValueRepository, typeValueSearchRepository);
        reindexForClass(User.class, userRepository, userSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional
    @SuppressWarnings("unchecked")
    private <T extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, Long> jpaRepository,
                                                          ElasticsearchRepository<T, Long> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.save((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.save(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
