package com.ask.core.config

import com.querydsl.codegen.ClassPathUtils
import com.querydsl.jpa.JPQLTemplates
import com.querydsl.jpa.impl.JPAQueryFactory
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy

@Configuration
@EnableJpaAuditing
class JpaConfig : InitializingBean {
  @Bean
  fun jpaQueryFactory(entityManager: EntityManager) = JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager)

  /**
   * @see org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration.Hikari
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  fun dataSource(dataSourceProperties: DataSourceProperties): HikariDataSource {
    return dataSourceProperties.initializeDataSourceBuilder()
      .type(HikariDataSource::class.java)
      .build()
  }

  @Bean
  @Primary
  fun lazyConnectionDataSourceProxy(hikariDataSource: HikariDataSource): LazyConnectionDataSourceProxy {
    return LazyConnectionDataSourceProxy(hikariDataSource)
  }

  override fun afterPropertiesSet() {
    ClassPathUtils.scanPackage(Thread.currentThread().contextClassLoader, "com.ask.core")
  }
}
