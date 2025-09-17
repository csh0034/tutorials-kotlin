# Spring AI

## 소개

- https://docs.spring.io/spring-ai/reference/index.html
- 목표는 불필요한 복잡성 없이 인공지능 기능을 통합한 애플리케이션 개발을 간소화하는 것
- LangChain과 LlamaIndex와 같은 유명한 Python 프로젝트에서 영감을 얻었지만 포팅한것은 아님
- 차세대 생성 AI 애플리케이션이 Python 개발자만을 위한 것이 아니라 다양한 프로그래밍 언어에서  
  널리 사용될 것이라는 믿음을 바탕으로 시작

> Connecting your enterprise Data and APIs with AI Models

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

## Chat Memory

- 대규모 언어 모델(LLM)은 상태 비저장(stateless) 방식으로, 이전 상호작용에 대한 정보를 유지하지 않는다.
- 이를 해결하기 위해 LLM과의 여러 상호작용에서 정보를 저장하고 검색할 수 있는 채팅 메모리 기능을 제공

### 해당 기능은 채팅 메모리를 관리하도록 설계됨

- Chat Memory: 대규모 언어 모델이 대화 전반에 걸쳐 **맥락적** 인식을 유지하기 위해 보관하고 사용하는 정보
- Chat History: 사용자와 모델 간에 교환된 모든 메시지를 포함한 전체 대화 기록입니다.

## Code & Examples

- https://github.com/spring-ai-community/awesome-spring-ai?tab=readme-ov-file#code--examples
- https://github.com/ThomasVitale/llm-apps-java-spring-ai/blob/main/observability/models/observability-models-openai/README.md
