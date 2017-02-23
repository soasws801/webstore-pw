package com.packt.webstore.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.packt.webstore.domain.Product;

public interface ProductRepository {

	List <Product> getAllProducts();
	List <Product> getProductsByCategory(String category);
	List<Product>getProductsByManufacturer(String manufacturer);
	List<Product>getProductsByPriceFilter(BigDecimal low, BigDecimal high);
	Product getProductById(String productID);
	Set<Product> getProductsByFilter(Map<String, List<String>> filterParams);
	void addProduct(Product product);
}
