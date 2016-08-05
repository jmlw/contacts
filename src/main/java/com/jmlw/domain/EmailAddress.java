package com.jmlw.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A EmailAddress.
 */
@Entity
@Table(name = "email_address")
@Document(indexName = "emailaddress")
public class EmailAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "email")
    @NotNull
    private String email;

    @ManyToOne
    private TypeValue type;

//    @ManyToOne
//    private Contact contact;

    @Column(name = "contact_id")
    private Long contactId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TypeValue getType() {
        return type;
    }

    public void setType(TypeValue typeValue) {
        this.type = typeValue;
    }

//    public Contact getContact() {
//        return contact;
//    }
//
//    public void setContact(Contact contact) {
//        this.contact = contact;
//    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailAddress emailAddress = (EmailAddress) o;
        if(emailAddress.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, emailAddress.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EmailAddress{" +
            "id=" + id +
            ", email='" + email + "'" +
            '}';
    }
}
