package com.ask.core.config

import com.querydsl.codegen.ClassPathUtils
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JpaConfig : InitializingBean {
  @Bean
  fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

  override fun afterPropertiesSet() {
    ClassPathUtils.scanPackage(Thread.currentThread().contextClassLoader, "com.ask.core")
  }
}
