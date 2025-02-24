package cz.cvut.fit.tjv.bar.Repository;

import cz.cvut.fit.tjv.bar.Model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFirstNameAndLastName (String firstName, String lastName);

    @Query("""
           SELECT c FROM Customer c
           JOIN Order o ON o.customer.id = c.id
           GROUP BY c.id
           ORDER BY COUNT(o.id) DESC
           LIMIT 1
           """)
    Optional<Customer> findBestCustomerByOrderCount();

    @Query("""
           SELECT c FROM Customer c
           JOIN Order o ON o.customer.id = c.id
           WHERE o.user.id = :userId
           GROUP BY c.id
           ORDER BY COUNT(o.id) DESC
           LIMIT 1
           """)
    Optional<Customer> findBestCustomerByUserId(@Param("userId") Long userId);
}
