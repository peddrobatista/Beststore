package com.peddrobatista.beststore.models;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/*
 * Modelo usado para criar e atualizar produtos 
 * permite ao usuário enviar os detalhes do produto
 * Ele é chamado de modelo DTO 
 * isto é Modelo Objeto de Transferência de Dados
 * */
public class ProductDto { 
	
	@NotEmpty(message = "O nome é obrigatório")
	private String name;
	
	@NotEmpty(message = "A marca é obrigatória")
	private String brand;
	
	@NotEmpty(message = "A categoria é obrigatória")
	private String category;
	
	@Min(0) // deve no mínimo um número positivo
	private double price;
	
	@Size(min = 10, message = "A descrição deve ter pelo menos 10 caracteres")
	@Size(max = 2000, message = "A descrição não pode exceder 2.000 caracteres")
	private String description;
	
	private MultipartFile imageFile; // arquivo de imagem

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}
	
	
}
