package com.chy.agents.storage.relation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 关系存储接口
 * 用于存储实体之间的关系数据
 */
public interface RelationStorage {

    /**
     * 存储关系
     *
     * @param sourceId 源实体ID
     * @param targetId 目标实体ID
     * @param relationType 关系类型
     * @param metadata 元数据
     * @return 是否成功
     */
    boolean storeRelation(String sourceId, String targetId, String relationType, Map<String, Object> metadata);

    /**
     * 查询关系
     *
     * @param sourceId 源实体ID
     * @param relationType 关系类型
     * @return 相关实体ID列表
     */
    Map<String, Map<String, Object>> queryRelation(String sourceId, String relationType);

    /**
     * 删除关系
     *
     * @param sourceId 源实体ID
     * @param targetId 目标实体ID
     * @param relationType 关系类型
     * @return 是否成功
     */
    boolean deleteRelation(String sourceId, String targetId, String relationType);

    /**
     * 内存实现
     */
    @Component
    @ConditionalOnProperty(prefix = "chy.agents.storage.relation", name = "type", havingValue = "memory", matchIfMissing = true)
    class InMemoryRelationStorage implements RelationStorage {
        
        // 三级嵌套Map: 源ID -> 关系类型 -> 目标ID -> 元数据
        private final Map<String, Map<String, Map<String, Map<String, Object>>>> relations = new HashMap<>();

        @Override
        public boolean storeRelation(String sourceId, String targetId, String relationType, Map<String, Object> metadata) {
            relations.computeIfAbsent(sourceId, k -> new HashMap<>())
                    .computeIfAbsent(relationType, k -> new HashMap<>())
                    .put(targetId, new HashMap<>(metadata));
            return true;
        }

        @Override
        public Map<String, Map<String, Object>> queryRelation(String sourceId, String relationType) {
            return Optional.ofNullable(relations.get(sourceId))
                    .map(r -> r.get(relationType))
                    .orElse(new HashMap<>());
        }

        @Override
        public boolean deleteRelation(String sourceId, String targetId, String relationType) {
            return Optional.ofNullable(relations.get(sourceId))
                    .map(r -> r.get(relationType))
                    .map(m -> {
                        m.remove(targetId);
                        return true;
                    })
                    .orElse(false);
        }
    }
} 