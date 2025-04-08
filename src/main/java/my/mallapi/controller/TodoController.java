package my.mallapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import my.mallapi.dto.PageRequestDTO;
import my.mallapi.dto.PageResponseDTO;
import my.mallapi.dto.TodoDTO;
import my.mallapi.service.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/todo")
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/{tno}")
    public TodoDTO get(@PathVariable(name="tno") Long tno) {
        return todoService.get(tno);
    }

    @GetMapping("/list")
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO) {
        log.info(pageRequestDTO);

        return todoService.list(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String, Long> register(@RequestBody TodoDTO todoDTO) {
        log.info("TodoDTO: {}", todoDTO);

        Long tno = todoService.register(todoDTO);

        return Map.of("TNO", tno);
    }

    @PutMapping("/{tno}")
    public Map<String, String> modify(@PathVariable(name="tno") Long tno, @RequestBody TodoDTO todoDTO) {
        todoDTO.setTno(tno);

        log.info("Modify: " + todoDTO);

        todoService.modify(todoDTO);
        return Map.of("RESULT", "SUCCESS");
    }

    @DeleteMapping("/{tno}")
    public Map<String, String> remove(@PathVariable(name="tno") Long tno) {
        log.info("Remove: " + tno);

        todoService.remove(tno);
        return Map.of("RESULT", "SUCCESS");
    }
}
