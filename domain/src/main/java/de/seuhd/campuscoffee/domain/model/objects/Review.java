package de.seuhd.campuscoffee.domain.model.objects;

import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.time.LocalDateTime;

/**
 * Domain record that stores a review for a point of sale.
 * Reviews are approved once they received a configurable number of approvals.
 */
@Builder(toBuilder = true)
public record Review(
        //TODO: x Implement review domain model.
        @Nullable Long id, // null when the review has not been created yet
        @Nullable LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt,
        @NonNull Long posId,
        @NonNull Long authorId,
        @NonNull String review,
        @NonNull Integer approvalCount, // is updated by the domain module
        @NonNull Boolean approved // is determined by the domain module
) implements DomainModel<Long> {
    @Override
    public Long getId() {
        return id;
    }
}
