package de.seuhd.campuscoffee.api.mapper;

import de.seuhd.campuscoffee.api.dtos.ReviewDto;
import de.seuhd.campuscoffee.domain.model.objects.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import de.seuhd.campuscoffee.domain.ports.api.PosService;
import de.seuhd.campuscoffee.domain.ports.api.UserService;

/**
 * MapStruct mapper for converting between the {@link Review} domain model objects and {@link ReviewDto}s.
 */
@Mapper(componentModel = "spring")
@ConditionalOnMissingBean // prevent IntelliJ warning about duplicate beans
public abstract class ReviewDtoMapper implements DtoMapper<Review, ReviewDto> {
    //TODO: x auskommentieren nachdem ReviewDto und Review implementiert sind
    @Autowired
    @SuppressWarnings("unused") // used in @Mapping expressions
    protected PosService posService;
    
    @Autowired
    @SuppressWarnings("unused") // used in @Mapping expressions
    protected UserService userService;

    /**
     * Maps from Review domain object to ReviewDto.
     * Since Review only contains posId and authorId (not full objects), 
     * we map the IDs directly without needing to fetch the full objects.
     */
    @Mapping(target = "posId", source = "posId")
    @Mapping(target = "authorId", source = "authorId")
    public abstract ReviewDto fromDomain(Review source);

    /**
     * Maps from ReviewDto to Review domain object.
     * When creating a new review, we set default values for approval-related fields.
     */
    @Mapping(target = "approved", constant = "false")
    @Mapping(target = "approvalCount", constant = "0")
    public abstract Review toDomain(ReviewDto source);
}
