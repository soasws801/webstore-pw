package com.packt.webstore.controller;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.packt.webstore.domain.Product;
import com.packt.webstore.exception.NoProductsFoundUnderCategoryException;
import com.packt.webstore.exception.ProductNotFoundException;
import com.packt.webstore.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@RequestMapping
	public String list(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}

	@RequestMapping("/all")
	public String allProducts(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "products";
	}

	@RequestMapping("/{category}")
	public String getProductsByCategory(Model model, @PathVariable("category") String category) {
		List<Product> products = productService.getProductsByCategory(category);
		if (products == null || products.isEmpty()) {
			throw new NoProductsFoundUnderCategoryException();
		}
		model.addAttribute("products", products);
		return "products";
	}

	@RequestMapping("/filter/{ByCriteria}")
	public String getProductsByFilter(@MatrixVariable(pathVar = "ByCriteria") Map<String, List<String>> filterParams,
			Model model) {
		model.addAttribute("products", productService.getProductsByFilter(filterParams));
		return "products";
	}

	@RequestMapping("/product")
	public String getProductById(@RequestParam("id") String productId, Model model) {
		model.addAttribute("product", productService.getProductById(productId));
		return "product";
	}

	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setAllowedFields("productId", "name", "unitPrice", "description", "manufacturer", "category",
				"unitsInStock", "condition", "productImage", "language");
		binder.setDisallowedFields("unitsInOrder", "discontinued");
	}

	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String getAddNewProductForm(Model model) {
		Product newProduct = new Product();
		model.addAttribute("newProduct", newProduct);
		return "addProduct";
	}

	@RequestMapping("/invalidPromoCode")
	public String invalidPromoCode() {
		return "invalidPromoCode";
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ModelAndView handleError(HttpServletRequest req, ProductNotFoundException exception) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("invalidProductId", exception.getProductId());
		mav.addObject("exception", exception);
		mav.addObject("url", req.getRequestURL() + "?" + req.getQueryString());
		mav.setViewName("productNotFound");
		return mav;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String processAddNewProductForm(@ModelAttribute("newProduct") Product newProduct, BindingResult result,
			HttpServletRequest request) {
		String[] suppressedFields = result.getSuppressedFields();
		if (suppressedFields.length > 0) {
			throw new RuntimeException("Attempting to bind disallowed fields: "
					+ StringUtils.arrayToCommaDelimitedString(suppressedFields));
		}

		MultipartFile productImage = newProduct.getProductImage();

		String rootDirectory = request.getSession().getServletContext().getRealPath("/");

		if (productImage != null && !productImage.isEmpty()) {
			try {
				productImage.transferTo(
						new File(rootDirectory + "resources\\images\\" + newProduct.getProductId() + ".png"));
			} catch (Exception e) {
				throw new RuntimeException("Product Image saving failed", e);
			}
		}

		productService.addProduct(newProduct);
		return "redirect:/products";
	}

	@RequestMapping("/{category}/{price}")
	public String filterProducts(@PathVariable("category") String category,
			@MatrixVariable(pathVar = "price") Map<String, List<String>> priceParams,
			@RequestParam("manufacturer") String manufacturer, Model model) {
		Set<Product> filteredProducts = new HashSet<Product>();

		List<Product> productsByCategory = productService.getProductsByCategory(category);
		List<Product> productsByManufacturer = productService.getProductsByManufacturer(manufacturer);
		Set<Product> productsByPrice = new HashSet<Product>();

		BigDecimal low = new BigDecimal(priceParams.get("low").get(0));
		BigDecimal high = new BigDecimal(priceParams.get("high").get(0));
		productsByPrice.addAll(productService.getProductsByPrice(low, high));

		for (Product categoryProduct : productsByCategory) {
			for (Product manufacturerProduct : productsByManufacturer) {
				for (Product priceProduct : productsByPrice) {
					if (priceProduct.equals(manufacturerProduct) && manufacturerProduct.equals(categoryProduct)) {
						filteredProducts.add(priceProduct);
					}
				}
			}
		}

		model.addAttribute("products", filteredProducts);

		return "products";
	}

}
