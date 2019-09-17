package com.devfactory.codefix.orderinformation.persistence;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.List;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderInformationRepository extends PagingAndSortingRepository<OrderInformation, Long> {

    /**
     * Retrieve and order by status and costumer, note that this method load all order issues, their repository and fix
     * from database.
     *
     * @param status the order status
     * @param customer the order customer
     * @return an instance of {@link OrderInformation} with order information if present, otherwise
     * {@link Optional#empty()}
     */
    @EntityGraph("Order.Issues.(Fix-Repository)")
    Optional<OrderInformation> findByStatusInAndCustomer(List<OrderStatus> status, Customer customer);

    /**
     * Get the give order by id, note that thirds method load issues, fix and repository nested entities and also set
     * a pessimistic write lock (other connections can not read/update row) over the order.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    OrderInformation getById(long id);

    boolean existsByStatusInAndCustomer(List<OrderStatus> status, Customer customer);
}
