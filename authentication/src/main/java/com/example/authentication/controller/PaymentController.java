package com.example.authentication.controller;

import com.example.authentication.model.Payment;
import com.example.authentication.service.interfaces.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    // Create new payment
    @PostMapping(value = "/payments")
    public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Payment payment) throws Exception {
        return ResponseEntity.ok(paymentService.createPayment(payment));
    }
    // Get all Payments by customer ID
    @GetMapping(value = "/payments/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAllPaymentByCustomerId(@PathVariable Long userId) throws Exception{
        return ResponseEntity.ok(paymentService.getAllPaymentByUserId(userId));
    }
    // Get all Payments by Product ID
    @GetMapping(value = "/payments")
    public ResponseEntity<List<Map<String, Object>>> getAllPayments() throws Exception{
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    // Get Payment By ID
    @GetMapping(value = "/payments/{paymentId}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable Long paymentId) throws Exception {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
    // Update Payment
    @PatchMapping(value = "/payments/{paymentId}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long paymentId, @RequestBody Payment payment) throws Exception {
        return ResponseEntity.ok(paymentService.updatePayment(paymentId, payment));
    }
    // Delete Payment
    @DeleteMapping(value = "/payments/{paymentId}")
    public ResponseEntity<Map<String, Boolean>> deletePayment(@PathVariable Long paymentId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", paymentService.deletePayment(paymentId));
        }});
    }
}
