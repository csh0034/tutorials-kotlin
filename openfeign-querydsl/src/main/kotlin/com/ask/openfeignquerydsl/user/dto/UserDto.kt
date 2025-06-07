package com.ask.openfeignquerydsl.user.dto

import com.ask.openfeignquerydsl.user.model.Name
import com.querydsl.core.annotations.QueryProjection

data class UserDto @QueryProjection constructor(
  val id: String,
  val name: Name,
)
