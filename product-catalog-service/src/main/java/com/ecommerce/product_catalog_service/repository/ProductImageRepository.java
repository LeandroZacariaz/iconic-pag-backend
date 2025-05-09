package com.ecommerce.product_catalog_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.product_catalog_service.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{

}
