package ru.practicum.stats.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.model.HitDto;
import ru.practicum.model.IStatsDto;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.repository.HitRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    public void saveHit(HitDto hitDto) {
        hitRepository.save(HitMapper.toHit(hitDto));
    }

    @Override
    public List<IStatsDto> findHitsByParams(LocalDateTime start, LocalDateTime end, List<String> uris,
                                            Boolean unique
    ) {
        if (start.isAfter(end)) {
            throw new ValidationException();
        }

        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                return hitRepository.findDistinctHitsByUris(start, end, uris);
            }
            return hitRepository.findHitsByUris(start, end, uris);
        }
        if (unique) {
            return hitRepository.findDistinctHits(start, end);
        }
        return hitRepository.findHits(start, end);
    }

}
