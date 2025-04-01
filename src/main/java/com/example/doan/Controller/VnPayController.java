package com.example.doan.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/payment")
public class VnPayController {

    // Thông tin cấu hình VnPay
    private final String vnp_TmnCode = "M83OD4C1"; // Terminal ID của bạn
    private final String vnp_HashSecret = "2SGFB47N8WJCSDI9BAWQ602LLGJRE4CC"; // Khóa bí mật
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String vnp_Version = "2.1.0";
    private final String vnp_Command = "pay";
    private final String vnp_ReturnUrl = "http://localhost:4200/payment-result";

    /**
     * API tạo URL thanh toán VnPay (phương thức cũ)
     */
    @PostMapping("/create")
    public ResponseEntity<String> createPayment(
            @RequestParam long amount,
            @RequestParam String orderInfo,
            HttpServletRequest request) {

        // Tạo các tham số cơ bản
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân 100 theo yêu cầu VNPAY
        
        // Sửa lỗi trong cách tạo CreateDate
        Calendar cld = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", getClientIP(request));
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        
        // Tạo mã tham chiếu giao dịch
        String vnp_TxnRef = generateTransactionRef();
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Sắp xếp tham số và tạo hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Tạo chuỗi hash
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                // Tạo chuỗi query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Tạo chuỗi hash
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        String paymentUrl = vnp_Url + "?" + query;

        return ResponseEntity.ok(paymentUrl);
    }

    /**
     * API tạo URL thanh toán VnPay (phương thức mới sử dụng RequestBody)
     */
    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(
            @RequestBody PaymentRequest paymentRequest,
            HttpServletRequest request) {
        
        // Tạo các tham số cơ bản
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(paymentRequest.getAmount() * 100)); // Nhân 100 theo yêu cầu VNPAY
        
        // Tạo CreateDate
        Calendar cld = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", getClientIP(request));
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", paymentRequest.getOrderInfo());
        vnp_Params.put("vnp_OrderType", paymentRequest.getOrderType() != null ? paymentRequest.getOrderType() : "other");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        
        // Tạo mã tham chiếu giao dịch
        String vnp_TxnRef = generateTransactionRef();
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        // Sắp xếp tham số và tạo hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Tạo chuỗi hash
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                // Tạo chuỗi query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        // Tạo chuỗi hash
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);
        String paymentUrl = vnp_Url + "?" + query;
        
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * API xử lý callback từ VnPay
     */
    @GetMapping("/payment-callback")
    public ResponseEntity<Map<String, String>> paymentCallback(
            @RequestParam Map<String, String> queryParams) {
        
        Map<String, String> response = new HashMap<>();
        
        boolean isValidSignature = validatePaymentReturn(queryParams);
        
        if (isValidSignature) {
            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            
            if ("00".equals(vnp_ResponseCode)) {
                // Thanh toán thành công
                response.put("status", "SUCCESS");
                response.put("message", "Thanh toán thành công");
                response.put("transactionId", queryParams.get("vnp_TransactionNo"));
                response.put("orderInfo", queryParams.get("vnp_OrderInfo"));
                response.put("amount", String.valueOf(Integer.parseInt(queryParams.get("vnp_Amount")) / 100));
            } else {
                // Thanh toán thất bại
                response.put("status", "FAILED");
                response.put("message", "Thanh toán thất bại");
                response.put("responseCode", vnp_ResponseCode);
            }
        } else {
            // Chữ ký không hợp lệ
            response.put("status", "INVALID_SIGNATURE");
            response.put("message", "Chữ ký không hợp lệ");
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    /**
     * Xác thực callback từ VnPay
     */
    private boolean validatePaymentReturn(Map<String, String> vnp_Params) {
        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        Map<String, String> validParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            if (!entry.getKey().equals("vnp_SecureHash") && !entry.getKey().equals("vnp_SecureHashType")) {
                validParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        // Sắp xếp tham số
        List<String> fieldNames = new ArrayList<>(validParams.keySet());
        Collections.sort(fieldNames);
        
        // Tạo chuỗi hash
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = validParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        return calculatedHash.equals(vnp_SecureHash);
    }
    
    /**
     * Tạo mã tham chiếu giao dịch
     */
    private String generateTransactionRef() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + new Random().nextInt(1000);
    }

    /**
     * Hàm tạo hash HMAC-SHA512
     */
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac.init(secretKey);
            byte[] rawHmac = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo hash", e);
        }
    }

    /**
     * Chuyển đổi byte sang chuỗi hex
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    /**
     * Lấy IP client
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

/**
 * Class đại diện cho yêu cầu thanh toán
 */
class PaymentRequest {
    private long amount;
    private String orderInfo;
    private String orderType;
    
    // Getters and Setters
    public long getAmount() {
        return amount;
    }
    
    public void setAmount(long amount) {
        this.amount = amount;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
    
    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}