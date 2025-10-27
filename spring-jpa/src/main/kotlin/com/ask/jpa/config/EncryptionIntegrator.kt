package com.ask.jpa.config

import org.hibernate.boot.Metadata
import org.hibernate.boot.spi.BootstrapContext
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.integrator.spi.Integrator
import org.hibernate.service.spi.SessionFactoryServiceRegistry
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EncryptionIntegrator : Integrator {
  @Value("\${custom.db-secret-key}")
  private lateinit var secretKey: String

  override fun integrate(
    metadata: Metadata,
    bootstrapContext: BootstrapContext,
    sessionFactory: SessionFactoryImplementor,
  ) {
    metadata.entityBindings.forEach { entity ->
      entity.referenceableProperties.forEach { property ->
        property.columns.forEach { column ->
          val customRead = column.customRead
          val writeExpr = column.customWrite

          if (customRead != null && customRead.contains(SECRET_KEY_PLACEHOLDER)) {
            column.customRead = customRead.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'")
          }

          if (writeExpr != null && customRead.contains(SECRET_KEY_PLACEHOLDER)) {
            column.customWrite = writeExpr.replace(SECRET_KEY_PLACEHOLDER, "'${secretKey}'")
          }
        }
      }
    }
  }

  override fun disintegrate(
    sessionFactory: SessionFactoryImplementor,
    serviceRegistry: SessionFactoryServiceRegistry
  ) {
    // no-op
  }
}
