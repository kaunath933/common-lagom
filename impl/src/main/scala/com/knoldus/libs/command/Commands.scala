package com.knoldus.libs.command

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

trait Commands[R] extends ReplyType[R]