## WorkLog

### STEP1
* 개발환경 구성
* 껍데기 API 구현하기
* 도메인 구현

#### TestCase 구현 및 RestDocs 작성
* IntegrationTests와 UnitTests
  - Controller Request/Response 검증을 위한 UnitTests 작성
  - Service/Repository/Model 구현 시 비즈니스 로직 검증을 위한 IntegrationTests 작성  
* 위와 같이 할 경우 Controller TestCase 중복 코드가 있지 않을까?
* 실제로 해보니, 복잡한 데이터를 리턴하는 서비스 메소드를 Mock Bean으로 정의하기 어렵다. Test Data를 통해서 하는것이 쉽다. 그렇다면 Unit Test로 ControllerTest를 만들어서 Request/Response 정의하려고 하는 방법은 좋지 않은 것 같다. 
그리고 Test를 위해서 Dto를 정의하는 경우, 별도의 Setter나 Builder를 만들어줘야 한다. <s>Controller Unit Test는 Validation Check를 하고, Documentation은 Controller Integration Test에서 하자.</s> 이것을 제한적으로(접근제한자 default로) 허용하는 것은 어떨까?

#### Layer Architecture
* Controller Request/Response Dto와 Service Parameter/Retrun Dto 는 공유 OR 별도
 - 공유한다면 controller와 service는 동일 package 
* Service Interface를 만들까?
  - controller부터 Top Down 방식으로 구현시 Interface가 있는것이 편하다. method signature만 정의해나가면 되므로,..
  - 그런데 전체 빌드를 통과하려면 impl class가 있어야 한다.
  - 만들지 말자.
 
#### Domain modeling
Controller 부터 구현을 했는데, 도메인 부터 하는게 좋겠다. Dto를 별도 만들지, Domain을 노출할지, 서비스 메소드는 어떻게 구현할지 등을 도메인이 없는 상태에서 구현하기 힘들다.
 
### STEP2
#### Unit Tests를 작성하면서 각 레이어 구현
* Controller/Service 분리, Dto 공유하지 말자
* Service Unit Tests 작성시 DB 연동관련 목 객체 정의하는 작업이 번거롭다. 그냥 리파지토리 연동해서 하는 것이 더 효율적으로 판단됨.
* 할당된 돈을 가져갈때 동시성 이슈에 관하여 분배를 효율적으로 하기 위해, 자원 경쟁을 줄이기 위해 분배 데이터를 랜덤하게 할당하면 효율적일까?
 
