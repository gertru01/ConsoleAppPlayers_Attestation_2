package ru.inno.course.player.myTests.paramsProviders;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import ru.inno.course.player.model.Player;

import java.util.stream.Stream;

public class PointAndPlayersProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        Player player1 = new Player();
        player1.setNick("Player 1");
        player1.setPoints(90);

        Player player2 = new Player();
        player2.setNick("Player 2");
        player2.setPoints(-100);

        Player player3 = new Player();
        player3.setNick("Player 3");
        player3.setPoints(23);

        return Stream.of(
                Arguments.of(player1, 100, 190),
                Arguments.of(player2, 40, -60),
                Arguments.of(player3, 200, 123)
        );
    }
}
