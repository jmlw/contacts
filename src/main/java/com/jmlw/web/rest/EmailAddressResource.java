package com.jmlw.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.jmlw.domain.EmailAddress;
import com.jmlw.repository.EmailAddressRepository;
import com.jmlw.repository.search.EmailAddressSearchRepository;
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
 * REST controller for managing EmailAddress.
 */
@RestController
@RequestMapping("/api")
public class EmailAddressResource {
    /* TODO: update all request mappings to enforce row level access using User ID
     *   Currently this is enforced by denying access to any state not nested under Contact Home
     *   in the client. This prevents direct access to any view that will allow modification to this entity
     */

    private final Logger log = LoggerFactory.getLogger(EmailAddressResource.class);

    @Inject
    private EmailAddressRepository emailAddressRepository;

    @Inject
    private EmailAddressSearchRepository emailAddressSearchRepository;

    /**
     * POST  /email-addresses : Create a new emailAddress.
     *
     * @param emailAddress the emailAddress to create
     * @return the ResponseEntity with status 201 (Created) and with body the new emailAddress, or with status 400 (Bad Request) if the emailAddress has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-addresses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailAddress> createEmailAddress(@RequestBody EmailAddress emailAddress) throws URISyntaxException {
        log.debug("REST request to save EmailAddress : {}", emailAddress);
        if (emailAddress.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("emailAddress", "idexists", "A new emailAddress cannot already have an ID")).body(null);
        }
        EmailAddress result = emailAddressRepository.save(emailAddress);
        emailAddressSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/email-addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("emailAddress", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /email-addresses : Updates an existing emailAddress.
     *
     * @param emailAddress the emailAddress to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated emailAddress,
     * or with status 400 (Bad Request) if the emailAddress is not valid,
     * or with status 500 (Internal Server Error) if the emailAddress couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/email-addresses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailAddress> updateEmailAddress(@RequestBody EmailAddress emailAddress) throws URISyntaxException {
        log.debug("REST request to update EmailAddress : {}", emailAddress);
        if (emailAddress.getId() == null) {
            return createEmailAddress(emailAddress);
        }
        EmailAddress result = emailAddressRepository.save(emailAddress);
        emailAddressSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("emailAddress", emailAddress.getId().toString()))
            .body(result);
    }

    /**
     * GET  /email-addresses : get all the emailAddresses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of emailAddresses in body
     */
    @RequestMapping(value = "/email-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<EmailAddress> getAllEmailAddresses() {
        log.debug("REST request to get all EmailAddresses");
        List<EmailAddress> emailAddresses = emailAddressRepository.findAll();
        return emailAddresses;
    }

    /**
     * GET  /email-addresses/:id : get the "id" emailAddress.
     *
     * @param id the id of the emailAddress to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the emailAddress, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/email-addresses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EmailAddress> getEmailAddress(@PathVariable Long id) {
        log.debug("REST request to get EmailAddress : {}", id);
        EmailAddress emailAddress = emailAddressRepository.findOne(id);
        return Optional.ofNullable(emailAddress)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/email-addresses/contact/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<EmailAddress> getAllEmailAddresses(@PathVariable Long id) {
        log.debug("REST request to get all EmailAddresses");
        List<EmailAddress> emailAddresses = emailAddressRepository.findByContactId(id);
        return emailAddresses;
    }

    /**
     * DELETE  /email-addresses/:id : delete the "id" emailAddress.
     *
     * @param id the id of the emailAddress to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/email-addresses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteEmailAddress(@PathVariable Long id) {
        log.debug("REST request to delete EmailAddress : {}", id);
        emailAddressRepository.delete(id);
        emailAddressSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("emailAddress", id.toString())).build();
    }

    /**
     * SEARCH  /_search/email-addresses?query=:query : search for the emailAddress corresponding
     * to the query.
     *
     * @param query the query of the emailAddress search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/email-addresses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<EmailAddress> searchEmailAddresses(@RequestParam String query) {
        log.debug("REST request to search EmailAddresses for query {}", query);
        return StreamSupport
            .stream(emailAddressSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
