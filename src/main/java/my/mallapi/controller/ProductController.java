package my.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import my.mallapi.dto.PageRequestDTO;
import my.mallapi.dto.PageResponseDTO;
import my.mallapi.dto.ProductDTO;
import my.mallapi.service.ProductService;
import my.mallapi.util.CustomFileUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CustomFileUtil fileUtil;

    // 상품 등록
    @PostMapping("/")
    public Map<String, Long> register(ProductDTO productDTO) {

        log.info("register: {}", productDTO);

        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadFileNames(uploadFileNames);

        log.info(uploadFileNames);

        // 서비스 호출
        Long pno = productService.register(productDTO);

        return Map.of("result", pno);
    }

    // 상품 이미지 조회
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
        return fileUtil.getFile(fileName);
    }

    // 상품 전체 조회
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')") // 특정 권한 사용자에게 권한 부여
    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO) {

        log.info("list: {}", pageRequestDTO);
        return productService.getList(pageRequestDTO);
    }

    // 상품 단건 조회
    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable(name = "pno") Long pno) {
        return productService.get(pno);
    }

    // 상품 수정
    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable(name = "pno") Long pno, ProductDTO productDTO) {

        productDTO.setPno(pno);

        ProductDTO oldProductDTO = productService.get(pno);

        // 기존의 파일들
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();

        // 새로 업로드 해야 하는 파일들
        List<MultipartFile> files = productDTO.getFiles();

        // 새로 업로드되어서 만들어진 파일 이름들
        List<String> currentUploadFilesNames = fileUtil.saveFiles(files);

        // 화면에서 변화 없이 계속 유지된 파일들
        List<String> uploadedFileNames = productDTO.getUploadFileNames();

        // 유지되는 파일들 + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
        if (currentUploadFilesNames != null && !currentUploadFilesNames.isEmpty()) {
            uploadedFileNames.addAll(currentUploadFilesNames);
        }

        // 수정 작업
        productService.modify(productDTO);

        if (oldFileNames != null && !oldFileNames.isEmpty()) {

            // 지워야 하는 파일 목록 찾기
            // 예전 파일들 중에서 지워져야 하는 파일이름들
            List<String> removeFiles = oldFileNames
                    .stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName)).toList();

            fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("RESULT", "SUCCESS");
    }

    // 상품 삭제
    @DeleteMapping("/{pno}")
    public Map<String, String> remove(@PathVariable("pno") Long pno) {

        // 삭제해야 할 파일들 알아내기
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames);

        return Map.of("RESULT", "SUCCESS");
    }
}
