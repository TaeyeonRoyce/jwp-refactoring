package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.List;
import kitchenpos.application.dto.OrderCreationRequest;
import kitchenpos.application.dto.OrderItemsWithQuantityRequest;
import kitchenpos.application.dto.OrderStatusChangeRequest;
import kitchenpos.application.dto.result.OrderResult;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    void create_order_success() {
        // given
        final Menu menuA = generateMenu("chicken", 20000L);
        final Menu menuB = generateMenu("beer", 10000L);
        final OrderTable orderTable = generateOrderTable(3);
        final OrderCreationRequest request = new OrderCreationRequest(
                orderTable.getId(),
                List.of(
                        new OrderItemsWithQuantityRequest(menuA.getId(), 1L),
                        new OrderItemsWithQuantityRequest(menuB.getId(), 2L)
                )
        );

        // when
        final OrderResult savedOrder = orderService.create(request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(savedOrder.getId()).isNotNull();
            softly.assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
            softly.assertThat(savedOrder.getMenuResults()).hasSize(2);
        });
    }

    @Nested
    class create_order_failure {

        @Test
        void order_table_is_not_exist() {
            // given
            final Menu menuA = generateMenu("chicken", 20000L);
            final Long notExistId = 10000L;
            final OrderCreationRequest request = new OrderCreationRequest(
                    notExistId,
                    List.of(new OrderItemsWithQuantityRequest(menuA.getId(), 1L))
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order table does not exist.");
        }

        @Test
        void any_menu_in_order_doesnt_exist() {
            // given
            final Menu menuA = generateMenu("chicken", 20000L);
            final OrderTable orderTable = generateOrderTable(3);
            final OrderCreationRequest request = new OrderCreationRequest(
                    orderTable.getId(),
                    List.of(
                            new OrderItemsWithQuantityRequest(menuA.getId(), 1L),
                            new OrderItemsWithQuantityRequest(10000L, 2L)
                    )
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Menu does not exist.");
        }

        @Test
        void order_table_state_is_empty() {
            // given
            final Menu menuA = generateMenu("chicken", 20000L);
            final Menu menuB = generateMenu("beer", 10000L);
            final OrderTable orderTable = generateOrderTable(3, true);
            final OrderCreationRequest request = new OrderCreationRequest(
                    orderTable.getId(),
                    List.of(
                            new OrderItemsWithQuantityRequest(menuA.getId(), 1L),
                            new OrderItemsWithQuantityRequest(menuB.getId(), 2L)
                    )
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order from empty table is not allowed");
        }
    }

    @Test
    void list() {
        // given
        final Order order = generateOrder(OrderStatus.COOKING, generateOrderTable(3));
        order.applyOrderLineItems(List.of(
                orderLineItemRepository.save(new OrderLineItem(order, generateMenu("chicken", 20000L), 1L))
        ));

        // when
        final List<OrderResult> list = orderService.list();

        // then
        assertThat(list).hasSize(1);
        final OrderResult foundOrder = list.get(0);
        assertSoftly(softly -> {
            softly.assertThat(foundOrder.getId()).isNotNull();
            softly.assertThat(foundOrder.getOrderStatus()).isEqualTo(OrderStatus.COOKING.name());
        });
    }

    @Test
    void change_order_status_success() {
        // given
        final OrderStatusChangeRequest request = new OrderStatusChangeRequest(OrderStatus.MEAL);
        final Order existOrder = generateOrder(OrderStatus.COOKING, generateOrderTable(3));
        existOrder.applyOrderLineItems(new ArrayList<>(List.of(
                orderLineItemRepository.save(new OrderLineItem(existOrder, generateMenu("chicken", 20000L), 1L))
        )));

        // when
        final OrderResult changedOrder = orderService.changeOrderStatus(existOrder.getId(), request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(changedOrder.getId()).isEqualTo(existOrder.getId());
            softly.assertThat(changedOrder.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
        });
    }

    @Nested
    class change_order_status_failure {

        @Test
        void order_is_not_exist() {
            // given
            long notExistId = 10000L;

            // when & then
            assertThatThrownBy(() -> orderService.changeOrderStatus(notExistId, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void order_status_is_completion() {
            // given
            final OrderStatusChangeRequest request = new OrderStatusChangeRequest(OrderStatus.MEAL);
            final Order existOrder = generateOrder(OrderStatus.COMPLETION, generateOrderTable(3));

            // when & then
            assertThatThrownBy(() -> orderService.changeOrderStatus(existOrder.getId(), request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Order already completed cannot be changed");
        }
    }
}
