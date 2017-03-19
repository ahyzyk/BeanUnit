# BeanUnit
Easy unit testing for CDI beans.


Test phases:
1. Start
1. @BeanImplementations
1. Initialize beans
1. Begin EntityManager transaction
1. @BeforeDBUnit
1. DB before annotations 
    1. @ClearTable 
    1. @UsingDataSet 
1. @Before
1. @Test
1. @After
1. Test ends without error :
    1. @PreDestroy on used beans
    1. @ShouldMatchDataSet        
1. @ClearTable
1. @AfterDBUnit
1. End of transaction
1. End


TODO: 
Dependency injection on method

@TransactionManagement
@TransactionAttribute

@Interceptors

MessageDriven

Timer Service

@Resource

