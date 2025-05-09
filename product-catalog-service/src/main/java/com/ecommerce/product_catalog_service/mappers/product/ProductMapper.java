package com.ecommerce.product_catalog_service.mappers.product;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ecommerce.product_catalog_service.domain.Product;
import com.ecommerce.product_catalog_service.dto.product.ProductCreateDto;
import com.ecommerce.product_catalog_service.dto.product.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryName", expression = "java(product.getCategory() != null ? product.getCategory().getName() : null)")
    @Mapping(target = "imageUrls", expression = "java(product.getImages() != null ? product.getImages().stream().map(com.ecommerce.product_catalog_service.domain.ProductImage::getImageUrl).toList() : java.util.Collections.emptyList())")
    ProductDto productToProductDto(Product product);

    @Mapping(target = "id_product", ignore = true)
    @Mapping(source = "name_category", target = "category.name")
    @Mapping(target = "images", ignore = true) // Ignorar imágenes porque se manejan en el servicio
    Product productCreateDtoToProduct(ProductCreateDto productDto);
}