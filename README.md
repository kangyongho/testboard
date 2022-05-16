# testboard
springboot로 게시판 만들어보기

###### 접속정보
[`testboard.duckdns.org`][12]  
`id: member` `pw: member321` > 로그인시 글삭제 권한 없음  
`id: admin` `pw: admin321`   > 로그인시 글삭제 권한 있음

###### 사용기술 및 IDE
<img src="https://img.shields.io/badge/springboot-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white"/><img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=Spring Security&logoColor=white"/>
<img src="https://img.shields.io/badge/jpa-59666C?style=flat-square&logo=Hibernate&logoColor=white"/>
<img src="https://img.shields.io/badge/h2-0078D4?style=flat-square&logo=h2 database&logoColor=white"/>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=MySQL&logoColor=white"/>
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=Thymeleaf&logoColor=white"/>
<img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=Java&logoColor=white"/>
<img src="https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=Bootstrap&logoColor=white"/>
<img src="https://img.shields.io/badge/Eclipse IDE-2C2255?style=flat-square&logo=Eclipse IDE&logoColor=white"/>

###### 구현동기
- 그동안 잊고 있었던 MVC 모델과 스프링의 의존성 주입 테스트 공부하기
- JPA ORM 사용
- SpringBoot 활용해보기
- Java Interface 가 필요한 상황 익히기

###### 참고자료
스프링 부트 퀵스타트 - 채규태 지음

###### 추가개발
Paging, Bootstrap CSS 적용

