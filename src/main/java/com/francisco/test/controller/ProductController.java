package com.francisco.test.controller;

import com.francisco.test.model.ProductDTO;
import com.francisco.test.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

  @Autowired
  private ProductService productService;

  @GetMapping("/{productId}/similar")
  public ResponseEntity<List<ProductDTO>> getSimilarProducts(@PathVariable String productId) {
    List<ProductDTO> similarProducts = productService.getSimilarProducts(productId);
    return ResponseEntity.ok(similarProducts);
  }
}