package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class OrderTest {

    @Test
    void throw_when_order_table_status_is_empty() {
        // given
        final OrderTable emptyTable = new OrderTable(null, 0, true);

        // when & then
        assertThatThrownBy(() -> new Order(emptyTable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order from empty table is not allowed");
    }

    @Test
    void throw_when_try_to_change_completed_order_status() {
        // given
        final OrderTable orderTable = new OrderTable(null, 0, false);
        final Order order = new Order(null, orderTable, OrderStatus.COMPLETION, LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> order.changeOrderStatus(OrderStatus.COOKING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Order already completed cannot be changed");
    }

    @Test
    void change_order_status() {
        // given
        final OrderTable orderTable = new OrderTable(null, 0, false);
        final Order order = new Order(null, orderTable, OrderStatus.COOKING, LocalDateTime.now());

        // when
        order.changeOrderStatus(OrderStatus.MEAL);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.MEAL);
    }
}
