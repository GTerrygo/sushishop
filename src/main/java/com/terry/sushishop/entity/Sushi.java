package com.terry.sushishop.entity;

import javax.persistence.*;

/**
 * @author
 * @create 2022-01-22-23:35
 */
@Entity
public class Sushi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name="time_to_make")
    private Integer timeToMake;

    public Sushi() {
    }

    public Sushi(String name, Integer timeToMake) {
        this.name = name;
        this.timeToMake = timeToMake;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTimeToMake() {
        return timeToMake;
    }

    public void setTimeToMake(Integer timeToMake) {
        this.timeToMake = timeToMake;
    }
}
