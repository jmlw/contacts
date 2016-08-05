package com.jmlw.web.rest.mapper;

import com.jmlw.domain.Contact;
import com.jmlw.web.rest.dto.ContactDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity Contact and its DTO ContactDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ContactDetailMapper {

    ContactDetailDTO contactToContactDetailDTO(Contact contact);

    List<ContactDetailDTO> contactsToContactDetailDTOs(List<Contact> contacts);

//    @Mapping(target = "phoneNumbers", ignore = true)
//    @Mapping(target = "addresses", ignore = true)
//    @Mapping(target = "emailAddresses", ignore = true)
    Contact contactDetailDTOToContact(ContactDetailDTO contactDetailDTO);

    List<Contact> contactDetailDTOsToContacts(List<ContactDetailDTO> contacDetailtDTOs);
}
