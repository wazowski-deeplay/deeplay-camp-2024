package io.deeplay.camp.game.entites.boardGenerator;

import io.deeplay.camp.game.entites.Cell;

public interface BoardGenerator {
    public Cell[][] generateBoard(int size);
}
