package com.example.authentication.repository;

import com.example.authentication.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    @Query(value = "SELECT t.* FROM transactions t " +
       "INNER JOIN user_payment up ON t.trans_id = up.trans_id " +
       "WHERE up.user_id = :userId", nativeQuery = true)
    List<TransactionEntity> findAllTransactionByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM TransactionEntity t " +
       "WHERE LOWER(t.transactionType) LIKE LOWER(CONCAT('%', :transactionName, '%'))")
    List<TransactionEntity> findAllTransactionByName(@Param("transactionName") String transactionName);

    // For native inserts, you must stick with native queries or use the entity manager.
    /**
                                        * Inserts a new record into the user_payment table linking a payment, user, and transaction.
                                        *
                                        * @param paymentId the ID of the payment to associate
                                        * @param userId the ID of the user to associate
                                        * @param transactionId the ID of the transaction to associate
                                        */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_payment(pay_id, user_id, trans_id) VALUES(:paymentId, :userId, :transactionId)", nativeQuery = true)
    void insertTransactionWithCustomer(@Param("paymentId") Long paymentId,
                                       @Param("userId") Long userId,
                                       @Param("transactionId") Long transactionId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_payment WHERE trans_id = :transId", nativeQuery = true)
    void deleteTransactionById(@Param("transId") Long transId);
}