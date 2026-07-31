package com.himpact.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * QR Code Generation Service using ZXing library.
 * Generates PNG QR codes and Base64 Data-URLs for guest invitations.
 *
 * See: Sprint 3 Objectives — QR Code Generation
 */
@Slf4j
@Service
public class QrCodeService {

    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * Generate a Base64 Data-URL string for a QR code encoding the given content.
     * Output format: data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...
     */
    public String generateQrCodeBase64(String content) {
        return generateQrCodeBase64(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public String generateQrCodeBase64(String content, int width, int height) {
        try {
            byte[] imageBytes = generateQrCodePngBytes(content, width, height);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (Exception ex) {
            log.error("Failed to generate QR code for content: {}", content, ex);
            throw new RuntimeException("QR code generation failed", ex);
        }
    }

    /**
     * Generate raw PNG byte array for a QR code.
     */
    public byte[] generateQrCodePngBytes(String content, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}
