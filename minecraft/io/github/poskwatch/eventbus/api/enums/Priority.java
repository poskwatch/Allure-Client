package io.github.poskwatch.eventbus.api.enums;

public enum Priority {

    VERY_LOW (0),
    LOW (1),
    MEDIUM (2),
    HIGH (3),
    VERY_HIGH (4);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
