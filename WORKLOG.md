# WorkLog

## STEP1
* 개발환경 구성
* 껍데기 API 구현하기
* 도메인 구현
* 
### TestCase 구현 및 RestDocs 작성
* IntegrationTests와 UnitTests
  - Controller Request/Response 검증을 위한 UnitTests 작성
  - Service/Repository/Model 구현 시 비즈니스 로직 검증을 위한 IntegrationTests 작성  
* 위와 같이 할 경우 Controller TestCase 중복 코드가 있지 않을까?
* 실제로 해보니, 복잡한 데이터를 리턴하는 서비스 메소드를 Mock Bean으로 정의하기 어렵다. Test Data를 통해서 하는것이 쉽다. 그렇다면 Unit Test로 ControllerTest를 만들어서 Request/Response 정의하려고 하는 방법은 좋지 않은 것 같다. 
그리고 Test를 위해서 Dto를 정의하는 경우, 별도의 Setter나 Builder를 만들어줘야 한다.
* Controller Unit Test는 Validation Check를 하고, Documentation은 Controller Integration Test에서 하자.

### Layer Architecture
* Controller Request/Response Dto와 Service Parameter/Retrun Dto 는 공유 OR 별도
 - 공유한다면 controller와 service는 동일 package
* Service Interface를 두자
 - controller부터 Top Down 방식으로 구현시 Interface가 있는것이 편하다. method signature만 정의해나가면 되므로,..
 - 그런데 전체 빌드를 통과하려면 impl class가 있어야 한다.
 
### Domin modeling
 Controller 부터 구현을 했는데, 도메인 부터 하는게 좋겠다. Dto를 별도 만들지, Domain을 노출할지, 서비스 메소드는 어떻게 구현할지 등을 도메인이 없는 상태에서 구현하기 힘들다.
 
 
# Issues
 * Test Case를 위한 코드들을 피하고 싶다. 
   - Dto에 값을 담기위해 Dto가 builder를 제공해야 하는 경우가 있음. 실 코드에서는 DB Domain객체로 부터 값을 할당함.
   
 
 
# Summary


## 개발순서
* 도메인 구현
* Test Case 작성 및 Controller/Service/Repository 구현
  * Controller Integration Tests
  * Service/Repository Unit Tests
  * Controller Unit Tests
  

 
 







