package com.joaobisi.productscatalog.services;

import com.joaobisi.productscatalog.domain.aws.MessageDto;
import com.joaobisi.productscatalog.domain.aws.OperationType;
import com.joaobisi.productscatalog.domain.category.Category;
import com.joaobisi.productscatalog.domain.category.CategoryDto;
import com.joaobisi.productscatalog.repositories.CategoryRepository;
import com.joaobisi.productscatalog.domain.category.exceptions.CategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final AwsSnsService snsService;

    public CategoryService(CategoryRepository repository, AwsSnsService snsService) {
        this.repository = repository;
        this.snsService = snsService;
    }

    public Category create(CategoryDto categoryDto) {
        Category newCategory = new Category(categoryDto);

        snsService.publish(new MessageDto(newCategory.toJsonString(OperationType.CREATE)));

        this.repository.save(newCategory);
        return newCategory;
    }

    public List<Category> fetchAll () {
        return this.repository.findAll();
    }

    public Optional<Category> findById(String id) {
        return this.repository.findById(id);
    }

    public Category update(String id, CategoryDto categoryDto) {
        Category category = this.repository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);

        if(!categoryDto.title().isEmpty()) category.setTitle(categoryDto.title());
        if(!categoryDto.description().isEmpty()) category.setDescription(categoryDto.description());

        snsService.publish(new MessageDto(category.toJsonString(OperationType.UPDATE)));

        this.repository.save(category);

        return category;
    }

    public void delete(String id) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException::new);

        snsService.publish(new MessageDto(category.toJsonString(OperationType.DELETE)));

        this.repository.delete(category);
    }

}
