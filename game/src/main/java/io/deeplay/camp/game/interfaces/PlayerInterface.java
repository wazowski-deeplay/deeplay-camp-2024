package io.deeplay.camp.game.interfaces;

import io.deeplay.camp.game.entites.Answer;
import io.deeplay.camp.game.entites.Field;

/**
 * Интерфейс для представления игрока, расширяет интерфейс игровых событий…
 */
public interface PlayerInterface extends GalaxyListener {
    /**
     * Получение ответа от игрока.
     *
     * @param field поле, с которым будут происходить преобразования
     * @return ответ игрока
     */
    Answer getAnswer(final Field field);
}
