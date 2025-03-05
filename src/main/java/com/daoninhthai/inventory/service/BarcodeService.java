package com.daoninhthai.inventory.service;

import com.daoninhthai.inventory.config.BarcodeConfig;
import com.daoninhthai.inventory.entity.Product;
import com.daoninhthai.inventory.exception.ResourceNotFoundException;
import com.daoninhthai.inventory.repository.ProductRepository;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeService {

    private final BarcodeConfig barcodeConfig;
    private final ProductRepository productRepository;

    public byte[] generateBarcode(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, barcodeConfig.getMargin());

            BarcodeFormat format = BarcodeFormat.valueOf(barcodeConfig.getFormat());
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, format, barcodeConfig.getWidth(), barcodeConfig.getHeight(), hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, barcodeConfig.getImageFormat(), outputStream);
            log.info("Generated barcode for content: {}", content);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Failed to generate barcode for content: {}", content, e);
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

    public byte[] generateBarcodeForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return generateBarcode(product.getSku());
    }

    public byte[] generateQRCode(String content) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, barcodeConfig.getMargin());
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    content, BarcodeFormat.QR_CODE, barcodeConfig.getQrSize(), barcodeConfig.getQrSize(), hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, barcodeConfig.getImageFormat(), outputStream);
            log.info("Generated QR code for content length: {}", content.length());
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    public String decodeBarcode(byte[] imageData) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            if (bufferedImage == null) {
                throw new RuntimeException("Could not read image data");
            }

            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

            Result result = new MultiFormatReader().decode(bitmap, hints);
            log.info("Decoded barcode: format={}, text={}", result.getBarcodeFormat(), result.getText());
            return result.getText();
        } catch (NotFoundException e) {
            log.warn("No barcode found in image");
            throw new RuntimeException("No barcode found in the provided image", e);
        } catch (IOException e) {
            log.error("Failed to read image for barcode decoding", e);
            throw new RuntimeException("Failed to read image", e);
        }
    }
}
