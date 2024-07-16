package ru.practicum.model.dto;

import lombok.Data;

import javax.validation.constraints.Positive;

@Data
public class LocationDto {

    @Positive
    private Double lat;

    @Positive
    private Double lon;

}
