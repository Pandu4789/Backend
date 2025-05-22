package com.example.myapp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.role = 'priest' " +
           "AND (:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:phone IS NULL OR u.phone LIKE %:phone%) " +
           "AND (:poojaType IS NULL OR EXISTS (SELECT 1 FROM u.poojas p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :poojaType, '%'))))")
    List<User> findPriestsWithFilters(@Param("name") String name,
                                      @Param("phone") String phone,
                                      @Param("poojaType") String poojaType,
                                      @Param("id") Long id);
                                      
 @Query("SELECT u FROM User u WHERE u.role = 'CUSTOMER' "
     + "AND (:name IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) "
     + "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) "
     + "AND (:phone IS NULL OR u.phone LIKE CONCAT('%', :phone, '%')) "
     + "AND (:id IS NULL OR u.id = :id)")
List<User> findCustomersWithFilters(@Param("name") String name,
                                    @Param("phone") String phone,
                                    @Param("id") Long id);

}
