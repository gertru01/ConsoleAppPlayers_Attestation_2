package ru.inno.course.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.course.player.ext.GeneratePlayers;
import ru.inno.course.player.ext.TestDataResolver;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({TestDataResolver.class})
public class PlayerServiceHamcrestTest {

    @Test
    @DisplayName("Проверить запуск с пустым хранилищем")
    public void runWithEmptyFile(PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size());
    }

    @Test
    @GeneratePlayers(1000)
    @DisplayName("Проверить запуск с 1000 пользователей")
    public void loadTest(PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();

        assertEquals(1000, listBefore.size());
        assertThat("Не совпал размер коллекции", listBefore, hasSize(999));
    }

    @Test
    public void playerInList() {
        Player p1 = new Player(1, "N1", 1, true);
        Player p2 = new Player(2, "N2", 1, true);
        Player p3 = new Player(3, "N3", 1, true);
        Player p4 = new Player(4, "N4", 1, true);

        // DB -> select * from banlist
        List<Player> banList = List.of(p1, p2, p3, p4);

        List<Player> onlinePlayers = List.of(
                new Player(3, "N3", 1, true),
                new Player(10, "N10", 1, true)
        );

        assertThat(p1, hasToString("Player{id=1, nick='N1', points=1, isOnline=true}"));
        assertThat(100.23456, closeTo(100.2, 1));
//        assertThat(banList, hasItems(onlinePlayers.get(0), onlinePlayers.get(1)));
    }
}
