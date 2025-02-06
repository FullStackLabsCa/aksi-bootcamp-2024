package org.reactivestax.canada_active_life.mapper;

import org.mapstruct.Mapper;
import org.reactivestax.canada_active_life.domain.Cart;
import org.reactivestax.canada_active_life.dto.CartDTO;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDTO toDto(Cart cart);
    Cart toEntity(CartDTO dto);
}
