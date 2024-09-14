package com.joaobisi.productscatalog.controllers;


import com.joaobisi.productscatalog.domain.product.Product;
import com.joaobisi.productscatalog.domain.product.ProductDto;
import com.joaobisi.productscatalog.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        Product newProduct = this.service.create(productDto);
        return ResponseEntity.ok().body(newProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> fetchProducts() {
        List<Product> products = this.service.fetchAll();
        return ResponseEntity.ok().body(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody ProductDto productDto) {
        Product product = this.service.update(id, productDto);
        return ResponseEntity.ok().body(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") String id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();

    }
}
