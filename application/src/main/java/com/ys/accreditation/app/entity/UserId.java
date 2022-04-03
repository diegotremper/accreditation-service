package com.ys.accreditation.app.entity;

import lombok.Builder;
import lombok.Value;

/** Represents an user identifier. */
@Builder
@Value
public class UserId {
  private String id;
}
