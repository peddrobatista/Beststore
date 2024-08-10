package com.peddrobatista.beststore.controllers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.peddrobatista.beststore.models.Product;
import com.peddrobatista.beststore.models.ProductDto;
import com.peddrobatista.beststore.services.ProductsRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/products") // define a rota base para os métodos dentro dessa classe.
public class ProductController {

	@Autowired // usada para injetar automaticamente a dependência do repositório de produtos (ProductsRepository).
	private ProductsRepository repo;
	
	@GetMapping({"","/"}) // Define que este método deve responder às requisições GET na raiz do mapeamento "/products".
	public String showProductList(Model model) {
		// recupera todos os produtos do repositório.
		List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC, "id")); // ordem decrescente
		model.addAttribute("products", products); // adiciona a lista de produtos ao modelo, que será utilizado pela view.
		return "products/index"; // retorna a view chamada "index" dentro do diretório "products".
	}
	
	/* Método que o exibe o formulário que permite ao usuário 
	 * criar novos produtos 
	 */
	@GetMapping("/create")
	public String showCreatePage(Model model) {
		ProductDto productDto = new ProductDto();
		// Adicionamos esse objeto ao modelo para que possamos ler os detalhes do produto
		model.addAttribute("productDto", productDto);
		return "products/CreateProduct";
	}
	
	// Método que cria e processa o produto
	@PostMapping("/create")
	public String createProduct(@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
		if (productDto.getImageFile().isEmpty()) {
			result.addError(new FieldError("productDto", "imageFile", "O arquivo de imagem é obrigatório"));
		}
		
		if (result.hasErrors()) {
            return "products/CreateProduct";
        }
		
		// Salvando o arquivo de imagem
		MultipartFile image = productDto.getImageFile();
		Date createdAt = new Date();
		String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
		
		try {
			String uploadDir = "public/images/";
			Path uploadPath = Paths.get(uploadDir);
			
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}
			
			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
						StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ex) {
			System.out.println("Exeption: " + ex.getMessage());
		}
		
		// Salvando os detalhes do produto
		Product product = new Product();
		product.setName(productDto.getName());
		product.setBrand(productDto.getBrand());
		product.setCategory(productDto.getCategory());
		product.setPrice(productDto.getPrice());
		product.setDescription(productDto.getDescription());
		product.setImageFileName(storageFileName);
		product.setCreatedAt(createdAt);
		
		// Salva o produto no banco de dados
		repo.save(product);
		
		return "redirect:/products";
	}
	
	// Método para editar o produto
	@GetMapping("/edit")
	public String showEditPage(Model model, @RequestParam int id ) {
		try {
			// Encontra o produto existente no banco de dados
			Product product = repo.findById(id).get();
			// Adicionamos esse objeto ao modelo para que possamos ler os detalhes do produto
			model.addAttribute("product", product);
			
			ProductDto productDto = new ProductDto();
			productDto.setName(product.getName());
			productDto.setBrand(product.getBrand());
			productDto.setCategory(product.getCategory());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			
			model.addAttribute("productDto", productDto);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			return "redirect:/products";
		}
		return "products/EditProduct";
	}
	
	// Método para atualizar o produto
	@PostMapping("/edit")
	public String updateProduct(Model model, @RequestParam int id, 
			@Valid @ModelAttribute ProductDto productDto, BindingResult result) {
		try {
			// Encontra o produto existente no banco de dados
			Product product = repo.findById(id).get();
			// Adicionamos esse objeto ao modelo para que possamos ler os detalhes do produto
			model.addAttribute("product", product);
			
			if (result.hasErrors()) {
				return "products/EditProduct";
			}
			
			if (!productDto.getImageFile().isEmpty()) {
				// Deletando imagem antiga 
				String uploadDir = "public/images/";
				Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
				
				try {
					Files.delete(oldImagePath);
				} catch (Exception e) {
					System.out.println("Exception: " + e.getMessage());
				}
				
				// Salvando nova imagem
				MultipartFile image = productDto.getImageFile();
				Date createdAt = new Date();
				String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
				
				try (InputStream inputStream = image.getInputStream()) {
					Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
				} 
				
				product.setImageFileName(storageFileName);
 			}
			
			// Atualiza os detalhes do produto com os dados do DTO
			product.setName(productDto.getName());
			product.setBrand(productDto.getBrand());
			product.setCategory(productDto.getCategory());
			product.setPrice(productDto.getPrice());
			product.setDescription(productDto.getDescription());
			
			// Salva product atualizado no banco de dados
			repo.save(product);
			
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return "redirect:/products";
	}
	
	// Método para excluir o produto
	@GetMapping("/delete")
	public String deleteProduct(@RequestParam int id) {
		
		try {
			// Encontra o produto existente no banco de dados
			Product product = repo.findById(id).get();
			
			// Deletando a imagem do produto
			Path imagePath = Paths.get("public/images" + product.getImageFileName());
			try {
				Files.delete(imagePath);
			} catch (Exception e) {
				System.out.println("Exception: " + e.getMessage());
			}
			
			// Deletando o produto
			repo.delete(product);
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
		return "redirect:/products";
	}
}
