package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.UserDto;
import ru.practicum.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody final UserDto userDto) {
        return userService.save(userDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") Long userId) {
        userService.delete(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) final List<Long> ids,
            @RequestParam(defaultValue = "0") @Min(0) final Integer from,
            @RequestParam(defaultValue = "10") @Min(1) final Integer size
    ) {
        return userService.getUsers(ids, from, size);

    }
}
