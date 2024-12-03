package com.example.tests;

import com.example.models.Product;
import com.example.models.ProductResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FakeStoreAPITest extends BaseTest {

    @Test
    public void testGetAllProducts() {
        when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("$", not(empty()))
                .body("size()", greaterThan(0));
    }

    @Test
    public void testGetProductById() {
        int productId = 1;

        given()
                .pathParam("id", productId)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(productId))
                .body("title", notNullValue())
                .body("price", greaterThan(0.0f));
    }

    @Test
    public void testSearchProducts() {
        String query = "WD 2TB Elements Portable External Hard Drive - USB 3.0";

        List<String> titles = when()
                .get("/products")
                .then()
                .statusCode(200)
                .extract()
                .path("title");

        List<String> filteredTitles = titles.stream()
                .filter(title -> title.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

        assertThat("There are no products containing the word " + query, filteredTitles.size(), greaterThan(0));
    }

    @Test
    public void testCreateProduct() {
        Product.Metadata metadata = new Product.Metadata(
                "Test company",
                "24m"
        );

        Product newProduct = new Product(
                "Test product",
                11.11,
                "Test product description",
                "electronics",
                metadata
        );

        ProductResponse createdProduct = given()
                .header("Content-Type", "application/json")
                .body(newProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(200)
                .extract()
                .as(ProductResponse.class);

        assertThat(createdProduct.getId(), notNullValue());
        assertThat(createdProduct.getTitle(), equalTo("Test product"));
        assertNotNull(createdProduct.getMetadata(), "Field 'metadata' is null");
        assertThat(createdProduct.getMetadata().getManufacturer(), equalTo("Test company"));
        assertThat(createdProduct.getMetadata().getWarranty(), equalTo("24m"));
    }
}
