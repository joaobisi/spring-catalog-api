package com.joaobisi.productscatalog.domain.product;

import com.joaobisi.productscatalog.domain.aws.OperationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    private String id;

    private String title;

    private String description;

    private float price;

    private String ownerId;

    private String categoryId;

    public Product(ProductDto productDto) {
        this.title = productDto.title();
        this.description = productDto.description();
        this.price = productDto.price();
        this.ownerId = productDto.ownerId();
        this.categoryId = productDto.categoryId();
    }

    public String toJsonString(OperationType operationType) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", this.id);
        jsonObject.put("title", this.title);
        jsonObject.put("description", this.description);
        jsonObject.put("ownerId", this.ownerId);
        jsonObject.put("categoryId", this.categoryId);
        jsonObject.put("type", "product");
        jsonObject.put("operationType", operationType);

        return jsonObject.toString();
    }
}
