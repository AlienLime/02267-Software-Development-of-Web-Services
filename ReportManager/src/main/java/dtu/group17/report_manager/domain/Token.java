/*
 * Author: Stine Lund Madsen (s204425)
 * Description:
 * Represents a token.
 * A token is a unique identifier (represented as a UUID).
 */

package dtu.group17.report_manager.domain;

import java.util.UUID;

public record Token(UUID id) {}
