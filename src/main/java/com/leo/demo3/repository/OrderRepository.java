package com.leo.demo3.repository;

import com.leo.demo3.model.Order;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

// 替换 JpaRepository 为 ListCrudRepository
public interface OrderRepository extends ListCrudRepository<Order, Long> {

    // 自定义方法签名保持不变，Spring Data JDBC 会自动实现
    Optional<Order> findByOrderKey(String orderKey);
}
