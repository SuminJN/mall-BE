package my.mallapi.service;

import lombok.extern.log4j.Log4j2;
import my.mallapi.commons.dto.PageRequestDTO;
import my.mallapi.commons.dto.PageResponseDTO;
import my.mallapi.todo.dto.TodoDTO;
import my.mallapi.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
@Log4j2
class TodoServiceImplTest {

    @Autowired
    private TodoService todoService;

    @Test
    public void testRegister() {

        TodoDTO todoDTO = TodoDTO.builder()
                .title("서비스 테스트")
                .writer("tester")
                .dueDate(LocalDate.of(2025, 10, 10))
                .build();

        Long tno = todoService.register(todoDTO);

        log.info("TNO: {}", tno);
    }

    @Test
    public void testGet() {
        Long tno = 101L;

        TodoDTO todoDTO = todoService.get(tno);

        log.info(todoDTO);
    }

    @Test
    public void testList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(2)
                .size(10)
                .build();

        PageResponseDTO<TodoDTO> response = todoService.list(pageRequestDTO);

        log.info(response);
    }
}