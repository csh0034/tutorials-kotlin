# Spring AI

## 소개

- https://docs.spring.io/spring-ai/reference/index.html
- 목표는 불필요한 복잡성 없이 인공지능 기능을 통합한 애플리케이션 개발을 간소화하는 것
- LangChain과 LlamaIndex와 같은 유명한 Python 프로젝트에서 영감을 얻었지만 포팅한것은 아님
- 차세대 생성 AI 애플리케이션이 Python 개발자만을 위한 것이 아니라 다양한 프로그래밍 언어에서  
  널리 사용될 것이라는 믿음을 바탕으로 시작

> Connecting your enterprise Data and APIs with AI Models

## Chat Memory

- 대규모 언어 모델(LLM)은 상태 비저장(stateless) 방식으로, 이전 상호작용에 대한 정보를 유지하지 않는다.
- 이를 해결하기 위해 LLM과의 여러 상호작용에서 정보를 저장하고 검색할 수 있는 채팅 메모리 기능을 제공

### 해당 기능은 채팅 메모리를 관리하도록 설계됨

- Chat Memory: 대규모 언어 모델이 대화 전반에 걸쳐 **맥락적** 인식을 유지하기 위해 보관하고 사용하는 정보
- Chat History: 사용자와 모델 간에 교환된 모든 메시지를 포함한 전체 대화 기록입니다.

## Code & Examples

- https://github.com/spring-ai-community/awesome-spring-ai?tab=readme-ov-file#code--examples
- https://github.com/ThomasVitale/llm-apps-java-spring-ai/blob/main/observability/models/observability-models-openai/README.md
