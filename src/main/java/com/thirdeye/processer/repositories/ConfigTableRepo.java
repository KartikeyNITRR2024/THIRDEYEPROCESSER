package com.thirdeye.processer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.processer.entity.ConfigTable;

@Repository
public interface ConfigTableRepo extends JpaRepository<ConfigTable, Long> {
}
