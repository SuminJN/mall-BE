package my.mallapi.service;

import my.mallapi.dto.PageRequestDTO;
import my.mallapi.dto.PageResponseDTO;
import my.mallapi.dto.TodoDTO;

public interface TodoService {

    Long register(TodoDTO todoDTO);

    TodoDTO get(Long tno);

    void modify(TodoDTO todoDTO);

    void remove(Long tno);

    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);
}
