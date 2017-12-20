package com.github.h2m.entity;

import org.hibernate.envers.Audited;
import org.springframework.core.style.ToStringCreator;

import javax.persistence.*;

@Entity
@Audited
public class MyEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(nullable = false, length = 10000)
    private String details;

    @Column(nullable = false, length = 2000)
    private String title;


    public MyEntity() {
    }

    public MyEntity(String details, String title) {
        this.details = details;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }
}

