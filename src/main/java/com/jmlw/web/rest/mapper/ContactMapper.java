package com.jmlw.web.rest.mapper;

import com.jmlw.domain.*;
import com.jmlw.web.rest.dto.ContactDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Contact and its DTO ContactDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ContactMapper {

    ContactDTO contactToContactDTO(Contact contact);

    List<ContactDTO> contactsToContactDTOs(List<Contact> contacts);

    @Mapping(target = "phoneNumbers", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "emailAddresses", ignore = true)
    Contact contactDTOToContact(ContactDTO contactDTO);

    List<Contact> contactDTOsToContacts(List<ContactDTO> contactDTOs);
}
