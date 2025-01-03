package com.thirdeye.processer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thirdeye.processer.entity.MicroservicesInfo;

@Repository
public interface MicroservicesInfoRepo extends JpaRepository<MicroservicesInfo, Long> {
	MicroservicesInfo getByMicroserviceName(String microserviceName);
}
