package com.ask.openfeignquerydsl.user.dto

import com.ask.openfeignquerydsl.user.model.Name
import com.querydsl.core.annotations.QueryProjection

@QueryProjection
data class UserDto(
  val id: String,
  val dto: Name,
)

@QueryProjection
data class NestedUserDto(
  val id: String,
  val dto: UserDto,
)
