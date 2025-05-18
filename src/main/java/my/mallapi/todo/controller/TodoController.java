package my.mallapi.todo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import my.mallapi.commons.dto.PageRequestDTO;
import my.mallapi.commons.dto.PageResponseDTO;
import my.mallapi.todo.dto.TodoDTO;
import my.mallapi.todo.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    // 투두 단건 조회
    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable(name="tno") Long tno) {
        return todoService.get(tno);
    }

    // 투두 전체 조회
    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);

        return todoService.list(pageRequestDTO);
    }

    // 투두 등록
    @PostMapping("/")
    public Map<String, Long> register(@RequestBody TodoDTO todoDTO) {
        log.info("TodoDTO: {}", todoDTO);

        Long tno = todoService.register(todoDTO);

        return Map.of("TNO", tno);
    }

    // 투두 수정
    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable(name="tno") Long tno, @RequestBody TodoDTO todoDTO) {
        todoDTO.setTno(tno);

        log.info("Modify: " + todoDTO);

        todoService.modify(todoDTO);
        return Map.of("RESULT", "SUCCESS");
    }

    // 투두 삭제
    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable(name="tno") Long tno) {
        log.info("Remove: " + tno);

        todoService.remove(tno);
        return Map.of("RESULT", "SUCCESS");
    }
}
