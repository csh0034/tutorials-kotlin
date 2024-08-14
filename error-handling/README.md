# error-handling

## Filter 예외 ControllerAdvice 로 공통 처리

Tomcat 의 요청은 Filter -> Servlet 으로 동작함 Filter 의 경우 ApplicationFilterChain 을 순회하며 처리한다.

해당 filter chain 을 생성하고 실행하는 대상은 StandardWrapperValve 이다.

1. StandardWrapperValve
2. ApplicationFilterChain
3. Filters....

Valve 란?

- 특정 컨테이너와 연관된 요청 처리 구성 요소.
- 일련의 Valve는 일반적으로 파이프라인으로 서로 연관되어 있음
- Valve 라는 이름은 실제 파이프라인에서 흐름을 통제하는 대상이기 때문에 지어짐

StandardWrapperValve 란?

- 컨테이너 구현에 대한 기본 동작을 구현하는 Valve
- 실제 FilterChain 을 실행하는 진입점

Valve 실행순서 역순

- StandardWrapperValve
- StandardContextValve
- StandardHostValve

filter 에서 예외가 throw 되면 StandardWrapperValve 여러 catch 구문에서 처리된다.

이때 exception 메서드를 보면 하단과 같음.

- 예외를 request 에 넣고 error set
- Executable War error page

파이프라인 이전에 있는 StandardHostValve 까지 내려가게 되면 request.getAttribute(RequestDispatcher.ERROR_EXCEPTION) 를
체크하고 예외가 있을 경우 TomcatEmbeddedContext 설정된 에러페이지로 보냄.

- TomcatEmbeddedContext 에 ErrorPage 세팅을 어디서하는지?
    - ErrorPageCustomizer 를 통해 TomcatServletWebServerFactory 에 설정됨.

실제 전송하는 부분  
StandardHostValve.custom()

- rd.forward(request.getRequest(), response.getResponse());
- forward 란?
  - ApplicationDispatcher.invoke 에서 처리되며 ApplicationFilterChain 을 forward 처리에 맞게 만들어서 요청을 다시 처리함.

### 결론

Controller 의 예외 처리를 담당하는 GlobalExceptionHandler 를 필터에 예외 처리로도 사용 가능함.

1. ErrorController 를 재정의하고 전달된 예외를 발생시키도록 Bean 추가
2. 필터에서 예외 발생
3. ApplicationFilterChain > StandardWrapperValve 에서 예외 request 에 저장
4. StandardHostValve 에서 등록된 ErrorPage 경로로 forward
5. ApplicationDispatcher 에서 Error Dispatcher Type 에서 처리 가능한 ApplicationFilterChain 을 생성
6. ApplicationFilterChain 정상종료후 DispatcherServlet 진입
7. 추가한 ErrorController 메서드에서 필터의 예외 throw
8. GlobalExceptionHandler 에서 처리

#### 추가사항

- StandardWrapperValve 에서 예외 저장시에 ERROR 로 로그를 출력함.
- org.apache.catalina.core.ContainerBase off 하면됨.
