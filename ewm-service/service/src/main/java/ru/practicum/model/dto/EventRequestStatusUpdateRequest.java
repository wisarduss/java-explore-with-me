package ru.practicum.model.dto;

import lombok.Data;
import ru.practicum.model.EventRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;
    private EventRequestStatus status;

}