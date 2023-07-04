package diotviet.server.constants;

import lombok.Getter;

@Getter
public enum Status {
    PENDING(0),
    RESOLVED(1),
    ABORTED(2);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    /**
     * Generate Type from code
     *
     * @param code
     * @return
     */
    public static Status fromCode(int code) {
        return switch (code) {
            case 0 -> Status.PENDING;
            case 1 -> Status.RESOLVED;
            case 2 -> Status.ABORTED;
            default -> null;
        };
    }

    /**
     * Generate Type from code
     *
     * @param code
     * @return
     */
    public static Status fromCode(long code) {
        return fromCode((int) code);
    }
}
