package com.donajosy.api.product.repository;

import com.donajosy.api.product.entity.Product;
import com.donajosy.api.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);

    Page<Product> findByCategoryAndName(ProductCategory category, String name, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
