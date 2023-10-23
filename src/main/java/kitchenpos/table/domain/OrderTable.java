package kitchenpos.table.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class OrderTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_group_id", nullable = true)
    private TableGroup tableGroup;
    private int numberOfGuests;
    private boolean empty;

    protected OrderTable() {
    }

    public OrderTable(
            final Long id,
            final TableGroup tableGroup,
            final int numberOfGuests,
            final boolean empty
    ) {
        this.id = id;
        this.tableGroup = tableGroup;
        this.numberOfGuests = numberOfGuests;
        this.empty = empty;
    }

    public OrderTable(
            final TableGroup tableGroup,
            final int numberOfGuests,
            final boolean empty
    ) {
        this(null, tableGroup, numberOfGuests, empty);
    }

    public OrderTable(
            final int numberOfGuests,
            final boolean empty
    ) {
        this(null, null, numberOfGuests, empty);
    }

    public boolean isAbleToGroup() {
        return this.empty && Objects.isNull(this.tableGroup);
    }

    public void groupByTableGroup(final TableGroup tableGroup) {
        this.tableGroup = tableGroup;
        this.empty = false;
    }

    public void ungroup() {
        this.tableGroup = null;
        this.empty = false;
    }

    public void changeEmpty(final boolean empty, final OrderTableValidator orderTableValidator) {
        orderTableValidator.validate(this);
        this.empty = empty;
    }

    public void changeNumberOfGuests(final int numberOfGuests, final TableChangeNumberOfGuestValidator tableValidator) {
        tableValidator.validate(numberOfGuests, this);
        this.numberOfGuests = numberOfGuests;
    }

    public Long getId() {
        return id;
    }

    public TableGroup getTableGroup() {
        return tableGroup;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public boolean isEmpty() {
        return empty;
    }
}
