package com.ask.core.fixture

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.api.plugin.SimpleValueJqwikPlugin
import com.navercorp.fixturemonkey.datafaker.plugin.DataFakerPlugin
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import net.jqwik.api.Arbitraries
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * @see <a href="https://naver.github.io/fixture-monkey/v1-1-0-kor/">Fixture Monkey</a>
 */
class FixtureMonkeyTest {
  val objectMapper = jsonMapper {
    addModule(kotlinModule())
    addModule(JavaTimeModule())
  }

  val fixtureMonkey: FixtureMonkey = FixtureMonkey.builder()
    .defaultNotNull(true)
    .plugin(DataFakerPlugin())
    .plugin(SimpleValueJqwikPlugin())
    .plugin(KotlinPlugin())
//    .plugin(JacksonPlugin(objectMapper))
//    .plugin(JakartaValidationPlugin())
    .build()

  @Test
  fun test() {
    val actual = fixtureMonkey.giveMeKotlinBuilder<Product>()
      .set(Product::id, 1000L)
      .size(Product::options, 3)
      .sample()

    assertThat(actual.id).isEqualTo(1000L)
    assertThat(actual.options).hasSize(3)
  }

  @Test
  fun test2() {
    val actual = fixtureMonkey.giveMeKotlinBuilder<Product>()
      .set(Product::id, Arbitraries.longs().greaterOrEqual(1000))
      .set(Product::email, Arbitraries.strings().withCharRange('a', 'z').ofLength(10))
      .set(Product::productType, Arbitraries.of(ProductType.CLOTHING, ProductType.ELECTRONICS))
      .sample()

    assertThat(actual.id).isGreaterThanOrEqualTo(1000)
    assertThat(actual.email).matches("^[a-z]+$")
    assertThat(actual.email.length).isLessThanOrEqualTo(10)
    assertThat(actual.productType).matches { it === ProductType.CLOTHING || it === ProductType.ELECTRONICS }
  }
}

private data class Product(
  val id: Long,
  val email: String,
  val price: Long,
  val options: List<String>,
  val createdAt: Instant,
  val productType: ProductType,
  val merchantInfo: Map<Integer, String>,
)

private enum class ProductType {
  CLOTHING, ELECTRONICS,
}
