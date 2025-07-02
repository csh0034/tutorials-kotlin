package com.ask.jndi.config

import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.descriptor.web.ContextResource
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JndiConfig {
  @Bean
  fun tomcatServletWebServerFactory(
    connectorCustomizers: ObjectProvider<TomcatConnectorCustomizer?>,
    contextCustomizers: ObjectProvider<TomcatContextCustomizer?>,
    protocolHandlerCustomizers: ObjectProvider<TomcatProtocolHandlerCustomizer<*>?>,
  ) = object : TomcatServletWebServerFactory() {
    override fun getTomcatWebServer(tomcat: Tomcat): TomcatWebServer {
      tomcat.enableNaming()
      return super.getTomcatWebServer(tomcat)
    }
  }.apply {
    tomcatConnectorCustomizers.addAll(connectorCustomizers.orderedStream().toList())
    tomcatContextCustomizers.addAll(contextCustomizers.orderedStream().toList())
    tomcatProtocolHandlerCustomizers.addAll(protocolHandlerCustomizers.orderedStream().toList())
  }

  @Bean
  fun tomcatContextCustomizer() = TomcatContextCustomizer {
    val resource = ContextResource()
    resource.name = "jdbc/mydb"
    resource.type = "javax.sql.DataSource"
    resource.setProperty("factory", "org.apache.commons.dbcp2.BasicDataSourceFactory")
    resource.setProperty("driverClassName", "org.mariadb.jdbc.Driver")
    resource.setProperty("url", "jdbc:mariadb://localhost:3306/mydb?createDatabaseIfNotExist=true")
    resource.setProperty("username", "root")
    resource.setProperty("password", "111111")
    resource.setProperty("maxTotal", "20")
    resource.setProperty("maxIdle", "20")
    resource.setProperty("maxWaitMillis", "10000")
    it.namingResources.addResource(resource)
  }
}
