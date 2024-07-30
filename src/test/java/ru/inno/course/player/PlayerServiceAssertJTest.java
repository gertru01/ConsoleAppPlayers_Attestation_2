package ru.inno.course.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.inno.course.player.ext.GeneratePlayers;
import ru.inno.course.player.ext.TestDataResolver;
import ru.inno.course.player.matchers.PlayerMatcher;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.service.PlayerService;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.inno.course.player.matchers.PlayerMatcher.assertThat;

@ExtendWith({TestDataResolver.class})
public class PlayerServiceAssertJTest {


    @Test
    @GeneratePlayers(10)
    @DisplayName("Проверить запуск с 1000 пользователей")
    public void loadTest(PlayerService service) {
        Collection<Player> listBefore = service.getPlayers();

        assertThat(listBefore).allMatch(p -> p.isOnline());
        assertThat(Path.of("./src")).isDirectory();
    }

    @Test
    public void playerTest(){
        Player p = new Player(1,"John", 1, true);

        assertThat(p)
                .hasPoints(1)
                .hasNick("Stan");
    }

}
