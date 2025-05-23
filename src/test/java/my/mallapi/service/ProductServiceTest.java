package my.mallapi.service;

import lombok.extern.log4j.Log4j2;
import my.mallapi.commons.dto.PageRequestDTO;
import my.mallapi.commons.dto.PageResponseDTO;
import my.mallapi.product.dto.ProductDTO;
import my.mallapi.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
class ProductServiceTest {

    @Autowired
    ProductService productService;

    @Test
    public void testList() {
        // 1 page, 10 size
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();

        PageResponseDTO<ProductDTO> result = productService.getList(pageRequestDTO);

        result.getDtoList().forEach(log::info);
    }

    @Test
    public void testRegister() {
        ProductDTO productDTO = ProductDTO.builder()
                .pname("새로운 상품")
                .pdesc("신규 추가 상품입니다.")
                .price(1000)
                .build();

        productDTO.setUploadFileNames(
                List.of(UUID.randomUUID() + "_" + "Test1.jpg", UUID.randomUUID() + "_" + "Test2.jpg")
        );

        productService.register(productDTO);
    }

    @Test
    public void testRead() {

        // 실제 존재하는 번호로 테스트
        Long pno = 12L;

        ProductDTO productDTO = productService.get(pno);

        log.info(productDTO);
        log.info(productDTO.getUploadFileNames());
    }
}