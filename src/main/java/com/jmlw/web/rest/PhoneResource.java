package com.jmlw.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jmlw.domain.Phone;
import com.jmlw.repository.PhoneRepository;
import com.jmlw.repository.search.PhoneSearchRepository;
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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Phone.
 */
@RestController
@RequestMapping("/api")
public class PhoneResource {
    /* TODO: update all request mappings to enforce row level access using User ID
     *   Currently this is enforced by denying access to any state not nested under Contact Home
     *   in the client. This prevents direct access to any view that will allow modification to this entity
     */

    private final Logger log = LoggerFactory.getLogger(PhoneResource.class);

    @Inject
    private PhoneRepository phoneRepository;

    @Inject
    private PhoneSearchRepository phoneSearchRepository;

    /**
     * POST  /phones : Create a new phone.
     *
     * @param phone the phone to create
     * @return the ResponseEntity with status 201 (Created) and with body the new phone, or with status 400 (Bad Request) if the phone has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phones",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Phone> createPhone(@RequestBody Phone phone) throws URISyntaxException {

        log.debug("REST request to save Phone : {}", phone);
        if (phone.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("phone", "idexists", "A new phone cannot already have an ID")).body(null);
        }
        Phone result = phoneRepository.save(phone);
        phoneSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/phones/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("phone", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /phones : Updates an existing phone.
     *
     * @param phone the phone to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated phone,
     * or with status 400 (Bad Request) if the phone is not valid,
     * or with status 500 (Internal Server Error) if the phone couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/phones",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Phone> updatePhone(@RequestBody Phone phone) throws URISyntaxException {
        log.debug("REST request to update Phone : {}", phone);
        if (phone.getId() == null) {
            return createPhone(phone);
        }
        Phone result = phoneRepository.save(phone);
        phoneSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("phone", phone.getId().toString()))
            .body(result);
    }

    /**
     * GET  /phones : get all the phones.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of phones in body
     */
    @RequestMapping(value = "/phones",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Phone> getAllPhones() {
        log.debug("REST request to get all Phones");
        List<Phone> phones = phoneRepository.findAll();
        return phones;
    }

    /**
     * GET  /phones/:id : get the "id" phone.
     *
     * @param id the id of the phone to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the phone, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/phones/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Phone> getPhone(@PathVariable Long id) {
        log.debug("REST request to get Phone : {}", id);
        Phone phone = phoneRepository.findOne(id);
        return Optional.ofNullable(phone)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/phones/contact/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Phone> getPhoneByContact(@PathVariable Long id) {
        log.debug("REST request to get Phone : {}", id);
        List<Phone> phones = phoneRepository.findByContactId(id);
        return phones;
    }

    /**
     * DELETE  /phones/:id : delete the "id" phone.
     *
     * @param id the id of the phone to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/phones/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        log.debug("REST request to delete Phone : {}", id);
        phoneRepository.delete(id);
        phoneSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("phone", id.toString())).build();
    }

    /**
     * SEARCH  /_search/phones?query=:query : search for the phone corresponding
     * to the query.
     *
     * @param query the query of the phone search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/phones",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Phone> searchPhones(@RequestParam String query) {
        log.debug("REST request to search Phones for query {}", query);
        return StreamSupport
            .stream(phoneSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
