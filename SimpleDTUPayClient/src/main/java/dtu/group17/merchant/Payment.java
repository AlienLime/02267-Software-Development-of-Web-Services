package dtu.group17.merchant;
import dtu.group17.Token;


public record Payment(Token token, int amount, String merchantId) {}