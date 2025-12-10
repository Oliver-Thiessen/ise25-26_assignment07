package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.configuration.ApprovalConfiguration;
import de.seuhd.campuscoffee.domain.exceptions.ValidationException;
import de.seuhd.campuscoffee.domain.model.objects.Review;
import de.seuhd.campuscoffee.domain.ports.api.ReviewService;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import de.seuhd.campuscoffee.domain.ports.data.PosDataService;
import de.seuhd.campuscoffee.domain.ports.data.ReviewDataService;
import de.seuhd.campuscoffee.domain.ports.data.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the Review service that handles business logic related to review entities.
 */
@Slf4j
@Service
public class ReviewServiceImpl extends CrudServiceImpl<Review, Long> implements ReviewService {
    private final ReviewDataService reviewDataService;
    private final UserDataService userDataService;
    private final PosDataService posDataService;
    // TODO: x Try to find out the purpose of this class and how it is connected to the application.yaml configuration file.
    // Wahrscheinlich wird diese Klasse durch campus-coffee:approval:min-count:3 angesprochen und einen Wert gesetzt.
    private final ApprovalConfiguration approvalConfiguration;

    public ReviewServiceImpl(@NonNull ReviewDataService reviewDataService,
                             @NonNull UserDataService userDataService,
                             @NonNull PosDataService posDataService,
                             @NonNull ApprovalConfiguration approvalConfiguration) {
        super(Review.class);
        this.reviewDataService = reviewDataService;
        this.userDataService = userDataService;
        this.posDataService = posDataService;
        this.approvalConfiguration = approvalConfiguration;
    }

    @Override
    protected CrudDataService<Review, Long> dataService() {
        return reviewDataService;
    }

    @Override
    @Transactional
    public @NonNull Review upsert(@NonNull Review review) {
        // TODO: x Implement the missing business logic here

        log.info("Processing upsert request for review with ID '{}'...",
                review.getId());

        // Validate that the POS exists
        try {
            posDataService.getById(review.posId());
        } catch (Exception e) {
            throw new ValidationException("POS with ID '%d' does not exist".formatted(review.posId()));
        }

        // Validate that the User exists
        try {
            userDataService.getById(review.authorId());
        } catch (Exception e) {
            throw new ValidationException("User with ID '%d' does not exist".formatted(review.authorId()));
        }

        // For new reviews (id is null), set default values
        if (review.getId() == null) {
            review = review.toBuilder()
                    .approvalCount(0)
                    .approved(false)
                    .build();
            log.debug("Setting default values for new review: approvalCount=0, approved=false");
        } else {
            // For updates, validate the review exists
            try {
                reviewDataService.getById(review.getId());
            } catch (Exception e) {
                throw new ValidationException("Review with ID '%d' does not exist".formatted(review.getId()));
            }
        }

        // Validate review content
        if (review.review() == null || review.review().trim().isEmpty()) {
            throw new ValidationException("Review content cannot be empty");
        }

        // Validate approval count is not negative
        if (review.approvalCount() < 0) {
            throw new ValidationException("Approval count cannot be negative");
        }

        // Call the parent upsert method which handles the actual persistence
        return super.upsert(review);
    }

    @Override
    @Transactional(readOnly = true)
    public @NonNull List<Review> filter(@NonNull Long posId, @NonNull Boolean approved) {
        return reviewDataService.filter(posDataService.getById(posId), approved);
    }

    @Override
    @Transactional
    public @NonNull Review approve(@NonNull Review review, @NonNull Long userId) {
        log.info("Processing approval request for review with ID '{}' by user with ID '{}'...",
                review.getId(), userId);

        // validate that the user exists
        // TODO: x Implement the required business logic here
        int approvalCount = review.approvalCount();

        if (userDataService.getById(userId) == null) {
            throw new ValidationException("User with ID '" + userId + "' not found");
        }

        // validate that the review exists
        // TODO: x Implement the required business logic here
        if (review.getId() != null && reviewDataService.getById(review.getId()) == null) {
            throw new ValidationException("Review with ID '" + review.getId() + "' not found");
        }

        // a user cannot approve their own review
        // TODO: x Implement the required business logic here
        if (review.authorId().equals(userId)) {
            throw new ValidationException("A user cannot approve their own review");
        }

        // increment approval count
        // TODO: x Implement the required business logic here
        review = review.toBuilder()
                .approvalCount(review.approvalCount() + 1)
                .build();

        // update approval status to determine if the review now reaches the approval quorum
        // TODO: x Implement the required business logic here
        review = updateApprovalStatus(review);

        return reviewDataService.upsert(review);
    }

    /**
     * Calculates and updates the approval status of a review based on the approval count.
     * Business rule: A review is approved when it reaches the configured minimum approval count threshold.
     *
     * @param review The review to calculate approval status for
     * @return The review with updated approval status
     */
    Review updateApprovalStatus(Review review) {
        log.debug("Updating approval status of review with ID '{}'...", review.getId());
        return review.toBuilder()
                .approved(isApproved(review))
                .build();
    }

    /**
     * Determines if a review meets the minimum approval threshold.
     *
     * @param review The review to check
     * @return true if the review meets or exceeds the minimum approval count, false otherwise
     */
    private boolean isApproved(Review review) {
        return review.approvalCount() >= approvalConfiguration.minCount();
    }
}