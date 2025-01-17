package dtu.group17.merchant;
import dtu.group17.Token;

import java.util.UUID;


public record Payment(Token token, int amount, UUID merchantId) {}