package ru.practicum.service;

import ru.practicum.model.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto save(UserDto user);

    void delete(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
