package com.dugx.event.repository;

import com.dugx.event.domain.Address;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Address entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaSpecificationExecutor<Address> {
    boolean existsByLocationIgnoreCase(String location);

    boolean existsByLocationIgnoreCaseAndIdNot(String location, Long id);
}
