package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.model.HitDto;
import ru.practicum.stats.entity.Hit;

@UtilityClass
public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .timestamp(hitDto.getRequestDateTime())
                .build();
    }
}
