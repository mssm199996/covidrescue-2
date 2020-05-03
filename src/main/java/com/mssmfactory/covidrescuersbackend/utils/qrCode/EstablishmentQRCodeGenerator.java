package com.mssmfactory.covidrescuersbackend.utils.qrCode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import org.springframework.stereotype.Component;

import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class EstablishmentQRCodeGenerator {

    private QRCodeWriter qrCodeWriter = new QRCodeWriter();

    public ByteArrayDataSource generateSmall(Establishment establishment) throws IOException, WriterException {
        return this.generate(establishment, 75, 75);
    }

    public ByteArrayDataSource generateSemiMedium(Establishment establishment) throws IOException, WriterException {
        return this.generate(establishment, 115, 115);
    }

    public ByteArrayDataSource generateMedium(Establishment establishment) throws IOException, WriterException {
        return this.generate(establishment, 256, 256);
    }

    public ByteArrayDataSource generateBig(Establishment establishment) throws IOException, WriterException {
        return this.generate(establishment, 512, 512);
    }

    private ByteArrayDataSource generate(Establishment establishment, int width, int height) throws WriterException, IOException {
        BitMatrix bitMatrix = this.qrCodeWriter.encode(establishment.getToken(), BarcodeFormat.QR_CODE, width, height);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", byteArrayOutputStream);

        return new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "image/png");
    }
}
