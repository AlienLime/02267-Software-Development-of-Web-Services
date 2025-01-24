/*
 * Author: Benjamin Noah Lumbye (s204428)
 * Description:
 * Represents a token.
 * A token is a unique identifier (represented as a UUID).
 */

package dtu.group17.dtu_pay_client;

import java.util.UUID;

public record Token(UUID id) {}