## 프로젝트 레이어 구조도
![autodraw testboard architecture](https://user-images.githubusercontent.com/14036077/167409041-cb41f423-c76a-4116-8c2c-8c832ca39ec3.png)

## 프로젝트 패키지 구조도
![testboard package architecture](https://user-images.githubusercontent.com/14036077/167633514-5d5625a2-7ab5-4577-a5c5-f8fcf41d9a3f.jpg)

## MVC Layer
`Dispatcher Servlet` `View` `Controller`  
thymeleaf 템플릿 문법 사용  
Controller URL 및 파리미터 처리  
## Business Layer
`Service Interface` `ServiceImpl` `Repository` `Entity`  
Service 인터페이스 구현체인 ServiceImpl 사용  
Entity 객체에 Table 연관관계 매핑  
DAO 개념에서 나온 Repository를 통한 객체 영속화 작업  

## MVC Layer code
#### getBoardList.html [link][0]
    <div class="center">
    	<h1 class="text-center">게시글 목록</h1>
    	<div class="text-right">
    	<span sec:authorize="isAuthenticated()">
    		<b><font color="red">
    		<span sec:authentication="principal.member.name"></span>
    		</font></b> 님 환영합니다.....................................
    		<a th:href="@{/system/logout}">로그아웃</a>&nbsp;&nbsp;&nbsp;
    		<a th:href="@{/admin/adminPage}">게시판 관리</a>
    	</span>
    	</div>
    	<!-- 검색 시작 -->
    	<form th:action="@{/board/getBoardList}" method="post">
    	<table class="table table-hover table-striped">
    	<tr>
    		<td align="right">
    			<select name="searchCondition">
    			<option value="TITLE">제목</option>
    			<option value="CONTENT">내용</option>
    			</select>
    			<input name="searchKeyword" type="text"/>
    			<input type="submit" value="검색"/>
    		</td>
    	</tr>
    	</table>
    	</form>
    	<!-- 검색종료 -->
    	<table class="table table-hover table-striped">
    		<thead>
    		<tr>
    			<th scope="col">번호</th>
    			<th scope="col">제목</th>
    			<th scope="col">작성자</th>
    			<th scope="col">등록일</th>
    			<th scope="col">조회수</th>
    		</tr>
    		</thead>
    		<tbody>
    		<tr th:each="board, state : ${boardList}">
    			<td th:text="${state.count}">
    			<td><a th:href="@{/board/getBoard(seq=${board.seq})}" th:text="${board.title}"></a></td>
    			<td th:text="${board.member.name}">
    			<td th:text="${#dates.format(board.createDate, 'yyyy-MM-dd')}">
    			<td th:text="${board.cnt}">
    		</tr>
    		</tbody>
    	</table>
    	<!-- 페이징 -->
    	<div th:if="${!boardList.isEmpty()}" class="text-center">
    		<ul class="pagination justify-content-center">
    			<li class="page-item" th:classappend="${!boardList.hasPrevious} ? 'disabled'">
    				<a class="page-link" th:href="@{|?page=${boardList.number-1}|}"><span>이전</span></a>
    			</li>
    			<li th:each="page: ${#numbers.sequence(0, boardList.totalPages-1)}"
    				th:if="${page >= boardList.number-5 and page <= boardList.number+5}"
                    th:classappend="${page == boardList.number} ? 'active'"
                    class="page-item">
                    <a th:text="${page}" class="page-link" th:href="@{|?page=${page}|}"></a>
    			</li>
    			<li class="page-item" th:classappend="${!boardList.hasNext} ? 'disabled'">
    				<a class="page-link" th:href="@{|?page=${boardList.number+1}|}"><span>다음</span></a>
    			</li>
    		</ul>
    	</div>
    	<div class="text-right">
    	<a th:href="@{/board/insertBoard}">게시글 등록</a>
    	</div>
    </div>
(thymeleaf namespace 적용)

#### BoardController [link][1]
    @Controller
    @RequestMapping("/board/")
    public class BoardController {
    	@Autowired	private BoardService boardService;

    	@RequestMapping("/getBoardList")
    	public String getBoardList(Model model, Search search, @RequestParam(value="page", defaultValue="0") int page) {
    		if(search.getSearchCondition() == null)
    			search.setSearchCondition("TITLE");
    		if(search.getSearchKeyword() == null)
    			search.setSearchKeyword("");
    		Page<Board> boardList = boardService.getBoardList(search, page);
    		model.addAttribute("boardList", boardList);
    		return "board/getBoardList";
    	}
getBoardList는 Model 객체에 정보매핑후 view(html)에 전달 - 게시판 목록 연결

## Business Layer code
### 서비스 객체
#### BoardService.interface [link][2]
    public interface BoardService {
    	void insertBoard(Board board);

    	void updateBoard(Board board);

    	void deleteBoard(Board board);

    	Board getBoard(Board board);

    	Page<Board> getBoardList(Search search, int page);
    }
인터페이스 사용하여 기능변경시 구현클래스만 교체하여 소스코드 수정 최소화  
만약 기능변경을 하지 않을 목적이면 인터페이스 사용없이 서비스클래스로 기능구현해야함

#### BoardServiceImpl.class [link][3]
    @Service
    public class BoardServiceImpl implements BoardService {

    	@Autowired	private BoardRepository boardRepo;

    	public void insertBoard(Board board) {
    		boardRepo.save(board);
    	}

    	public void updateBoard(Board board) {
    		Board findBoard = boardRepo.findById(board.getSeq()).get();

    		findBoard.setTitle(board.getTitle());
    		findBoard.setContent(board.getContent());
    		boardRepo.save(findBoard);
    	}

    	public void deleteBoard(Board board) {
    		boardRepo.deleteById(board.getSeq());
    	}

    	public Board getBoard(Board board) {
    		return boardRepo.findById(board.getSeq()).get();
    	}

    	public Page<Board> getBoardList(Search search, int page) {
    		BooleanBuilder builder = new BooleanBuilder();

    		QBoard qboard = QBoard.board;
    		if(search.getSearchCondition().equals("TITLE")) {
    			builder.and(qboard.title.like("%" + search.getSearchKeyword() + "%"));
    		} else if(search.getSearchCondition().equals("CONTENT")) {
    			builder.and(qboard.content.like("%" + search.getSearchKeyword() + "%"));
    		}
    		Pageable pageable = PageRequest.of(page, 5, Sort.Direction.DESC, "seq");
    		return boardRepo.findAll(builder, pageable);
    	}
    }
서비스기능 구현 클래스  
페이징: PageRequest > Pageable 리턴. 매개변수 page부터 5개의 데이터만 조회하도록 쿼리를 생성해준다.

### Domain, Entity, Repository 영속성 객체
#### Board.class [link][4]
    @Getter
    @Setter
    @ToString(exclude="member")
    @Entity
    public class Board {

    	@Id
    	@GeneratedValue
    	private Long seq;

    	private String title;

    	private String content;

    	@Temporal(TemporalType.TIMESTAMP)
    	@Column(updatable=false)
    	private Date createDate = new Date();

    	@Column(updatable=false)
    	private Long cnt = 0L;

    	@ManyToOne
    	@JoinColumn(name="MEMBER_ID", nullable=false, updatable=false)
    	private Member member;

    	public void setMember(Member member) {
    		this.member = member;
    		member.getBoardList().add(this);
    	}
    }
Board 게시판 Entity 테이블 매핑. 다대일(N:1) @ManyToOne 사용

#### Member.class [link][5]
    @Getter
    @Setter
    @ToString(exclude="boardList")
    @Entity
    public class Member {
    	@Id
    	@Column(name="MEMBER_ID")
    	private String id;

    	private String password;

    	private String name;

    	@Enumerated(EnumType.STRING)
    	private Role role;

    	private boolean enabled;

    	@OneToMany(mappedBy="member", fetch=FetchType.EAGER)
    	private List<Board> boardList = new ArrayList<Board>();
    }
Member 회원 Entity 테이블 매핑. 일대다(1:N) @OneToMany 사용

#### BoardRepository.interface [link][6]
    public interface BoardRepository extends CrudRepository<Board, Long>, QuerydslPredicateExecutor<Board> {
    	@Query("SELECT b FROM Board b")
    	Page<Board> getBoardList(Pageable pageable);
    }
springboot jpa CrudRepository 상속하여 CRUD 구현

#### MemberRepository.interface [link][7]
    public interface MemberRepository extends CrudRepository<Member, String>{

    }
springboot jpa CrudRepository 상속하여 CRUD 구현

### Spring Security 로그인, 패스워드암호화
#### SecurityController.class [link][8]
    @Controller
    public class SecurityController {
    	@Autowired	private MemberRepository memberRepo;
    	@Autowired	private BoardRepository boardRepo;
    	@Autowired	private PasswordEncoder encoder;

    	@GetMapping("/system/login")
    	public void login( ) {}

    	@GetMapping("/system/accessDenied")
    	public void accessDenied() {}

    	@GetMapping("/system/logout")
    	public void logout() {}

    	@GetMapping("/admin/adminPage")
    	public void admin() {}

#### SecurityConfig.class [link][9]
    @EnableWebSecurity
    public class SecurityConfig extends WebSecurityConfigurerAdapter {
    	@Autowired
    	private SecurityUserDetailsService userDetailsService;

    	@Override
    	protected void configure(HttpSecurity security) throws Exception {
    		security.userDetailsService(userDetailsService);

    		security.authorizeRequests().antMatchers("/", "/system/**").permitAll();
    		security.authorizeRequests().antMatchers("/board/**").authenticated();
    		security.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN");

    		security.csrf().disable();
    		security.formLogin().loginPage("/system/login").defaultSuccessUrl("/board/getBoardList", true);
    		security.exceptionHandling().accessDeniedPage("/system/accessDenied");
    		security.logout().logoutUrl("/system/logout").invalidateHttpSession(true).logoutSuccessUrl("/");
    	}

    	@Bean
    	public PasswordEncoder passwordEncoder() {
    		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    	}
    }
antMatchers 이용해 url 접근권한 설정  
PasswordEncoder @Bean 등록후 패스워드암호화 사용 (수동으로 회원가입 초기화 코드 작성) [link][10]

#### 검색기능: QueryDSL 사용 (해당 부분 스프링부트 퀵스타트 참고했습니다)
### BoardServiceImpl.class [link][11]
    @Service
    public class BoardServiceImpl implements BoardService {
      public Page<Board> getBoardList(Search search, int page) {
    		BooleanBuilder builder = new BooleanBuilder();

		QBoard qboard = QBoard.board;
		if(search.getSearchCondition().equals("TITLE")) {
			builder.and(qboard.title.like("%" + search.getSearchKeyword() + "%"));
		} else if(search.getSearchCondition().equals("CONTENT")) {
			builder.and(qboard.content.like("%" + search.getSearchKeyword() + "%"));
		}
		Pageable pageable = PageRequest.of(page, 5, Sort.Direction.DESC, "seq");
		return boardRepo.findAll(builder, pageable);
	}
QBoard QueryDSL 객체 메서드 통해 데이터베이스 검색 Query 동적 생성

[0]: https://github.com/kangyongho/testboard/blob/main/src/main/resources/templates/board/getBoardList.html
[1]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/controller/BoardController.java
[2]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/service/BoardService.java
[3]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/service/BoardServiceImpl.java
[4]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/domain/Board.java
[5]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/domain/Member.java
[6]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/persistence/BoardRepository.java
[7]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/persistence/MemberRepository.java
[8]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/controller/SecurityController.java
[9]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/security/SecurityConfig.java
[10]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/controller/SecurityController.java
[11]: https://github.com/kangyongho/testboard/blob/main/src/main/java/com/example/demo/service/BoardServiceImpl.java
[12]: http://testboard.duckdns.org

