package my.mallapi.todo.service;

import my.mallapi.commons.dto.PageRequestDTO;
import my.mallapi.commons.dto.PageResponseDTO;
import my.mallapi.todo.dto.TodoDTO;

public interface TodoService {

    Long register(TodoDTO todoDTO);

    TodoDTO get(Long tno);

    void modify(TodoDTO todoDTO);

    void remove(Long tno);

    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
