package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.User;
import ru.practicum.exception.IdNotFoundException;
import ru.practicum.model.dto.UserDto;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.utils.CustomPageRequest.pageRequestOf;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto save(UserDto user) {
        log.debug("пользователь сохранен");
        return modelMapper.map(userRepository.save(modelMapper.map(user, User.class)), UserDto.class);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));
        log.debug("Пользователь удален");
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageRequestOf(from, size)).stream()
                    .map(u -> modelMapper.map(u, UserDto.class))
                    .collect(Collectors.toList());
        }
        log.debug("получены все пользователи");
        return userRepository.findAllByIdIn(ids, pageRequestOf(from, size)).stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

}
