package com.joaobisi.productscatalog.domain.category;

import com.joaobisi.productscatalog.domain.aws.OperationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    private String id;

    private String title;

    private String description;

    private String ownerId;

    public Category(CategoryDto categoryDto) {
        this.title = categoryDto.title();
        this.description = categoryDto.description();
        this.ownerId = categoryDto.ownerId();
    }

    public String toJsonString(OperationType operationType) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", this.id);
        jsonObject.put("title", this.title);
        jsonObject.put("description", this.description);
        jsonObject.put("ownerId", this.ownerId);
        jsonObject.put("type", "category");
        jsonObject.put("operationType", operationType);

        return jsonObject.toString();
    }
}