#### Exception Handling
* [kb2019](https://github.com/jhkim105/kb2019/tree/master/src/main/java/com/example/demo/exception) 
 
#### Repository Tests(@DataJpaTest)
* JpaConfig를 load하지 못한다. @Import문을 추가했음. 
 
### STEP3 - Integration Test
* Integration Tests 작성
 - Jpa 관련 에러나서 필요한 곳에서 JpaConfig를 import 하는 것으로 변경
 - Unit Tests에서 걸러져야 할 오류들이 Integration Test에서 다수 발견됨.
 - MockMvc를 활용한 테스트는 응답값을 검증하는 것인데, Integration Tests에 적합한가?
   - 실제로 Data가 의도한 대로 생성되었는지 확인하기가 어려움.
   - Service method에 대한 Integration Tests 작성 필요함. 이럴 경우 기존 Integration Test에 통합할 것인가 아니면 별도(ServiceIntegrationTest)로 작성할 것인가? 
 - 연결된 테스트를 효과 적으로 하는 방법? give -> take
* @Test와 @Transactional을 주면 insert/update 쿼리가 실행되고, 테스트 후에 Rollback한다.
* MockBean에서 메소드 파라미터로 사용되는 Dto는 EqualsAndHashCode를 override 해야 한다.

### STEP4 - Concurrency Test
* Distributed Lock using Hazelcast
  - https://docs.hazelcast.org/docs/latest/manual/html-single/#lock 
  
* 동시성 테스트 방법
  - Parallel Streams
  - Fork/Join

* References
  - https://www.baeldung.com/java-testing-multithreaded
  - https://www.baeldung.com/java-fork-join
 
 
### JPA Pessimistic Locking
* https://www.baeldung.com/jpa-pessimistic-locking
* Lock Mode
  - PESSIMISTIC_READ: shared lock, prevent update/delete
  - PESSIMISTIC_WRITE: exclusive lock,  prevent lock, read/update/delete
  - PESSIMISTIC_FORCE_INCREMENT: PESSIMISTIC_WRITE and version 증가
* PESSIMISTIC_READ, PESSIMISTIC_WRITE 둘다 for update 쿼리를 실행함. shared lock이 안되나? (H2)
* MySQL innoDB는 LOCK IN SHARED MODE와 FOR UPDATE를 지원한다. 
  - LOCK IN SHARED MODE는 동일 트랜잭션이 끝나기 전까지만 유효하므로, auto commit mode를 꺼야 한다.
  - FOR UPDATE를 SELECT를 가져온 이후로 해당 ROW에 대해 다른 세션의 SELECT, UPDATE, DELETE 등의 쿼리가 모두 잠김 상태가 된다. 
  - PESSIMISTIC_READ: 
    - lock in share mode 
    - parallelStream()에서만 테스트 케이스 통과
    ```java
      @Lock(LockModeType.PESSIMISTIC_READ)
      Optional<MoneyGive> findByTokenAndFinishedDateIsNull(String token);
    ```
    ```sql
    select moneygive0_.id as id1_0_, moneygive0_.amount as amount2_0_, moneygive0_.count as count3_0_, moneygive0_.created_by as created_4_0_, moneygive0_.created_date as created_5_0_, moneygive0_.finished_date as finished6_0_, moneygive0_.room_id as room_id7_0_, moneygive0_.token as token8_0_, moneygive0_.version as version9_0_ from km_money_give moneygive0_ where moneygive0_.token=? and (moneygive0_.finished_date is null) lock in share mode
    ```
    
  - PESSIMISTIC_WRITE
    - for update
    - 테스트케이스 통과
    ```java
     @Lock(LockModeType.PESSIMISTIC_WRTE)
     Optional<MoneyGive> findByTokenAndFinishedDateIsNull(String token);
    ``` 
    ```sql
    select moneygive0_.id as id1_0_, moneygive0_.amount as amount2_0_, moneygive0_.count as count3_0_, moneygive0_.created_by as created_4_0_, moneygive0_.created_date as created_5_0_, moneygive0_.finished_date as finished6_0_, moneygive0_.room_id as room_id7_0_, moneygive0_.token as token8_0_, moneygive0_.version as version9_0_ from km_money_give moneygive0_ where moneygive0_.token=? and (moneygive0_.finished_date is null) for update
    ```
* Test시 주의사항
  - 테스트케이스에 @Transactional을 주지 않아야 한다. multiple thread 환경에서의 테스트이므로 @Transactional 주더라도 의도한대로(rollback) 동작하지 않는다.    
    
* entityManager를 사용하는 경우 PESSIMISTIC_FORCE_INCREMENT을 사용하는 경우, 테스트케이스 무한로딩 상태가 됨. information_schema.innodb_lock_waits에 데이터는 없음
```java
  public MoneyTake take(MoneyTakeDto moneyTakeDto) {
    Optional<MoneyGive> moneyGiveOptional = moneyGiveRepository.findByTokenAndFinishedDateIsNull(moneyTakeDto.getToken());
    moneyGiveOptional.orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTS_MONEY_GIVE));
    MoneyGive moneyGive = moneyGiveOptional.get();
    entityManager.lock(moneyGive, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
```
* method annotation 중 @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)를 사용할 경우 @DataJpaTest에서 에러 발생
```

java.lang.NullPointerException
	at org.hibernate.type.IntegerType.next(IntegerType.java:70)
	at org.hibernate.type.IntegerType.next(IntegerType.java:22)
	at org.hibernate.persister.entity.AbstractEntityPersister.forceVersionIncrement(AbstractEntityPersister.java:1861)
	at org.hibernate.event.internal.DefaultPostLoadEventListener.onPostLoad(DefaultPostLoadEventListener.java:54)
	at org.hibernate.event.service.internal.EventListenerGroupImpl.fireEventOnEachListener(EventListenerGroupImpl.java:102)
	at org.hibernate.internal.FastSessionServices.firePostLoadEvent(FastSessionServices.java:295)
	at org.hibernate.engine.internal.TwoPhaseLoad.postLoad(TwoPhaseLoad.java:512)
	at org.hibernate.loader.Loader.initializeEntitiesAndCollections(Loader.java:1231)
	at org.hibernate.loader.Loader.processResultSet(Loader.java:1001)
	at org.hibernate.loader.Loader.doQuery(Loader.java:959)
	at org.hibernate.loader.Loader.doQueryAndInitializeNonLazyCollections(Loader.java:349)
	at org.hibernate.loader.Loader.doList(Loader.java:2850)
	at org.hibernate.loader.Loader.doList(Loader.java:2832)
	at org.hibernate.loader.Loader.listIgnoreQueryCache(Loader.java:2664)

```

