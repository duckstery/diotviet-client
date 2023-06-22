package diotviet.server.views;

import org.springframework.beans.factory.annotation.Value;

public interface Visualize {
    /**
     * Src
     *
     * @return
     */
    @Value("#{T(org.apache.commons.lang3.StringUtils).isBlank(target.src) ? \"/files/default.jpeg\" : \"/files/\" + target.src}")
    String getSrc();

    /**
     * Set name
     *
     * @param name
     */
    Visualize setSrc(String src);
}
