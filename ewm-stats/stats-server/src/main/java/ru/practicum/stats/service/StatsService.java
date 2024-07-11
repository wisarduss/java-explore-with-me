package ru.practicum.stats.service;

import ru.practicum.model.HitDto;
import ru.practicum.model.IStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(HitDto hitDto);

    List<IStatsDto> findHitsByParams(LocalDateTime start, LocalDateTime end, List<String> uris,
                                     Boolean unique);
}
