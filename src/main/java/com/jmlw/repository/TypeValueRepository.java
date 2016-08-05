package com.jmlw.repository;

import com.jmlw.domain.TypeValue;

import com.jmlw.domain.enumeration.EntityType;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the TypeValue entity.
 */
@SuppressWarnings("unused")
public interface TypeValueRepository extends JpaRepository<TypeValue,Long> {

    List<TypeValue> findAllByEntityType(EntityType entity);
}
