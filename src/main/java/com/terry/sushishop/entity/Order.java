package com.terry.sushishop.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.terry.sushishop.util.DateToLongSerializer;
import org.springframework.data.annotation.CreatedDate;
import javax.persistence.*;
import java.util.Date;

/**
 * @author
 * @create 2022-01-22-23:42
 */
@Entity
@Table(name="sushi_order")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="status_id")
    private Integer statusId;
    @Column(name="sushi_id")
    private Integer sushiId;
    @Column(name="timeSpent")
    private Long timeSpent;
    @Column(name = "createdAt")
    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createdAt;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getSushiId() {
        return sushiId;
    }

    public void setSushiId(Integer sushiId) {
        this.sushiId = sushiId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public Long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Long timeSpent) {
        this.timeSpent = timeSpent;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return (Order)super.clone();
    }
}
