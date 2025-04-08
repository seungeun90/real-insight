package io.insight.real.apt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {
    private final ObjectMapper objectMapper;

    @Bean
    public OpenApiCustomizer autoExampleCustomizer() {
        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                for (Map.Entry<String, ApiResponse> entry : responses.entrySet()) {
                    ApiResponse response = entry.getValue();
                    Content content = response.getContent();
                    if (content != null) {
                        MediaType mediaType = null;

                        if (content.containsKey("application/json")) {
                            mediaType = content.get("application/json");
                        } else if (content.containsKey("*/*")) {
                            mediaType = content.get("*/*"); // 👈 지금 이 케이스!
                        }

                        if (mediaType != null) {
                            Schema<?> schema = mediaType.getSchema();
                            if (schema != null) {
                                Schema<?> effectiveSchema = schema;
                                boolean isArray = false;

                                if ("array".equals(schema.getType()) && schema.getItems() != null) {
                                    effectiveSchema = schema.getItems();
                                    isArray = true;
                                }

                                String schemaName = extractSchemaName(effectiveSchema);
                                if (schemaName != null) {
                                    Map<String, Object> example = new HashMap<>();
                                    example.put("success", true);
                                    if (isArray) {
                                        example.put("data", List.of(Map.of("exampleFrom", schemaName)));
                                    } else {
                                        example.put("data", Map.of("exampleFrom", schemaName));
                                    }
                                    example.put("error", null);
                                    example.put("pagination", null);

                                    try {
                                        String json = objectMapper.writeValueAsString(example);
                                        mediaType.setExample(json); // 적용
                                    } catch (Exception ignored) {
                                    }
                                }
                            }
                        }
                    }
                }
            });
        });
    }

    private String extractSchemaName(Schema<?> schema) {
        if (schema.get$ref() != null) {
            // $ref 값이 있을 경우 → 예: "#/components/schemas/CityBasicData"
            return schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
        } else if (schema.getName() != null) {
            // inline 스키마에 name이 정의된 경우
            return schema.getName();
        } else {
            return null;
        }
    }
}
