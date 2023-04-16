package ua.klesaak.simpleconomy.manager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {
    private String playerName;
    private double money;
    private int coins;
}
