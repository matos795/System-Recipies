package com.MyRecipies.recipies.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.MyRecipies.recipies.entities.Recipe;

public class RecipeDTO {

    // Dados do produto
    private String productName;
    private Double productPrice;
    private String imgUrl;

    // Dados da receita
    private Long id;
    private String description;
    private Integer amount;
    private LocalDate createDate;
    private LocalDateTime lastUpdateDate;

    // Itens da receita
    private List<RecipeItemDTO> items;

    public RecipeDTO() {

    }

    public RecipeDTO(String productName, Double productPrice, String imgUrl, Long id, String description,
            Integer amount, LocalDate createDate, LocalDateTime lastUpdateDate, List<RecipeItemDTO> items) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.imgUrl = imgUrl;
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.items = items;
    }

    public RecipeDTO(Recipe entity) {
        productName = entity.getProduct().getName();
        productPrice = entity.getProduct().getPrice();
        imgUrl = entity.getProduct().getImgUrl();
        id = entity.getId();
        description = entity.getDescription();
        amount = entity.getAmount();
        createDate = entity.getProduct().getCreateDate();
        lastUpdateDate = entity.getLastUpdateDate();
        items = entity.getItems().stream().map(x -> new RecipeItemDTO(x)).collect(Collectors.toList());
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<RecipeItemDTO> getItems() {
        return items;
    }

    public void setItems(List<RecipeItemDTO> items) {
        this.items = items;
    }

    
}
