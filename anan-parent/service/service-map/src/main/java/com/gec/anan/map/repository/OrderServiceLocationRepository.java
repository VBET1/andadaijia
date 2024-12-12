package com.gec.anan.map.repository;

import com.gec.anan.model.entity.map.OrderServiceLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderServiceLocationRepository extends MongoRepository<OrderServiceLocation, String> {


    List<OrderServiceLocation> findByOrderIdOrderByCreateTimeAsc(Long orderId);
}