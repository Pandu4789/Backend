package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Change findByUsername to findByEmail for login/signup validation
    Optional<User> findByEmail(String email); // For login and signup email validation

    // Old method, consider if still needed or replaced by findByEmail
    // Optional<User> findByUsername(String username);

   @Query("SELECT u FROM User u WHERE u.role = 'PRIEST' " +
       "AND (:name IS NULL OR u.firstName LIKE %:name% OR u.lastName LIKE %:name%) " +
       "AND (:phone IS NULL OR u.phone LIKE %:phone%) " +
       "AND (:poojaType IS NULL OR :poojaType IN ELEMENTS(u.priestDetails.servicesOffered)) " +
       "AND (:id IS NULL OR u.id = :id)")
List<User> findPriestsWithFilters(@Param("name") String name,
                                  @Param("phone") String phone,
                                  @Param("poojaType") String poojaType,
                                  @Param("id") Long id);

    @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' " +
           "AND (:name IS NULL OR u.firstName LIKE %:name% OR u.lastName LIKE %:name%) " +
           "AND (:phone IS NULL OR u.phone LIKE %:phone%) " +
           "AND (:id IS NULL OR u.id = :id)")
    List<User> findCustomersWithFilters(@Param("name") String name,
                                        @Param("phone") String phone,
                                        @Param("id") Long id);
}