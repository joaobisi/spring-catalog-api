package com.joaobisi.productscatalog.controllers;

import com.joaobisi.productscatalog.domain.category.Category;
import com.joaobisi.productscatalog.domain.category.CategoryDto;
import com.joaobisi.productscatalog.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        Category newCategory = this.service.create(categoryDto);
        return ResponseEntity.ok().body(newCategory);
    }

    @GetMapping
    public ResponseEntity<List<Category>> fetchCategories() {
        List<Category> categories = this.service.fetchAll();
        return ResponseEntity.ok().body(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") String id, @RequestBody CategoryDto categoryDto) {
        Category category = this.service.update(id, categoryDto);
        return ResponseEntity.ok().body(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory (@PathVariable("id") String id) {
        this.service.delete(id);
        return ResponseEntity.noContent().build();

    }
}
