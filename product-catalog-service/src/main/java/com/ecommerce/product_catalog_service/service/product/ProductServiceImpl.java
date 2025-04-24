package com.ecommerce.product_catalog_service.service.product;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.product_catalog_service.domain.Product;

import com.ecommerce.product_catalog_service.dto.product.ProductCreateMultipartDto;
import com.ecommerce.product_catalog_service.dto.product.ProductDto;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.mappers.product.ProductMapper;
import com.ecommerce.product_catalog_service.repository.CategoryRepository;
import com.ecommerce.product_catalog_service.repository.ProductRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private ProductMapper productMapper;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private Cloudinary cloudinary;
    
    @Override
    public ProductDto createProduct(ProductCreateMultipartDto productCreateDto) {
        Product product = new Product();
        product.setName(productCreateDto.name());
        product.setDescription(productCreateDto.description());
        product.setPrice(productCreateDto.price());
        product.setStock(productCreateDto.stock());
        product.setCategory(categoryRepository.findByName(productCreateDto.name_category())
            .orElseThrow(() -> new ResourceNotFoundException("La categoría con nombre: " + productCreateDto.name_category() + " no existe.")));

        // Manejar la imagen con Cloudinary
        if (productCreateDto.image() != null && !productCreateDto.image().isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(productCreateDto.image().getBytes(), ObjectUtils.emptyMap());
                product.setImage(uploadResult.get("url").toString()); // Guardar la URL de Cloudinary
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage());
            }
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.productToProductDto(savedProduct);
    }
    
    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::productToProductDto);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return productRepository.findById(id).map(productMapper::productToProductDto)
                                .orElseThrow(() -> 
                                new ResourceNotFoundException("El producto con ID: " + id + " no existe."));
    }

    @Override
    public ProductDto updateProduct(Long id_product, ProductCreateMultipartDto productCreateDto) {
        Product product = productRepository.findById(id_product)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id_product));

        product.setName(productCreateDto.name());
        product.setDescription(productCreateDto.description());
        product.setPrice(productCreateDto.price());
        product.setStock(productCreateDto.stock());
        product.setCategory(categoryRepository.findByName(productCreateDto.name_category())
            .orElseThrow(() -> new ResourceNotFoundException("La categoría con nombre: " + productCreateDto.name_category() + " no existe.")));

        // Manejar la imagen con Cloudinary
        if (productCreateDto.image() != null && !productCreateDto.image().isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(productCreateDto.image().getBytes(), ObjectUtils.emptyMap());
                product.setImage(uploadResult.get("url").toString()); // Actualizar la URL de Cloudinary
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage());
            }
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.productToProductDto(updatedProduct);
    }


    @Override
    public void deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        }else{
            throw new ResourceNotFoundException("El producto con ID: " +id+" no existe." );
        }
    }

}
