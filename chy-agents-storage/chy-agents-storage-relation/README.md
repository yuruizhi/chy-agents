# CHY Agents - 存储关系

该子模块为CHY Agents提供关系型数据库集成，实现高效的结构化数据存储和检索。

## 支持的关系型数据库

- **PostgreSQL**: 具有丰富功能的高级开源关系型数据库
- **MySQL**: 流行的开源关系型数据库管理系统
- **Neo4j**: 面向关联数据关系的图形数据库平台
- **H2**: 用于测试和开发目的的内存数据库

## 核心特性

- **实体管理**: 简化的实体定义和映射
- **存储库模式**: 基于接口的数据访问抽象
- **事务支持**: 符合ACID的事务管理
- **关系建模**: 定义和导航复杂的数据关系
- **查询优化**: 高性能应用的高效查询
- **连接池**: 优化的数据库连接管理

## 配置

### PostgreSQL示例

```java
@Configuration
public class PostgresqlConfig {
    @Bean
    @ConditionalOnProperty(name = "chy.agents.storage.relation.type", havingValue = "postgresql")
    public DataSource postgresqlDataSource(
            @Value("${chy.agents.storage.relation.postgresql.url}") String url,
            @Value("${chy.agents.storage.relation.postgresql.username}") String username,
            @Value("${chy.agents.storage.relation.postgresql.password}") String password) {
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        
        return new HikariDataSource(config);
    }
}
```

## 使用示例

### JPA仓库示例

```java
@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByTitleContaining(String titleFragment);
    
    @Query("SELECT c FROM Conversation c WHERE c.createdAt > :date")
    List<Conversation> findRecentConversations(@Param("date") LocalDateTime date);
}

@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }
    
    public Conversation createConversation(String title) {
        Conversation conversation = Conversation.builder()
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
                
        return conversationRepository.save(conversation);
    }
    
    public List<Conversation> getRecentConversations(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        return conversationRepository.findRecentConversations(cutoffDate);
    }
} 