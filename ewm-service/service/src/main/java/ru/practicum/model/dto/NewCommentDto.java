package ru.practicum.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewCommentDto {

    @NotNull
    @NotBlank
    @Length(min = 5)
    private String text;

}
