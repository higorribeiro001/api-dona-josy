package com.donajosy.api.product.service;

import com.donajosy.api.product.entity.Product;
import com.donajosy.api.product.entity.ProductCategory;
import com.donajosy.api.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ProductService {
    @Autowired // injeção de dependencia
    private ProductRepository productRepository;

    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(UUID id) {
        return productRepository.getReferenceById(id);
    }

    public Page<Product> getProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> getProductsByCategoryAndName(ProductCategory category, String name, Pageable pageable) {
        return productRepository.findByCategoryAndName(category, name,  pageable);
    }

    public Page<Product> getProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Product insertProduct(
            Product product,
            String name,
            String description,
            ProductCategory category,
            BigDecimal price,
            MultipartFile image,
            boolean isActive
    ) throws IOException {
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(price);
        product.setActive(isActive);

        String imageUrl = saveImage(image);

        product.setImage(imageUrl);

        return product;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }

    public Product update(UUID id, Product product) {
        if (productRepository.existsById(id)) {
            product.setId(id);
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product with id: " + id + " not found");
        }
    }

    public Product editStatus(UUID id, boolean status) {
        Product product = productRepository.getReferenceById(id);

        if (productRepository.existsById(id)) {
            product.setActive(status);
            return productRepository.save(product);
        } else {
            throw new RuntimeException("Product with id: " + id + " not found");
        }
    }

    public String saveImage(MultipartFile image) throws IOException {
        Path uploadPath = Paths.get("uploads/products/");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = image.getOriginalFilename();

        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + extension;

        Path path = uploadPath.resolve(fileName);

        Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return "uploads/products/" + fileName;
    }
}
