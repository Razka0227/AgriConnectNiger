package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.Product;
import ne.agriconnect.dto.ProductDto;
import ne.agriconnect.repository.ProductRepository;
import ne.agriconnect.service.DtoMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final DtoMapper mapper;

    @GetMapping
    public List<ProductDto> list() {
        return productRepository.findAll().stream().map(mapper::productToDto).toList();
    }
}
