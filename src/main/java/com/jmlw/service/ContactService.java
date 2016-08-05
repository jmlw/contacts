package com.jmlw.service;

import com.jmlw.domain.*;
import com.jmlw.repository.*;
import com.jmlw.repository.search.ContactSearchRepository;
import com.jmlw.security.AuthoritiesConstants;
import com.jmlw.security.SecurityUtils;
import com.jmlw.web.rest.dto.ContactDTO;
import com.jmlw.web.rest.dto.ContactDetailDTO;
import com.jmlw.web.rest.mapper.ContactDetailMapper;
import com.jmlw.web.rest.mapper.ContactMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Contact.
 */
@Service
@Transactional
public class ContactService {
    /* TODO: update all request mappings to enforce row level access using User ID
     *   Currently this is enforced by denying access to any state not nested under Contact Home
     *   in the client. This prevents direct access to any view that will allow modification to this entity
     *
     *   Additionally for Contact, the list of contacts provided is limited to the current user's User ID
     *   so this will prevent direct GUI access to contacts outside of the current user's context
     */

    private final Logger log = LoggerFactory.getLogger(ContactService.class);

    @Inject
    private ContactRepository contactRepository;

    @Inject
    private ContactMapper contactMapper;
    @Inject
    private ContactDetailMapper contactDetailMapper;

    @Inject
    private ContactSearchRepository contactSearchRepository;

    @Inject
    private EmailAddressRepository emailAddressRepository;
    @Inject
    private PhoneRepository phoneRepository;
    @Inject
    private AddressRepository addressRepository;
    @Inject
    private UserRepository userRepository;

    /**
     * Save a contact.
     *
     * @param contactDTO the entity to save
     * @return the persisted entity
     */
    public ContactDTO save(ContactDTO contactDTO) {
        log.debug("Request to save Contact : {}", contactDTO);
        Contact contact = contactMapper.contactDTOToContact(contactDTO);

        if (contact.getUserId() == null) {
            contact.setUserId(getUserId());
        }
        contact = contactRepository.save(contact);

        Long contactId = contact.getId();
        contact.getPhoneNumbers().forEach(phone -> {
            phone.setContactId(contactId);
        });
        contact.getAddresses().forEach(address -> {
            address.setContactId(contactId);
        });
        contact.getEmailAddresses().forEach(email -> {
            email.setContactId(contactId);
        });

        ContactDTO result = contactMapper.contactToContactDTO(contact);
        contactSearchRepository.save(contact);
        return result;
    }

    /**
     * Get all the contacts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Contact> findAll(Pageable pageable) {
        log.debug("Request to get all Contacts");
        Page<Contact> result = new PageImpl<Contact>(new ArrayList<Contact>());
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            result = contactRepository.findAll(pageable);
        } else {
            Long userId = getUserId();
            if (userId != null) {
                result = contactRepository.findByUserId(userId, pageable);
            }
        }
        List<Long> contactIds = new ArrayList<>();
        result.forEach(contact -> contactIds.add(contact.getId()));

        Map<Long, EmailAddress> emailAddresses = new HashMap<>();
        Map<Long, Phone> phones = new HashMap<>();
        emailAddressRepository.findByContactIdInOrderById(contactIds).forEach(res -> {
            if (!emailAddresses.containsKey(res.getContactId())) {
                emailAddresses.put(res.getContactId(), res);
            }
        });
        phoneRepository.findByContactIdInOrderById(contactIds).forEach(res -> {
            if (!phones.containsKey(res.getContactId())) {
                phones.put(res.getContactId(), res);
            }
        });

        result.forEach(res -> {
            Phone phone = phones.getOrDefault(res.getId(), null);
            EmailAddress email = emailAddresses.getOrDefault(res.getId(), null);
            if (email != null)
                res.setEmailAddress(email.getEmail());
            if (phone != null)
                res.setPhoneNumber(phone.getPhoneNumber());
        });
        return result;
    }

    private Long getUserId() {
        Optional<User> optUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        return (Long) optUser.map(User::getId).orElse(null);
    }

    /**
     * Get one contact by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ContactDTO findOne(Long id) {
        log.debug("Request to get Contact : {}", id);
        Contact contact = contactRepository.findOne(id);
        ContactDTO contactDTO = contactMapper.contactToContactDTO(contact);
        return contactDTO;
    }

    /**
     * Get one contact by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public ContactDetailDTO findOneForDetail(Long id) {
        log.debug("Request to get Contact : {}", id);
        Contact contact = contactRepository.findOne(id);
        ContactDetailDTO contactDetailDTO = contactDetailMapper.contactToContactDetailDTO(contact);
        if (contact != null) {
            Set<Address> addresses = new HashSet<>();
            addressRepository.findByContactId(contact.getId()).forEach(addresses::add);
            Set<EmailAddress> emails = new HashSet<>();
            emailAddressRepository.findByContactId(contact.getId()).forEach(emails::add);
            Set<Phone> phones = new HashSet<>();
            phoneRepository.findByContactId(contact.getId()).forEach(phones::add);

            contactDetailDTO.setAddresses(addresses);

            contactDetailDTO.setEmailAddresses(emails);

            contactDetailDTO.setPhoneNumbers(phones);
        }
        return contactDetailDTO;
    }

    /**
     * Delete the  contact by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Contact : {}", id);
        //cascade the delete to dependent objects
        phoneRepository.deleteByContactId(id);
        emailAddressRepository.deleteByContactId(id);
        addressRepository.deleteByContactId(id);

        contactRepository.delete(id);
        contactSearchRepository.delete(id);
    }

    /**
     * Search for the contact corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Contact> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Contacts for query {}", query);
        return contactSearchRepository.search(queryStringQuery(query), pageable);
    }
}
