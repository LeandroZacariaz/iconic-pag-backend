package com.ecommerce.product_catalog_service.service.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ecommerce.product_catalog_service.dto.product.ProductCreateMultipartDto;
import com.ecommerce.product_catalog_service.dto.product.ProductDto;

public interface ProductService {
    ProductDto createProduct(ProductCreateMultipartDto productCreateDto);
    Page<ProductDto> getAllProducts(Pageable pageable);
    ProductDto getProductById(Long id);
    ProductDto updateProduct(Long id, ProductCreateMultipartDto productUpdateDto);
    void deleteProduct(Long id);
}
