# Spring AI

## 소개

- https://docs.spring.io/spring-ai/reference/index.html
- 목표는 불필요한 복잡성 없이 인공지능 기능을 통합한 애플리케이션 개발을 간소화하는 것
- LangChain과 LlamaIndex와 같은 유명한 Python 프로젝트에서 영감을 얻었지만 포팅한것은 아님
- 차세대 생성 AI 애플리케이션이 Python 개발자만을 위한 것이 아니라 다양한 프로그래밍 언어에서  
  널리 사용될 것이라는 믿음을 바탕으로 시작

> Connecting your enterprise Data and APIs with AI Models

## RAG (Retrieval Augmented Generation)

- 대규모 언어 모델(LLM)이 가진 한계를 극복하는 데 유용한 기법으로, 긴 형식의 콘텐츠 처리, 사실 정확성  
  그리고 문맥 인식과 같은 문제를 보완한다.
- 대규모 언어 모델의 출력을 최적화하여 응답을 생성하기 전에 외부의 신뢰할 수 있는 기술 자료를 참조하도록 하는 프로세스.
- Embedding Model 을 활용하여 저장된 VectorStore 에서 유사도 검색을 통한 자료를 프롬프트에 보강한다.
- Hallucination(환각) 을 줄이는 방법이다.
  - **Hallucination**: 인공지능 영역에서는 AI가 잘못된 정보를 생성하는 현상 또는 기술적 오류를 의미한다.

### RAG 가 중요한 이유, LLM 의 알려진 문제점

- 답이 없을 때 허위 정보를 제공. (Hallucination) 
- 사용자가 구체적이고 최신의 응답을 기대할 때 오래되었거나 일반적인 정보를 제공.
- 신뢰할 수 없는 출처로부터 응답을 생성.
- 다양한 훈련 소스에서 동일한 용어를 사용하여 다른 내용을 설명하면서 용어 혼동으로 인해 응답이 부정확.

### RAG 의 이점

- 비용 효율적인 구현
  - 모델을 재교육하는것에 비해 비용이 효율적
- 최신 정보 제공
  - 모델에게 최신 연구, 통계등의 정보를 제공 할 수 있음
- 사용자 신뢰 강화
  - 모델이 데이터의 출처 또는 저작를 표시하게 하여 신뢰도 확보 가능
- 개발자 제어 강화
  - 변화하는 요구사항에 맞춰 정보를 제어할수 있다.

### RAG 동작

- 외부 데이터 생성
  - 임베딩 모델을 통한 벡터정보를 벡터스토어에 저장
- 관련 정보 검색
  - 사용자의 입력정보를 벡터로 변환하고 벡터스토어에서 유사도 검색을 통해 가져옴
- LLM 프롬프트 확장
  -  프롬프트 엔지니어링을 통하여 검색된 데이터를 프롬프트에 추가(보강)한다.
- 외부 데이터 업데이트 (ETL pipeline)
  - 최신 정보 검색을 유지하기 위해 문서를 주기적으로 업데이트하고 벡터스토어에 저장 

![01.png](img/01.png)

- 출처
  - https://aws.amazon.com/ko/what-is/retrieval-augmented-generation/

### RetrievalAugmentationAdvisor

- 하단 의존성 필요

```kotlin
implementation("org.springframework.ai:spring-ai-rag")
```

- Spring AI 는 직접 RAG 흐름을 구축할 수 있도록 RAG 모듈 라이브러리를 포함한다.
- RetrievalAugmentationAdvisor 는 모듈형 아키텍처를 기반으로 가장 일반적인 RAG 흐름에 대해    
  즉시 사용할 수 있는 구현을 제공하는 Advisor 이다.

### Module

- "Modular RAG: Transforming RAG Systems into LEGO-like Reconfigurable Frameworks" 논문에서 설명한    
  모듈성 개념을 바탕으로, 모듈형 RAG 아키텍처를 구현한다.

> QueryTransformer를 사용할 때는 ChatClient.Builder를 낮은 temperature 값(예: 0.0)으로 설정하는 것이 권장된다.  
이렇게 하면 결과가 더 **결정적(deterministic)**이고 정확해져서 검색 품질이 향상된다.  
대부분의 Chat 모델은 기본 temperature 값이 상대적으로 높게 설정되어 있는데, 이는 Query Transformation 과정에서  
최적의 검색 성능을 내기에 적합하지 않으며, 검색 효과가 떨어질 수 있다.

#### Pre-Retrieval 

- 유사도 검색 전에 실행하는 모듈
  - CompressionQueryTransformer: 대화 기억과 관련이 있는 모호한 사용자 질문을 LLM 을 이용하여 완전한 질문으로 변환
  - RewriteQueryTransformer: 사용자 질문에 검색 결과의 품질에 영향을 줄 수 있는 불필요한 내용을 LLM 을 사용하여 재작성 
  - TranslationQueryTransformer: 사용자 질문을 LLM 을 이용해서 모델이 지원하는 대상 언어로 번역
  - MultiQueryExpander: 사용자 질문을 LLM 을 이용하여 다양한 변형 질문으로 확장, 이는 유사도 검색시 사용됨

#### Retrieval

- 유사도 검색시 사용하는 모듈
  - VectorStoreDocumentRetriever: 입력 쿼리와 의미적으로 유사한 문서를 벡터 저장소에서 검색

#### Post-Retrieval

- 유사도 검색 후에 실행하는 모듈

#### Generation

