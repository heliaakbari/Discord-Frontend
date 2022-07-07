package com.example.mutual;
import java.io.Serializable;

/**
 * an Enum class to define the relationship between two users which can be : friend, blocked, rejected and friend pending (for requests)
 */
public enum Relationship implements Serializable {
    Friend,
    Block,
    Friend_pending,
    Rejected;
    private static final long serialVersionUID = 524225788387725L;

}
