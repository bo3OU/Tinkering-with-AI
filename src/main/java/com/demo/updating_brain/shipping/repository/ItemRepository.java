package com.demo.updating_brain.shipping.repository;

import com.demo.updating_brain.shipping.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOrderId(Long orderId);
}
