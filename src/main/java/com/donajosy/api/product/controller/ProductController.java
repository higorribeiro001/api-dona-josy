package com.donajosy.api.product.controller;

import com.donajosy.api.product.dto.CreateProductRequest;
import com.donajosy.api.product.entity.Product;
import com.donajosy.api.product.entity.ProductCategory;
import com.donajosy.api.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping
    public Page<Product> findAll(
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        if (category != null && name != null) {
            return productService.getProductsByCategoryAndName(category, name, pageable);
        }

        if (category != null) {
            return productService.getProductsByCategory(category, pageable);
        }

        if (name != null) {
            return productService.getProductsByName(name, pageable);
        }

        return productService.getProducts(pageable);
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable UUID id) {
        return productService.getProductById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Product save(@Valid @ModelAttribute CreateProductRequest request) throws IOException {
        Product product = productService.insertProduct(
                new Product(),
                request.name(),
                request.description(),
                request.category(),
                request.price(),
                request.image(),
                Boolean.TRUE.equals(request.isActive())
        );

        return productService.save(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product update(
            @PathVariable UUID id,
            @Valid @ModelAttribute CreateProductRequest request
    ) throws IOException {
        Product product = productService.getProductById(id);
        product = productService.insertProduct(
                product,
                request.name(),
                request.description(),
                request.category(),
                request.price(),
                request.image(),
                Boolean.TRUE.equals(request.isActive())
        );

        return productService.update(id, product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}")
    public Product updateStatus(
            @PathVariable UUID id,
            @RequestBody boolean status
    ) throws IOException {
        return productService.editStatus(id, status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        productService.deleteById(id);
    }
}


