package diotviet.server.views.Document;

import org.springframework.beans.factory.annotation.Value;

public interface DocumentMetaView {
    Long getId();
    String getName();
    @Value("#{target.group.key}")
    String getKey();
    String getVersion();
}
