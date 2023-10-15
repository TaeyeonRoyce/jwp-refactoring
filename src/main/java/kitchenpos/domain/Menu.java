package kitchenpos.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Embedded
    private Price price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id")
    private MenuGroup menuGroup;
    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    private List<MenuProduct> menuProducts;

    protected Menu() {
    }

    public Menu(
            final Long id,
            final String name,
            final Price price,
            final MenuGroup menuGroup,
            final List<MenuProduct> menuProducts
    ) {
        validateMenuProductsPrice(menuProducts);
        this.id = id;
        this.name = name;
        this.price = price;
        this.menuGroup = menuGroup;
        this.menuProducts = menuProducts;
    }

    public Menu(
            final String name,
            final BigDecimal price,
            final MenuGroup menuGroup
    ) {
        this(null, name, Price.from(price), menuGroup, new ArrayList<>());
    }

    private void validateMenuProductsPrice(final List<MenuProduct> menuProducts) {
        final Price totalPrice = menuProducts.stream()
                .map(MenuProduct::calculateProductsPrice)
                .reduce(Price.from(BigDecimal.ZERO), Price::add);
        if (this.price.isGreaterThan(totalPrice)) {
            throw new IllegalArgumentException();
        }
    }

    public void addMenuProduct(final MenuProduct menuProduct) {
        menuProducts.add(menuProduct);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Price getPrice() {
        return price;
    }

    public MenuGroup getMenuGroup() {
        return menuGroup;
    }

    public List<MenuProduct> getMenuProducts() {
        return menuProducts;
    }
}
