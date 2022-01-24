package com.terry.sushishop.repository;

import com.terry.sushishop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @create 2022-01-22-23:51
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
