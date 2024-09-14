package com.joaobisi.productscatalog.services;

import com.joaobisi.productscatalog.domain.aws.MessageDto;
import com.joaobisi.productscatalog.domain.aws.OperationType;
import com.joaobisi.productscatalog.domain.product.Product;
import com.joaobisi.productscatalog.domain.product.ProductDto;
import com.joaobisi.productscatalog.domain.category.exceptions.CategoryNotFoundException;
import com.joaobisi.productscatalog.domain.product.exceptions.ProductNotFoundException;
import com.joaobisi.productscatalog.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final CategoryService categoryService;
    private final ProductRepository repository;
    private final AwsSnsService snsService;

    public ProductService(CategoryService categoryService, ProductRepository repository, AwsSnsService snsService) {
        this.categoryService = categoryService;
        this.repository = repository;
        this.snsService = snsService;
    }

    public Product create(ProductDto productDto) {
        this.categoryService.findById(productDto.categoryId()).orElseThrow(CategoryNotFoundException::new);
        Product newProduct = new Product(productDto);
        this.repository.save(newProduct);

        this.snsService.publish(new MessageDto(newProduct.toJsonString(OperationType.CREATE)));

        return newProduct;
    }

    public List<Product> fetchAll () {
        return this.repository.findAll();
    }

    public Product update(String id, ProductDto productDto) {
        Product product = this.repository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        if(productDto.categoryId() != null) {
            this.categoryService.findById(productDto.categoryId()).orElseThrow(CategoryNotFoundException::new);
            product.setCategoryId(productDto.categoryId());
        }
        if(!productDto.title().isEmpty()) product.setTitle(productDto.title());
        if(!productDto.description().isEmpty()) product.setDescription(productDto.description());
        if(!(productDto.price() == null)) product.setPrice(productDto.price());

        this.repository.save(product);

        this.snsService.publish(new MessageDto(product.toJsonString(OperationType.UPDATE)));

        return product;
    }

    public void delete(String id) {
        Product product = this.repository.findById(id)
                .orElseThrow(ProductNotFoundException::new);

        this.snsService.publish(new MessageDto(product.toJsonString(OperationType.DELETE)));

        this.repository.delete(product);
    }

}
