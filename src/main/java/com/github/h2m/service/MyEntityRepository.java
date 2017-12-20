package com.github.h2m.service;

import com.github.h2m.entity.MyEntity;
import org.springframework.data.repository.Repository;

interface MyEntityRepository extends Repository<MyEntity, Long> {


    MyEntity save(MyEntity review);
}
