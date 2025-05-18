package my.mallapi.product.service;

import my.mallapi.commons.dto.PageRequestDTO;
import my.mallapi.commons.dto.PageResponseDTO;
import my.mallapi.product.dto.ProductDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductService {

    PageResponseDTO<ProductDTO> getList(PageRequestDTO pageRequestDTO);
    Long register(ProductDTO productDTO);
    ProductDTO get(Long pno);
    void modify(ProductDTO productDTO);
    void remove(Long pno);
}
