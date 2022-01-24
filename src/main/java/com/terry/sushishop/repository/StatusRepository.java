package com.terry.sushishop.repository;

import com.terry.sushishop.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @create 2022-01-23-0:41
 */
@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
}