- LLM 으로 보내기 직전에 실행하는 모듈
  - ContextualQueryAugmenter: 제공된 문서의 내용을 기반으로 사용자 쿼리에 문맥 데이터를 보강한다.  
    기본적으로 검색된 컨텍스트가 비어 있는 것을 허용하지 않으며 그런 경우, 모델에게 사용자 쿼리에 답변하지 말라고 지시한다. 

## ETL pipeline

- 추출(Extract), 변환(Transform), 적재(Load) 로 이루어진 ETL 프레임워크는 RAG(Retrieval Augmented Generation)  
  활용 사례에서 데이터 처리의 근간을 이룬다.
- ETL 파이프라인은 원시 데이터 소스로부터 구조화된 벡터 저장소(Vector Store)로의 흐름을 처리하여, AI 모델이 검색할 수 있도록  
  데이터가 최적의 형식으로 준비되도록 한다.
- RAG 활용 사례는 대규모 언어 모델의 생성 능력을 보강하기 위해, 데이터 집합에서 관련 정보를 검색하여 생성된 출력의 품질과  
  적합성을 향상시키는 것을 목표로 한다.

### DocumentReaders (Extract)

- JSON, Text, Markdown, PDF, DOCX, PPT 등 다양한 형식의 문서를 읽고 파싱한다.
- 원시 데이터를 Spring AI 파이프라인에서 처리할 수 있는 표준 문서(Document) 구조로 변환한다.

### Transformers (Transform)

- 문서 원본을 분리(Chunking) 하고, 필요시 전처리 및 변환(Preprocessing & Transformation) 을 수행한다.
- 예: 긴 문서를 작은 단위로 나누기, 불필요한 텍스트 제거, Embedding 변환 준비, metadata 추가하기

### Writers (Load)

- 전처리된 데이터를 VectorStore, 파일 등에 저장한다.
- 예: 벡터 임베딩을 Milvus, Pinecone, Elasticsearch, Redis 등에 기록.

## Chat Memory (Multi-turn)

- Multi-turn 이라고 주로 불린다.
- 대규모 언어 모델(LLM)은 상태 비저장(stateless) 방식으로, 이전 상호작용에 대한 정보를 유지하지 않는다.
- 이를 해결하기 위해 LLM과의 여러 상호작용에서 정보를 저장하고 검색할 수 있는 채팅 메모리 기능을 제공

### 해당 기능은 채팅 메모리를 관리하도록 설계됨

- Chat Memory: 대규모 언어 모델이 대화 전반에 걸쳐 **맥락적** 인식을 유지하기 위해 보관하고 사용하는 정보
- Chat History: 사용자와 모델 간에 교환된 모든 메시지를 포함한 전체 대화 기록입니다.

## Tool Calling

- Function Calling 이라고도 불리며 AI 모델이 자체 지식만으로는 해결할 수 없는 문제를 풀기 위해,  
  외부의 API, DB, 웹 서비스, 파일 시스템, 액션 실행 등을 호출할 수 있게 해주는 방식

> 현재 모델과 도구 실행과 관련해 교환되는 내부 메시지는 사용자에게 노출되지 않는다.  
> 만약 이러한 메시지에 접근해야 한다면, 사용자 제어형(User-controlled) 도구 실행 방식을 사용해야 한다.

### Tool Calling의 주요 활용

#### 1. 정보 검색 (Information Retrieval)

- 모델의 지식 한계를 보완하기 위해 외부 데이터를 가져오는 경우
- 예시:
  - DB에서 특정 고객 레코드 조회
  - 웹 검색으로 최신 뉴스 가져오기
  - 날씨 API 호출해서 현재 기온 확인
- RAG(검색 기반 생성)에서 많이 사용됨
- 목표는 모델에 대한 지식을 보강하여 다른 대답에 대답 할 수없는 질문에 대답 할 수 있도록 하는것

#### 2. 액션 실행 (Taking Action)

- 단순히 데이터 검색이 아니라 시스템에 변화를 주는 동작을 실행하는 경우
- 예시:
  - 이메일 전송
  - DB에 새 레코드 생성
  - 예약/결제 처리
  - 코드 자동 생성 후 빌드 실행
- 모델이 "의도"만 전달하면, 실제 실행은 애플리케이션이 담당
- 목표는 인간의 개입 또는 명시적 프로그래밍이 필요한 작업을 자동화하는 것

### 중요 보안 사항

- 모델은 도구(API)에 직접 접근하지 않는다.
- 일반적으로 도구 호출을 모델 기능으로 언급하지만 실제로 도구 호출 로직을 제공하는 것은  
  클라이언트 응용 프로그램에 달려 있다.

### ToolExecutionEligibilityPredicate

- 도구 호출이 실행 가능한지 여부를 결정하는 로직
- 기본적으로 ToolCallingChatOptions 의 internalToolExecutionEnabled 속성 값에 따라 처리됨 (설정하지 않을 경우 true)

### Troubleshooting

- tool calling 을 지원하지 않는 model 이 있음
  - qwen2.5-coder 의 경우 코드 생성, 리뷰 등에 최적화된 모델이므로 지원되지 않음
    - https://github.com/vllm-project/vllm/issues/10952
    - https://github.com/QwenLM/Qwen3-Coder/issues/180

## Code & Examples

- https://github.com/spring-ai-community/awesome-spring-ai?tab=readme-ov-file#code--examples
- https://github.com/ThomasVitale/llm-apps-java-spring-ai/blob/main/observability/models/observability-models-openai/README.md
