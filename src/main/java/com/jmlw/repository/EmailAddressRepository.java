package com.jmlw.repository;

import com.jmlw.domain.Contact;
import com.jmlw.domain.EmailAddress;

import org.springframework.data.jpa.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the EmailAddress entity.
 */
@SuppressWarnings("unused")
public interface EmailAddressRepository extends JpaRepository<EmailAddress,Long> {

//    List<EmailAddress> findByContactInOrderById(Collection<Contact> contacts);
//
//    EmailAddress findTop1ByContact(Contact contact);
//
//    Set<EmailAddress> findByContact(Contact contact);

    List<EmailAddress> findByContactIdInOrderById(Collection<Long> contactIds);

    EmailAddress findTop1ByContactId(long contactId);

    List<EmailAddress> findByContactId(long contactId);

    void deleteByContactId(Long contactId);
}
