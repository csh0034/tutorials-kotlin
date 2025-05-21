# coroutine

## Dispatchers.IO

- 블로킹 IO 작업을 위한 코루틴 디스패처
- 공유된 스레드 풀을 사용
- 시스템 속성 `kotlinx.coroutines.io.parallelism` 로 제한 가능
  - 기본값: max(64, CPU 코어 수)
