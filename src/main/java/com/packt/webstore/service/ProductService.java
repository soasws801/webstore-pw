package com.packt.webstore.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.packt.webstore.domain.Product;

public interface ProductService {

	List<Product> getAllProducts();
	List<Product> getProductsByCategory(String category);
	Product getProductById(String productID);
	Set<Product> getProductsByFilter(Map<String, List<String>> filterParams);
	void addProduct(Product product);
	List<Product> getProductsByManufacturer(String manufacturer);
	List<Product> getProductsByPrice(float low, float high);
	
}
