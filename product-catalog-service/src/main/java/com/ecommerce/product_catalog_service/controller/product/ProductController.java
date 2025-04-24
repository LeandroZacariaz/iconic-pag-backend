package com.ecommerce.product_catalog_service.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product_catalog_service.dto.product.ProductCreateMultipartDto;
import com.ecommerce.product_catalog_service.dto.product.ProductDto;
import com.ecommerce.product_catalog_service.service.product.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
@Tag(name = "Productos", description = "Operaciones relacionadas con los productos.")
public class ProductController {
    private ProductService productService;

    @Operation(summary = "Crear un nuevo producto", description = "Crea un producto con un nombre, precio y stock. ")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(@ModelAttribute @Valid ProductCreateMultipartDto productCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productCreateDto));
    }

    @Operation(summary = "Obtener todos los productos con paginación", description = "Devuelve una lista paginada de productos.")
    @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente")
    @GetMapping()
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(productService.getAllProducts(pageable));
    }

    @Operation(summary = "Obtener producto por ID", description = "Devuelve los detalles de un producto por ID.")
    @ApiResponse(responseCode = "200", description = "Producto obtenido exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") Long id_product){
        return ResponseEntity.ok().body(productService.getProductById(id_product));
    }

    @Operation(summary = "Actualizar un producto", description = "Actualiza el contenido de un producto existente por ID")
    @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(@PathVariable("id") Long id_product, @ModelAttribute @Valid ProductCreateMultipartDto productCreateDto){
        return ResponseEntity.ok().body(productService.updateProduct(id_product, productCreateDto));
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto específico por su ID")
    @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id_product){
        productService.deleteProduct(id_product);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
