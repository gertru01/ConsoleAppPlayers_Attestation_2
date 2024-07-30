package ru.inno.course.player.matchers;

import org.assertj.core.api.AbstractAssert;
import ru.inno.course.player.model.Player;

public class PlayerMatcher extends AbstractAssert<PlayerMatcher, Player> {

    private PlayerMatcher(Player player, Class<?> selfType) {
        super(player, selfType);
        System.out.println("Создали PlayerMatcher");
    }

    public static PlayerMatcher assertThat(Player player){
        return new PlayerMatcher(player, PlayerMatcher.class);
    }

    public PlayerMatcher hasPoints(int pointsTobe){
        if (super.actual.getPoints() != pointsTobe){
            failWithMessage("Количество очков не совпало: " + super.actual.getPoints());
        }
        return this;
    }

    public void hasNick(String nickToBe){
        if (!super.actual.getNick().equals(nickToBe) ){
            failWithMessage("никнеймы не совпали: " + super.actual.getNick());
        }
    }

}
