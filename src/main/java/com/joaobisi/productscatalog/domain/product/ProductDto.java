package com.joaobisi.productscatalog.domain.product;

public record ProductDto(String title, String description, Integer price, String ownerId, String categoryId) {
}
