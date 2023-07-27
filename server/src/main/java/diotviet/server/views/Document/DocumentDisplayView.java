package diotviet.server.views.Document;

import org.springframework.beans.factory.annotation.Value;

public interface DocumentDisplayView {
    String getId();
    @Value("#{target.group.key}")
    String getKey();
    String getContent();
}
