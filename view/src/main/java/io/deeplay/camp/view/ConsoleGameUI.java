package io.deeplay.camp.view;

import io.deeplay.camp.game.entites.Field;
import io.deeplay.camp.game.entites.Move;
import io.deeplay.camp.game.entites.Ship;
import io.deeplay.camp.game.utils.ConvertorFieldToString;

import java.util.List;

public class ConsoleGameUI implements GameUI {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001b[48;5;17m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final int MIN_FIELD = 5;
    public static final int MAX_FIELD = 20;
    public static final String STAR = ANSI_YELLOW + "★" + ANSI_RESET;
    private static final String border = "*".repeat(51);

    public void showStartPage() {
        System.out.println(border);
        System.out.println("*" + ANSI_BLUE + " ".repeat(49) + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(22) + "GALAXY" + " ".repeat(21) + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(49) + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(49) + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(15) + "Welcome to the Galaxy!" + " ".repeat(12) + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(4) + "Prepare for an adventure among the stars...  " + ANSI_RESET + "*");
        System.out.println("*" + ANSI_BLUE + " ".repeat(49) + ANSI_RESET + "*");
        System.out.println(border);
        System.out.println("Press Enter to start...");
    }

    public void showRulesGame() {
        System.out.println(border);
        System.out.println(" ".repeat(9) + "Это ваша инструкция по игре.\n");
        System.out.println("В игре можно выполнять следующие действия:\n");
        System.out.println(STAR + " Для совершения хода нажать - 1");
        System.out.println(STAR + " Для пропуска хода - 2");
        System.out.println(STAR + " Игра начинается с хода игрока с красными кораблями, игрок с синими ходит после красных");
        System.out.println(STAR + " В начале хода выберите флот для совершения хода");
        System.out.println(STAR + " Для выбора флота введите координаты флота через пробел, например, \"4 Е\"");
        System.out.println(STAR + " После выбора флота выберите действие с ним:");
        System.out.println(STAR + " 1 - атака, 2 - перемещение, 3 - разгруппировка и группировка флота, 4 - информация о флоте");
        System.out.println(STAR + " Выбери клетку из доступных для хода");
        System.out.println(STAR + " Ход переходит к другому игроку");
        System.out.println(STAR + " Захватывай планеты, уничтожай вражеские флоты и береги свои корабли.");
        System.out.println(STAR + " Удачи!");
        System.out.println(border);
    }

    public void showInfoForMove() {
        System.out.println("Выберите вариант хода\n1 - атака, 2 - перемещение, 3 - разгруппировка и группировка флота, 4 - информация о флоте");
        System.out.println("Введите число и нажмите Enter");
    }

    @Override
    public void registrationUser() {
        System.out.println("Для регистрации в игре введите свой логин и нажмите Enter");
    }

    @Override
    public void createGameSession(String username) {
        System.out.println("Игровая сессия создана для пользователя: " + username);
    }

    @Override
    public void selectShipColor() {
        System.out.println("Выберете цвет кораблей:\n");
        System.out.println(STAR + "Красный - 1");
        System.out.println(STAR + "Синий - 2");
        System.out.println(STAR + "Случайный - 3");
        System.out.println("Введите число и нажмите Enter");
    }

    @Override
    public void startGameSession(String gameId) {
        System.out.println("Создана игровая комната №" + gameId);
        System.out.println("Выбери размер игрового поля в диапазоне от " + MIN_FIELD + " до " + MAX_FIELD);
        System.out.println("Введите число и нажмите Enter");
    }

    @Override
    public void connectingPlayer(String waitingPlayerName) {
        System.out.println("К игровой сессии подключился игрок " + waitingPlayerName);
    }

    @Override
    public void gameStarted(Field field) {
        System.out.println(border);
        System.out.println("Запускается путешествие по галактике...");
        System.out.println("Игровое поле");
        renderGameField(field);
        System.out.println(border);
    }

    // переделать
    @Override
    public void getPlayerAction(Move move, String playerName) {
        System.out.println(border);
        System.out.println("Выберите действие:");
        System.out.println("Чтобы сделать ход введите - 1");
        System.out.println("Чтобы пропустить ход введите - 2");
        System.out.println(border);
    }

    @Override
    public void addCredits() {
        System.out.println("Игрокам начислены очки хода");
    }

    @Override
    public void createShips(List<Ship.ShipType> ships, String playerName) {
        System.out.println(playerName + " создал " + ships.size() + " кораблей");
        for (Ship.ShipType ship : ships) {
            System.out.println("Корабль: " + ship);
        }
    }

    @Override
    public void renderGameField(Field field) {
        System.out.println(ConvertorFieldToString.convertFieldToString(field));

    }

    @Override
    public void suggestMoveOptions(List<Move> moves) {
        int countMove = 1;
        moves.forEach(move -> System.out.println(countMove + ". " + move));
        System.out.println("Введи номер подходящего хода");
    }

    @Override
    public void gameEnded(String winner) {
        System.out.println(border);
        System.out.println(" ".repeat(4) + "Конец игры.");
        System.out.println(STAR + "Победу одержал: " + winner + "!" + STAR);
        System.out.println(border);
    }

    @Override
    public void endGameSession() {
        System.out.println("Игровая сессия завершена");
    }
}