package ru.inno.course.player.myTests;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import ru.inno.course.player.model.Player;
import ru.inno.course.player.myTests.annotations.GeneratePlayers;
import ru.inno.course.player.myTests.paramsProviders.PointAndPlayersProvider;
import ru.inno.course.player.myTests.paramsProviders.PointsProvider;
import ru.inno.course.player.myTests.resolvers.TestDataResolver;
import ru.inno.course.player.myTests.testWatchers.MyTestWatcher;
import ru.inno.course.player.service.PlayerService;
import ru.inno.course.player.service.PlayerServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MyTestWatcher.class, TestDataResolver.class})
public class MyPositiveTest {

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
    @DisplayName("Добавление игрока и проверка наличия его в списке - PosTest1")
    public void addAndCheckNewPlayer() {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size()); //Проверка, что изначально список игроков пустой

        int playerId = service.createPlayer(NICKNAME); //Создание нового игрока
        Player playerById = service.getPlayerById(playerId); //Получение записи игрока из списка

        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");

    }

    @Test
    @DisplayName("Удаление нового игрока и проверка отсутствия его в списке - PosTest2")
    public void deleteAndCheckNewPlayer() {
        int playerId = service.createPlayer(NICKNAME);

        Player playerById = service.deletePlayer(playerId);

        Collection<Player> listAfter = service.getPlayers();
        assertEquals(0, listAfter.size(), "Список игроков не пустой");
    }

    @Test
    @DisplayName("Добавление игрока при отсутствии json-файла при запуске приложения - PosTest3")
    public void addNewPlayerWithoutJSONFile() throws IOException {

        Files.deleteIfExists(Path.of("./data.json")); // Явное удаление json-файла с игроками

        service = new PlayerServiceImpl(); // Создание нового класса приложения

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(0, listBefore.size(), "При запуске приложения список игроков не пустой");

        int playerId = service.createPlayer(NICKNAME);
        Player playerById = service.getPlayerById(playerId);

        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
        assertEquals(1, service.getPlayers().size(), "Количество игроков в списке не равно 1");

    }

    @ParameterizedTest
    @ArgumentsSource(PointsProvider.class)
    @DisplayName("Начисление баллов существующему игроку - PosTest5")
    public void addPointsToPlayer(int pointsToAdd, int pointsToBe) {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, pointsToAdd);

        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints(), "Количество points не соответствует значению " + pointsToBe);
    }

    @ParameterizedTest
    @ArgumentsSource(PointAndPlayersProvider.class)
    @DisplayName("Добавление баллов игроку с ненулевым количеством баллов - PosTest6")
    public void doubleAddPointsToPlayer(Player player, int pointsToAdd, int pointsToBe) {

        int playerId = service.createPlayer(player.getNick());
        service.addPoints(playerId, player.getPoints());
        service.addPoints(playerId, pointsToAdd);

        Player playerById = service.getPlayerById(playerId);
        assertEquals(pointsToBe, playerById.getPoints(), "Количество points не соответствует значению " + pointsToBe);
    }

    @Test
    @DisplayName("Получение данных созданного игрока - PosTest7")
    public void getPlayerInformation() {

        int playerId = service.createPlayer(NICKNAME);

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");

    }

    @Test
    @DisplayName("Сохранение нового игрока в файл - PosTest8")
    public void checkSavePlayerToFile() {

        int playerId = service.createPlayer(NICKNAME);
        service.addPoints(playerId, 50);

        service = new PlayerServiceImpl();

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(1, listBefore.size());

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(50, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 50. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");


    }

    @Test
    @DisplayName("Проверка уникальности id игроков - PosTest10")
    public void checkUniqueIdPlayers() {

        service.createPlayer("Player_1");
        service.createPlayer("Player_2");
        service.createPlayer("Player_3");
        service.createPlayer("Player_4");
        service.createPlayer("Player_5");

        service.deletePlayer(3);

        int playerId = service.createPlayer(NICKNAME);
        assertEquals(6, playerId, "Количество игроков в списке не равно 6");
    }

    @Test
    @DisplayName("Получение списка игроков при отсутствии json-файла при запуске приложения - PosTest11")
    public void getPlayersListWithoutJSONFile() throws IOException {

        Files.deleteIfExists(Path.of("./data.json")); // Явное удаление json-файла с игроками

        Collection<Player> list = service.getPlayers();
        assertEquals(0, list.size(), "Количество игроков в списке не равно 0");
    }

    @Test
    @DisplayName("Создание игрока с никнеймом с 15-ю символами - PosTest12")
    public void createNewPlayerWithNicknameWith15Symbols() {

        int playerId = service.createPlayer("Vladislava_1234");

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals("Vladislava_1234", playerById.getNick(), "Nick не совпадает с переданным значением");
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Проверка загрузки файла и корректности данных в нем - PosTest9")
    public void checkPlayerListFromJSONFile(PlayerService service) {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(5, listBefore.size(), "Количество игроков в списке не равно 5");
    }

    @Test
    @GeneratePlayers(5)
    @DisplayName("Добавление игрока при наличии json-файла при запуске приложения - PosTest4")
    public void addNewPlayerWithJSONFile(PlayerService service) {

        Collection<Player> listBefore = service.getPlayers();
        assertEquals(5, listBefore.size(), "Количество игроков в списке не равно 5");

        int playerId = service.createPlayer(NICKNAME);
        listBefore = service.getPlayers();
        assertEquals(6, listBefore.size(), "Количество игроков в списке не равно 6");

        Player playerById = service.getPlayerById(playerId);
        assertEquals(playerId, playerById.getId(), "Некорректный id. При создании был получен id: " + playerId + ". id в списке игроков: " + playerById.getId() + ".");
        assertEquals(0, playerById.getPoints(), "Количество points не совпадает. Ожидаемое количество points: 0. Фактическое количество points: " + playerById.getPoints() + ".");
        assertEquals(NICKNAME, playerById.getNick(), "Nick не совпадает со значением, переданным при создании игрока: " + NICKNAME);
        assertTrue(playerById.isOnline(), "Статус игрока не соответствует значению true");
    }
}
