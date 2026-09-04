package ne.agriconnect.controller;

import ne.agriconnect.domain.ProductCategory;
import ne.agriconnect.domain.Region;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegionController {

    @GetMapping("/regions")
    public List<Map<String, String>> regions() {
        return Arrays.stream(Region.values())
                .map(r -> Map.of("code", r.name(), "label", r.getLabel()))
                .toList();
    }

    @GetMapping("/categories")
    public List<Map<String, String>> categories() {
        return Arrays.stream(ProductCategory.values())
                .map(c -> Map.of("code", c.name(), "label", c.getLabel()))
                .toList();
    }
}
