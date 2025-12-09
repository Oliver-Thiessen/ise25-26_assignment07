package de.seuhd.campuscoffee.api.dtos;

import lombok.Builder;
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO record for POS metadata.
 */
@Builder(toBuilder = true)
public record ReviewDto (
        // TODO: x Implement ReviewDto
    @Nullable Long id,
    @Nullable LocalDateTime createdAt,
    @Nullable LocalDateTime updatedAt,

    @NotNull
    @NonNull Long posId,

    @NotNull
    @NonNull Long authorId,

    @NotBlank(message = "Review cannot be empty.")
    @NonNull String review,

    // ich bin überfragt
    Boolean approved


) implements Dto<Long> {
    @Override
    public @Nullable Long getId() {
        return id;
    }
}
