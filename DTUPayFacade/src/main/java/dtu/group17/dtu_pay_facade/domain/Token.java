/*
 * Author: Katja Kaj (s123456)
 * Description:
 * Represents a token.
 * A token is a unique identifier (represented as a UUID).
 */


package dtu.group17.dtu_pay_facade.domain;

import java.util.UUID;

public record Token(UUID id) {}
