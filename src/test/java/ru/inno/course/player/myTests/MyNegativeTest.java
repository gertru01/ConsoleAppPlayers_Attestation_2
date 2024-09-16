package ru.inno.course.player.myTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.myTests.annotations.GeneratePlayers;
import ru.inno.course.player.myTests.resolvers.TestDataResolver;
import ru.inno.course.player.myTests.testWatchers.MyTestWatcher;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MyTestWatcher.class, TestDataResolver.class})
public class MyNegativeTest {

    private PlayerService service;
    private static final String NICKNAME = "Nikita";

    @BeforeEach
    public void setUp() {

        service = new PlayerServiceImpl();

    }

    @AfterEach
    public void tearDown() throws IOException {

        Files.deleteIfExists(Path.of("./data.json"));

    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Удаление игрока c несуществующим id - NegTest1")
    public void deletePlayerWithNonexistentId(PlayerService service) throws IOException {

        assertThrows(NoSuchElementException.class, () -> service.deletePlayer(6), "Доступно удаление игрока по несуществующему id");

    }

    @Test
    @DisplayName("Создание игрока с занятым nickname - NegTest2")
    public void createPlayerWithDuplicateNickname() {

        service.createPlayer(NICKNAME);
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(NICKNAME));

    }

    @Test
    @DisplayName("Получение данных игрока по несуществующему id - NegTest3")
    public void getPlayerWithNonexistentId() {

        assertThrows(NoSuchElementException.class, () -> service.getPlayerById(1), "Получены данные игрока по несуществующему id");

    }

    /**
     * Т.к. создание игрока с пустым ником не ограничено, тест будет падать
     */
    @Test
    @DisplayName("Создание игрока с пустым ником - NegTest4")
    public void addPlayerWithoutNickname() {

        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(""), "Создан игрок с пустым nick"); // Ошибки нет, игрок с пустым никнеймом сохраняется
    }

    /**
     * Т.к. возможность добавления отрицательного количества баллов игроку не ограничено, тест будет падать
     */
    @Test
    @DisplayName("Добавление игроку отрицательной суммы баллов - NegTest5")
    public void addNegativeNumberPointsToPlayer() {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, -50);
        Player playerById = service.getPlayerById(playerId);
        assertEquals(0, playerById.getPoints(), "Игроку начислено отрицательное количество points"); // В случае, если отрицательные баллы не начисляются игроку

        //assertThrows(Exception.class, () -> service.addPoints(playerId, -50), "Игроку начислено отрицательное количество points"); // В случае, если при попытке начислить отрицательное количество баллов, возвращается исключение

    }

    @Test
    @DisplayName("Начисление баллов несуществующему игроку - NegTest6")
    public void addPointsToNonexistentPlayer() {

        assertThrows(NoSuchElementException.class, () -> service.addPoints(1, 50), "Игроку с несуществующим id начислены points");

    }


    /**
     * Изначально думал, что нужно перехватить исключение при чтении файла, но т.к. исключение обрабатывалось в приватном методе initStorages, то перехватить исключение через assertThrows не получалось.
     * В итоге понял, что нужно проверить поведение программы, а не детали ее реализации. При ошибке при прочтении файла создается пустая мапа players. Поэтому, вот :)
     */
    @Test
    @DisplayName("Запуск системы с некорректным json-файлом - NegTest7")
    public void startWithIncorrectJSONFile() throws IOException {

        Files.copy(Path.of("./src/test/resources/test_files/incorrect_data.json"), Path.of("./data.json"), StandardCopyOption.REPLACE_EXISTING); // Копирование подготовленного файла

        service = new PlayerServiceImpl();
        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size(), "Список игроков не пустой");

    }

    @Test
    @DisplayName("Запуск системы с json-файлом с дублирующимися записями - NegTest8")
    public void startWithJSONFileWithDuplicateData() throws IOException {

        Files.copy(Path.of("./src/test/resources/test_files/duplicate_data.json"), Path.of("./data.json"), StandardCopyOption.REPLACE_EXISTING); // Копирование подготовленного файла
        service = new PlayerServiceImpl();

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(1, listBefore.size(), "Количество игроков в списке не равно 1");

    }

    /**
     * Т.к. создание игрока с количеством символов в нике не ограничено, тест будет падать
     */
    @ParameterizedTest
    @ValueSource(strings = {"Nikita_Ivanov123", "Vladislav_Petrov_", "Vladislav_Petrov_1"})
    @DisplayName("Создание игрока с количеством символов в nick более 15 - NegTest9")
    public void addPlayerWithNicknameLengthMoreThan15Symbols(String nickname) {

        assertThrows(Exception.class, () -> service.createPlayer(nickname), "Создан игрок с ником длинной более 15 символов");

    }
}
