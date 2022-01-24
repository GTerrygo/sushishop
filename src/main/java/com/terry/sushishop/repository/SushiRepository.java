package com.terry.sushishop.repository;

import com.terry.sushishop.entity.Sushi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author
 * @create 2022-01-22-23:39
 */
@Repository
public interface SushiRepository extends JpaRepository<Sushi, Integer> {
    Sushi findByName(String name);
}
