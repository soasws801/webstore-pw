package com.packt.webstore.domain.repository.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.packt.webstore.domain.Product;
import com.packt.webstore.domain.repository.ProductRepository;

@Repository
public class InMemoryProductRepository implements ProductRepository {

	private List<Product> listOfProducts = new ArrayList<Product>();

	public InMemoryProductRepository() {
		Product iphone = new Product("P1234", "iPhone 5s", new BigDecimal(500));
		iphone.setDescription("Apple iPhone 5s smartphone with 4.00-inch "
				+ "640x1136 display and 8-megapixel rear camera");
		iphone.setCategory("Smart Phone");
		iphone.setManufacturer("Apple");
		iphone.setUnitsInStock(1000);

		Product laptop_dell = new Product("P1235", "Dell Inspiron",
				new BigDecimal(700));
		laptop_dell.setDescription("Dell Inspiron 14-inch Laptop (Black) "
				+ "with 3rd generation Intel Core Processors");
		laptop_dell.setCategory("Laptop");
		laptop_dell.setManufacturer("Dell");
		laptop_dell.setUnitsInStock(1000);

		Product tablet_Nexus = new Product("P1236", "Nexus 7", new BigDecimal(
				300));
		tablet_Nexus
				.setDescription("Google Nexus 7 is the lightest 7 inch tablet with"
						+ "a quad-core Qualcomm Snapdragon™ S4 Pro processor");
		tablet_Nexus.setCategory("Tablet");
		tablet_Nexus.setManufacturer("Google");
		tablet_Nexus.setUnitsInStock(1000);

		listOfProducts.add(iphone);
		listOfProducts.add(laptop_dell);
		listOfProducts.add(tablet_Nexus);
	}

	public List<Product> getAllProducts() {
		return listOfProducts;
	}

	public Product getProductById(String productId) {
		Product productById = null;

		for (Product product : listOfProducts) {
			if (product != null && product.getProductId() != null
					&& product.getProductId().equals(productId)) {
				productById = product;
				break;
			}
		}

		if (productById == null) {
			throw new IllegalArgumentException(
					"No products found with the product id: " + productId);
		}
		return productById;
	}

	public List<Product> getProductsByCategory(String category) {
		List<Product> productsByCategory = new ArrayList<Product>();

		for (Product product : listOfProducts) {
			if (category.equalsIgnoreCase(product.getCategory())) {
				productsByCategory.add(product);
			}
		}
		return productsByCategory;
	}

	public Set<Product> getProductsByFilter(
			Map<String, List<String>> filterParams) {
		Set<Product> productsByBrand = new HashSet<Product>();
		Set<Product> productsByCategory = new HashSet<Product>();

		Set<String> criteria = filterParams.keySet();

		if (criteria.contains("brand")) {
			for (String brandName : filterParams.get("brand")) {
				for (Product product : listOfProducts) {
					if (brandName.equalsIgnoreCase(product.getManufacturer())) {
						productsByBrand.add(product);
					}
				}
			}
		}

		if (criteria.contains("category")) {
			for (String category : filterParams.get("category")) {
				productsByCategory.addAll(this.getProductsByCategory(category));
			}
		}

		productsByCategory.retainAll(productsByBrand);

		return productsByCategory;
	}

	public List<Product> getProductsByManufacturer(String manufacturer) {
		List<Product> productsByManufacturer = new ArrayList<Product>();

		for (Product product : listOfProducts) {
			if (manufacturer.equalsIgnoreCase(product.getManufacturer())) {
				productsByManufacturer.add(product);
			}
		}
		return productsByManufacturer;
	}

	public List<Product> getProductsByPriceFilter(BigDecimal low,
			BigDecimal high) {
		List<Product> productsByPriceFilter = new ArrayList<Product>();

		for (Product product : listOfProducts) {
			if (low.compareTo(product.getUnitPrice()) <= 0
					&& high.compareTo(product.getUnitPrice()) >= 0) {
				productsByPriceFilter.add(product);
			}
		}
		return productsByPriceFilter;
	}

	public Set<Product> filterProducts(BigDecimal lowPrice,
			BigDecimal highPrice, String manufacturer, String category) {
		// This is super inefficient, but whatever
		Set<Product> byPrice = new HashSet<Product>(getProductsByPriceFilter(
				lowPrice, highPrice));
		Set<Product> byManufacturer = new HashSet<Product>(
				getProductsByManufacturer(manufacturer));
		Set<Product> byCategory = new HashSet<Product>(
				getProductsByCategory(category));

		byPrice.retainAll(byManufacturer);
		byPrice.retainAll(byCategory);

		return byPrice;
	}

	public void addProduct(Product product) {
		listOfProducts.add(product);
	}

//	public List<Product> getProductsByPrice(BigDecimal low, BigDecimal high) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	public List<Product> getProductsByPrice(float low, float high) {
//		
//		return listOfProducts.;
//		
//	
//	}

	}

