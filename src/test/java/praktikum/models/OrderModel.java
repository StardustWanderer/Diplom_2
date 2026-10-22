package praktikum.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/*
 * Модель тела запроса для создания заказа.
 *
 * Сервер ожидает JSON следующего вида:
 *
 * {
 *     "ingredients": [
 *         "id первого ингредиента",
 *         "id второго ингредиента"
 *     ]
 * }
 *
 * REST Assured преобразует объект OrderModel в JSON
 * при отправке HTTP-запроса.
 */
@Setter
@Getter
@NoArgsConstructor
public class OrderModel {

    /*
     * Список идентификаторов ингредиентов, которые должны
     * войти в заказ.
     *
     * List<String> означает, что у нас есть список строк,
     * а каждая строка содержит ID одного ингредиента.
     *
     * Если список пустой, будет отправлено:
     * {
     *     "ingredients": []
     * }
     */
    private List<String> ingredients;

    /*
     * Основной конструктор.
     *
     * Позволяет сразу передать список ингредиентов:
     *
     * new OrderModel(Arrays.asList(id1, id2));
     */
    public OrderModel(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}