package com.example.qrscanner

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QrCodeAnalyzer(
    // It is a callback function that will be called when a QR code is detected.
    private val onQrCodeScanner: (String) -> Unit
): ImageAnalysis.Analyzer {

    // private list of supported image formats.
    // The list contains the three most common YUV image formats.

    private val supportImageFormat = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888,
    )
    override fun analyze(image: ImageProxy) {
        if(image.format in supportImageFormat){
            val bytes = image.planes.first().buffer.toByteArray()
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            val binaryBmp = BinaryBitmap(HybridBinarizer(source))
            try {
                val res = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBmp)
                onQrCodeScanner(res.text)
            }catch (e:Exception){
                e.printStackTrace()
            } finally {
                image.close()
            }
        }
    }

}

private fun ByteBuffer.toByteArray(): ByteArray? {
    rewind()
    return ByteArray(remaining()).also {
        get(it)
    }
}
