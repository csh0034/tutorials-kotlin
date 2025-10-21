# ArchUnit

## Troubleshooting

### and, or 사용시 이슈

- and, or 를 dsl 형식으로 쓸 경우 논리적 그룹화가 올바르게 동작하지 않을수 있어 주의 해야한다

```text
a or b and c and d 인 경우
> ((a or b) and c) and d 로 동작함

> 판단 순서
1. OrPredicate a or b
2. AndPredicate 1의 결과 and c
3. AndPredicate 2의 결과 and d

a or b and c or d 인 경우
> ((a or b) and c) or d 로 동작함

> 판단 순서
1. OrPredicate a or b
2. AndPredicate 1의 결과 and c
3. AndPredicate 2의 결과 or d

만약 (a or b) and (c or d) 가 필요하면 하단과 같이 해야함
> .and(annotatedWith(ManyToOne::class.java).or(annotatedWith(OneToOne::class.java)))
```

## 참조

- https://www.archunit.org/
- https://www.archunit.org/userguide/html/000_Index.html
- https://d2.naver.com/helloworld/9222129
