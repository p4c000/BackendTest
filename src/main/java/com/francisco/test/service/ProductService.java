package com.francisco.test.service;

import com.francisco.test.model.ProductDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class ProductService {

  private final WebClient webClient;

  // Constructor injection
  public ProductService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder
        .baseUrl("http://localhost:3001")
        .build();
  }

  public List<String> getSimilarProductIds(String productId) {
    try {
      String[] ids = webClient.get()
          .uri("/product/{productId}/similarids", productId)
          .retrieve()
          .bodyToMono(String[].class)
          .timeout(Duration.ofSeconds(5))
          .block();

      return ids != null ? List.of(ids) : List.of();
    } catch (Exception e) {
      return List.of();
    }
  }

  public ProductDTO getProductDetail(String productId) {
    try {
      return webClient.get()
          .uri("/product/{productId}", productId)
          .retrieve()
          .bodyToMono(ProductDTO.class)
          .timeout(Duration.ofSeconds(5))
          .block();
    } catch (Exception e) {
      return null;
    }
  }

  public List<ProductDTO> getSimilarProducts(String productId) {
    // 1. Get similar product IDs
    List<String> similarIds = getSimilarProductIds(productId);

    if (similarIds.isEmpty()) {
      return List.of();
    }

    // 2. Fetching all products details in parallel
    //TODO: Implement a proper logger
    //TODO: Discuss the proper response to one of the products not found
    //TODO: Define the timeout

    return Flux.fromIterable(similarIds)
        .flatMap(id -> webClient.get()
            .uri("/product/{productId}", id)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                response -> {
                  System.out.println("Product " + id + " not found (404)");
                  return Mono.empty(); // Expected, skip silently
                }
            )
            .bodyToMono(ProductDTO.class)
            .timeout(Duration.ofSeconds(15))
            .onErrorResume(WebClientResponseException.class, e -> {
              if (e.getStatusCode().is4xxClientError()) {
                System.out.println("Product " + id + " not found (4xx error)");
                return Mono.empty(); // Skip
              } else {
                System.err.println("Server error fetching product " + id + ": " + e.getMessage());
                return Mono.empty(); // Or rethrow for 5xx errors
              }
            })
            .onErrorResume(TimeoutException.class, e -> {
              System.out.println("⚠️ Timeout fetching product " + id);
              return Mono.empty();
            })
            .onErrorResume(e -> {
              System.err.println("❌ Unexpected error fetching product " + id + ": " + e.getMessage());
              return Mono.empty();
            })
        )
        .collectList()
        .block();
  }
}