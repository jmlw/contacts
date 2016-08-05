package com.jmlw.repository;

import com.jmlw.domain.Contact;
import com.jmlw.domain.Phone;

import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Phone entity.
 */
@SuppressWarnings("unused")
public interface PhoneRepository extends JpaRepository<Phone,Long> {

//    List<Phone> findByContactInOrderById(List<Contact> contacts);
//
//    Phone findTop1ByContact(Contact contact);
//
//    Set<Phone> findByContact(Contact contact);

    List<Phone> findByContactIdInOrderById(List<Long> contactIds);

    Phone findTop1ByContactId(long contactId);

    List<Phone> findByContactId(long contactId);

    void deleteByContactId(Long contactId);
}
