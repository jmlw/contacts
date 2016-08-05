package com.jmlw.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jmlw.domain.TypeValue;
import com.jmlw.domain.enumeration.EntityType;
import com.jmlw.repository.TypeValueRepository;
import com.jmlw.repository.search.TypeValueSearchRepository;
import com.jmlw.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing TypeValue.
 */
@RestController
@RequestMapping("/api")
public class TypeValueResource {

    private final Logger log = LoggerFactory.getLogger(TypeValueResource.class);

    @Inject
    private TypeValueRepository typeValueRepository;

    @Inject
    private TypeValueSearchRepository typeValueSearchRepository;

    /**
     * POST  /type-values : Create a new typeValue.
     *
     * @param typeValue the typeValue to create
     * @return the ResponseEntity with status 201 (Created) and with body the new typeValue, or with status 400 (Bad Request) if the typeValue has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-values",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeValue> createTypeValue(@RequestBody TypeValue typeValue) throws URISyntaxException {
        log.debug("REST request to save TypeValue : {}", typeValue);
        if (typeValue.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("typeValue", "idexists", "A new typeValue cannot already have an ID")).body(null);
        }
        TypeValue result = typeValueRepository.save(typeValue);
        typeValueSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/type-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("typeValue", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /type-values : Updates an existing typeValue.
     *
     * @param typeValue the typeValue to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated typeValue,
     * or with status 400 (Bad Request) if the typeValue is not valid,
     * or with status 500 (Internal Server Error) if the typeValue couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/type-values",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeValue> updateTypeValue(@RequestBody TypeValue typeValue) throws URISyntaxException {
        log.debug("REST request to update TypeValue : {}", typeValue);
        if (typeValue.getId() == null) {
            return createTypeValue(typeValue);
        }
        TypeValue result = typeValueRepository.save(typeValue);
        typeValueSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("typeValue", typeValue.getId().toString()))
            .body(result);
    }

    /**
     * GET  /type-values : get all the typeValues.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of typeValues in body
     */
    @RequestMapping(value = "/type-values",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeValue> getAllTypeValues() {
        log.debug("REST request to get all TypeValues");
        List<TypeValue> typeValues = typeValueRepository.findAll();
        return typeValues;
    }

    @RequestMapping(value = "/type-values/type/{entity}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeValue> getAllTypeValuesForEntity(@PathVariable String entity) {
        log.debug("REST request to get all TypeValues for an entity");
        List<TypeValue> typeValues = typeValueRepository.findAllByEntityType(EntityType.valueOf(entity));
        return typeValues;
    }

    /**
     * GET  /type-values/:id : get the "id" typeValue.
     *
     * @param id the id of the typeValue to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the typeValue, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/type-values/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TypeValue> getTypeValue(@PathVariable Long id) {
        log.debug("REST request to get TypeValue : {}", id);
        TypeValue typeValue = typeValueRepository.findOne(id);
        return Optional.ofNullable(typeValue)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /type-values/:id : delete the "id" typeValue.
     *
     * @param id the id of the typeValue to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/type-values/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteTypeValue(@PathVariable Long id) {
        log.debug("REST request to delete TypeValue : {}", id);
        typeValueRepository.delete(id);
        typeValueSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("typeValue", id.toString())).build();
    }

    /**
     * SEARCH  /_search/type-values?query=:query : search for the typeValue corresponding
     * to the query.
     *
     * @param query the query of the typeValue search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/type-values",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TypeValue> searchTypeValues(@RequestParam String query) {
        log.debug("REST request to search TypeValues for query {}", query);
        return StreamSupport
            .stream(typeValueSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
