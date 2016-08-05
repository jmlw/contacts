package com.jmlw.repository;

import com.jmlw.domain.Address;

import com.jmlw.domain.Contact;
import org.springframework.data.jpa.repository.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Address entity.
 */
@SuppressWarnings("unused")
public interface AddressRepository extends JpaRepository<Address,Long> {

//    Set<Address> findByContact(Contact contact);

    List<Address> findByContactId(long id);

    void deleteByContactId(Long contactId);
}
