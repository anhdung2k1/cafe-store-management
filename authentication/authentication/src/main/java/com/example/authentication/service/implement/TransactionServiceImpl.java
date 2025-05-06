package com.example.authentication.service.implement;

import com.example.authentication.entity.OrderEntity;
import com.example.authentication.entity.PaymentEntity;
import com.example.authentication.entity.TransactionEntity;
import com.example.authentication.model.Transactions;
import com.example.authentication.repository.OrderRepository;
import com.example.authentication.repository.PaymentRepository;
import com.example.authentication.repository.TransactionRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private Map<String, Object> transactionMap(TransactionEntity transactionEntity) {
        return new HashMap<>() {{
            put("transactionID", transactionEntity.getTransactionId());
            put("transactionType", transactionEntity.getTransactionType());
            put("shippingAddress", transactionEntity.getShippingAddress());
            put("billingPayment", transactionEntity.getBillingPayment());
            put("paymentMethod", transactionEntity.getPayment().getPaymentMethod());
        }};
    }

    @Override
    public Boolean createTransaction(Transactions transactions, Long userId) throws Exception {
        try {
            PaymentEntity paymentEntity = paymentRepository
                    .findPaymentEntitiesByPaymentMethod(transactions.getPayment().getPaymentMethod())
                    .orElseThrow(() -> new NoSuchElementException("Payment method not found"));

            TransactionEntity transactionEntity = new TransactionEntity();
            BeanUtils.copyProperties(transactions, transactionEntity);
            transactionEntity.setPayment(paymentEntity);
            transactionEntity.setCreateAt(LocalDateTime.now());
            transactionEntity.setUpdateAt(LocalDateTime.now());
            transactionRepository.save(transactionEntity);

            Long transactionId = transactionEntity.getTransactionId();
            transactionRepository.insertTransactionWithCustomer(paymentEntity.getPaymentId(), userId, transactionId);

            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setTransaction(transactionEntity);
            orderEntity.setOrderStatus("Success");
            orderEntity.setOrderDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            orderEntity.setTotalAmount(transactionEntity.getBillingPayment());
            orderEntity.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId)));
            orderRepository.save(orderEntity);

            return true;
        } catch (Exception e) {
            throw new Exception("Could not create new transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionsByName(String transactionName) throws Exception {
        try {
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByName(transactionName);
            List<Map<String, Object>> result = new ArrayList<>();
            transactionEntities.forEach(entity -> result.add(transactionMap(entity)));
            return result;
        } catch (Exception e) {
            throw new Exception("Could not retrieve transactions by name: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllTransactionByUserId(Long userId) throws Exception {
        try {
            List<TransactionEntity> transactionEntities = transactionRepository.findAllTransactionByUserId(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            transactionEntities.forEach(entity -> result.add(transactionMap(entity)));
            return result;
        } catch (Exception e) {
            throw new Exception("Could not retrieve transactions by user ID: " + userId + ", " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getTransactionByTransactionId(Long transactionId) throws Exception {
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchElementException("Transaction not found with ID: " + transactionId));
        return transactionMap(transactionEntity);
    }

    @Override
    public Transactions updateTransaction(Long transactionId, Transactions transactions) throws Exception {
        try {
            TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new NoSuchElementException("Transaction not found with ID: " + transactionId));

            transactionEntity.setTransactionType(transactions.getTransactionType());
            transactionEntity.setShippingAddress(transactions.getShippingAddress());
            transactionEntity.setBillingPayment(transactions.getBillingPayment());
            transactionEntity.setUpdateAt(LocalDateTime.now());

            transactionRepository.save(transactionEntity);
            return transactions;
        } catch (Exception e) {
            throw new Exception("Could not update transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public Boolean deleteTransaction(Long transactionId) throws Exception {
        try {
            TransactionEntity transactionEntity = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new NoSuchElementException("Transaction not found with ID: " + transactionId));
            transactionRepository.delete(transactionEntity);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not delete transaction: " + e.getMessage(), e);
        }
    }
}