### JPA Optimistic Locking
* Lock Mode
  - OPTIMISTIC
  - OPTIMISTIC_FORCE_INCREMENT
* One(MoneyGive)측은 데이터 변경이 없으므로, Many(MoneyTake) 쪽에 Lock 설정
* OPTIMISTIC Lock Mode 일 경우 StaleObjectStateException 발생
```
Caused by: org.hibernate.StaleObjectStateException: Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect) : [com.example.demo.money.domain.MoneyTake#9a2e595e-45cf-4939-93d6-a24c4dde57b3]
	at org.hibernate.persister.entity.AbstractEntityPersister.check(AbstractEntityPersister.java:2610)
	at org.hibernate.persister.entity.AbstractEntityPersister.update(AbstractEntityPersister.java:3454)
	at org.hibernate.persister.entity.AbstractEntityPersister.updateOrInsert(AbstractEntityPersister.java:3317)
	at org.hibernate.persister.entity.AbstractEntityPersister.update(AbstractEntityPersister.java:3731)
	at org.hibernate.action.internal.EntityUpdateAction.execute(EntityUpdateAction.java:201)
	at org.hibernate.engine.spi.ActionQueue.executeActions(ActionQueue.java:604)
	at org.hibernate.engine.spi.ActionQueue.lambda$executeActions$1(ActionQueue.java:478)

```
* OPTIMISTIC_FORCE_INCREMENT Lock Mode 일 경우 ObjectOptimisticLockingFailureException 발생
* 익셉션 발생하면서 요청이 실패하게 되므로 자동으로 재시도를 하게 하려면 추가적인 구현이 필요하다. take_using_executorService() test의 경우 10번의 시도중 9번이 실패하여 테스트케이스가 실패한다. parallelStream()과 joinFork()는 테스트케이스 성공함.
 
 
## Summary
### Development Issues
* Test Case를 위한 코드들을 피하고 싶다. 
  - Dto에 값을 담기위해 Dto가 builder를 제공해야 하는 경우가 있음. 실 코드에서는 DB Domain객체로 부터 값을 할당함.
  - 이것을 허용해야 되는 상황이 많을 듯. builder 정도는 허용해도 괜찮지 않을까? 생성에는 열려있고, 변경에는 제한적이니까. 
* Service Unit Tests 작성시 DB 연동관련 목 객체 정의하는 작업이 번거롭다. 그냥 리파지토리 연동해서 하는 것이 더 효율적이지 않을까? 메소드 성격을 보고 선별적으로 Unit Test를 작성해야겠다.
* OneToMany 관계에서 대상 객체가 비즈니스 키가 없을 경우 cascade를 통한 저장시 hashcode 이슈가 있다. 이럴때는 Set보다는 List를 사용하는 것이 좋을까?
* Concurrency(Thread Safe)를 보장하기 위한 방법으로 DB Lock을 활용하는 것 보다, Redis(Hazelcast) Distributed Lock을 사용하는 것이 테스트 하기도 쉽고, 더 안전할 것 같다.


### Domain Issues
* 할당된 돈을 가져갈때 동시성 이슈에 관하여 분배를 효율적으로 하기 위해, 자원 경쟁을 줄이기 위해 분배 데이터를 랜덤하게 할당하면 효율적일까?


### Development Process & Strategy
* 도메인 구현
  - Setter를 두지 않는다.
  - Builder는 TestCase 작성시 필요하므로 작성한다.
  - Entity 정의시 nullable, insertable, updatable을 최대한 정의한다.
  
* Test Case 작성 및 Controller/Service/Repository 구현
  - RestDocs Documentation을 빠르게 해야하는 경우, Controller Unit Test를 먼저 빠르게 작성하고 그렇지 않은 경우 Integration Tests를 바로 작성한다.
  - Unit Tests는 필요성이 생길 때 마다 바로 바로 작성한다.
  - Controller Unit Tests와 Controller Integration Tests를 둘 다 작성하는 경우에 RestDocs, Validation Check 등을 담당하는 것이 좋겠다.
    
### Working Time
* STEP1 5h
* STEP2 5h
* STEP3 3h
* REFACTORING - 1h
* STEP4 3h 
 
## TODO
* 404 handling
 
 







