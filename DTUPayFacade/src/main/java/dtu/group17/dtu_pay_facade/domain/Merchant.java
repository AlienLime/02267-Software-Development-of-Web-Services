/*
 * Author: Katja Kaj (s123456)
 * Description:
 * A merchant must have an ID for the DTU Pay app, an account ID for the bank, a first name, a last name, and a CPR number.
 * The ID can temporarily be null, before it is assigned by the DTU Pay app.
 */

package dtu.group17.dtu_pay_facade.domain;

import java.util.UUID;

public record Merchant(UUID id, String firstName, String lastName, String cpr) {}
