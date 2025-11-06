package com.demo.updating_brain.repository;

import com.demo.updating_brain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN Order o ON u.id = o.userId WHERE o.id = :orderId")
    User findByOrderId(@Param("orderId") Long orderId);
}
