package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends IntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    void create_product_success() {
        // given
        final Product product = new Product();
        final BigDecimal price = BigDecimal.valueOf(10000);
        product.setName("chicken");
        product.setPrice(price);

        // when
        final Product savedProduct = productService.create(product);

        // then
        assertSoftly(softly -> {
            softly.assertThat(savedProduct.getId()).isNotNull();
            softly.assertThat(savedProduct.getName()).isEqualTo("chicken");
        });
    }

    @Nested
    class create_product_failure {

        @Test
        void product_price_is_under_zero() {
            // given
            final Product product = new Product();
            product.setName("chicken");
            product.setPrice(BigDecimal.valueOf(-1000));

            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void product_price_is_null() {
            // given
            final Product product = new Product();
            product.setName("chicken");
            product.setPrice(null);

            // when & then
            assertThatThrownBy(() -> productService.create(product))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    void list() {
        // given
        generateProduct("chicken", 10000L);
        generateProduct("chicken-2", 10000L);

        // when
        final List<Product> products = productService.list();

        // then
        assertThat(products).hasSize(2);
    }
}
