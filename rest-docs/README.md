# rest-docs

## restdocs kotlin dsl

- https://toss.tech/article/kotlin-dsl-restdocs

## Troubleshooting

### snippets 안만들어지는 이슈

- kotlin dsl 로 mockmvc 사용시 handle 로 감싸거나 확장함수 추가해야함.
- https://github.com/spring-projects/spring-restdocs/issues/677

```kotlin
@Test
fun test() {
    mockMvc.get("/test") {
        queryParam("name", "ask")
    }.andExpectAll {
        status { isOk() }
        jsonPath("$.name") { value("ask") }
    }.andDo {
        print()
        document("test-sample")
    }
}

fun MockMvcResultHandlersDsl.document(identifier: String, vararg snippets: Snippet) {
    handle(MockMvcRestDocumentation.document(identifier, *snippets))
}
```
