package com.terry.sushishop.service;

import com.terry.sushishop.entity.Sushi;
import com.terry.sushishop.repository.SushiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author
 * @create 2022-01-22-23:40
 */
@Service
public class SushiService {
    @Autowired
    private SushiRepository sushiRepository;
    public Sushi findSushiByName(String name){
        return sushiRepository.findByName(name);
    }
    public Sushi findSushiById(Integer id){
        Optional<Sushi> optionalSushi = sushiRepository.findById(id);
        return optionalSushi.get();
    }
}
