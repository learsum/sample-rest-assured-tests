package com.example.tests;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        String newProduct = "{ \"title\": \"Test product\", \"price\": 11.11, \"description\": \"test product\"}";

        given()
                .header("Content-Type", "application/json")
                .body(newProduct)
                .when()
                .post("/products")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("Test product"));
    }
}
