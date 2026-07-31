package com.dugx.event.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Sinh anh ma QR tu mot chuoi bat ky (o day la ma ve).
 */
@Service
public class QrCodeService {

    /** Kich thuoc mac dinh cua anh QR, tinh bang pixel. */
    public static final int DEFAULT_SIZE = 320;

    /** Vien trang quanh ma QR, tinh theo so o. Can co de may quet doc duoc. */
    private static final int QUIET_ZONE = 1;

    /**
     * Sinh anh QR dang PNG.
     *
     * @param content noi dung ma hoa vao QR.
     * @param size chieu rong va chieu cao anh, tinh bang pixel.
     * @return mang byte cua anh PNG.
     */
    public byte[] generatePng(String content, int size) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("QR content must not be empty");
        }

        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        // Muc sua loi M: van doc duoc khi anh bi mo hoac che mot phan nho.
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, QUIET_ZONE);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);

            MatrixToImageWriter.writeToStream(matrix, "PNG", output);

            return output.toByteArray();
        } catch (WriterException | IOException e) {
            throw new IllegalStateException("Cannot generate QR code", e);
        }
    }

    /**
     * Sinh anh QR duoi dang data URI, dung truc tiep cho thuoc tinh src cua the img.
     *
     * @param content noi dung ma hoa vao QR.
     * @return chuoi dang {@code data:image/png;base64,...}
     */
    public String generatePngDataUri(String content) {
        byte[] png = generatePng(content, DEFAULT_SIZE);

        return "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(png);
    }
}
