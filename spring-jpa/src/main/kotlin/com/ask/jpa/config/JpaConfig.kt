package com.ask.jpa.config

import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.hibernate.cfg.AvailableSettings
import org.hibernate.jpa.boot.spi.IntegratorProvider
import org.hibernate.jpa.boot.spi.JpaSettings
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JpaConfig {
  @Bean
  fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

  @Bean
  fun hibernatePropertiesCustomizer(inspector: EncryptionStatementInspector, integrator: EncryptionIntegrator) =
    HibernatePropertiesCustomizer {
      it[AvailableSettings.STATEMENT_INSPECTOR] = inspector
      it[JpaSettings.INTEGRATOR_PROVIDER] = IntegratorProvider { listOf(integrator) }
    }
}

