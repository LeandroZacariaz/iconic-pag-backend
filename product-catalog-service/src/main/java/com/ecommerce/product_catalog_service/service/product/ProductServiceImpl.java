package com.ecommerce.product_catalog_service.service.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.product_catalog_service.domain.Product;
import com.ecommerce.product_catalog_service.domain.ProductImage;
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

        // Inicializar la lista de imágenes
        List<ProductImage> images = new ArrayList<>();
        if (productCreateDto.images() != null && !productCreateDto.images().isEmpty()) {
            for (MultipartFile image : productCreateDto.images()) {
                if (!image.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(uploadResult.get("url").toString());
                        productImage.setProduct(product);
                        images.add(productImage);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage());
                    }
                }
            }
        }
        product.setImages(images);

        Product savedProduct = productRepository.save(product);
        return productMapper.productToProductDto(savedProduct);
    }
    
    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::productToProductDto).toList();
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

        // Actualizar imágenes solo si se envían nuevas
        if (productCreateDto.images() != null && !productCreateDto.images().isEmpty()) {
            product.getImages().clear(); // Eliminar imágenes existentes solo si hay nuevas
            for (MultipartFile image : productCreateDto.images()) {
                if (!image.isEmpty()) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(uploadResult.get("url").toString());
                        productImage.setProduct(product);
                        product.getImages().add(productImage);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al subir la imagen a Cloudinary: " + e.getMessage());
                    }
                }
            }
        } // Si no se envían imágenes, se mantienen las existentes

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
