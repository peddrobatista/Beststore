package com.peddrobatista.beststore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.peddrobatista.beststore.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{

}
