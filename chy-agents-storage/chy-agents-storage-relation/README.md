# CHY Agents - Storage Relation

This submodule provides relational database integrations for CHY Agents, enabling efficient structured data storage and retrieval.

## Supported Relational Databases

- **PostgreSQL**: Advanced open-source relational database with extensive features
- **MySQL**: Popular open-source relational database management system
- **Neo4j**: Graph database platform for connected data relationships
- **H2**: In-memory database for testing and development purposes

## Key Features

- **Entity Management**: Streamlined entity definition and mapping
- **Repository Pattern**: Interface-based data access abstraction
- **Transaction Support**: ACID-compliant transaction management
- **Relationship Modeling**: Define and navigate complex data relationships
- **Query Optimization**: Efficient querying for high-performance applications
- **Connection Pooling**: Optimized database connection management

## Configuration

### PostgreSQL Example

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

## Usage

### JPA Repository Example

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