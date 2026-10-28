package com.vaylora.app.ml

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.vaylora.app.rag.RAGEngine
import com.vaylora.app.rag.RuleEntity
import kotlinx.coroutines.*
import java.nio.ByteBuffer

class CameraAnalyzer(
    private val wasteDetector: WasteDetector,
    private val ragEngine: RAGEngine,
    private val onResult: (material: String, rule: RuleEntity?, instruction: String) -> Unit
) : ImageAnalysis.Analyzer {

    private var isProcessing = false
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }
        isProcessing = true

        scope.launch {
            try {
                val bitmap = imageProxyToBitmap(imageProxy)
                if (bitmap != null) {
                    val result = wasteDetector.detect(bitmap)
                    if (result != null) {
                        val rule = ragEngine.findBestRule(result.normalizedMaterial)
                        val instruction = if (result.isLivingBeing) {
                            "Living beings are not waste! Please point the camera at everyday household items or packaging."
                        } else if (result.isBulkOrVehicle) {
                            rule?.instruction ?: "Bulk item detected. Do not place in standard household bins. Arrange municipal bulk pickup."
                        } else {
                            ragEngine.getDisposalInstruction(result.normalizedMaterial)
                        }
                        withContext(Dispatchers.Main) {
                            onResult(result.primaryLabel, rule, instruction)
                        }
                    }
                }
            } catch (_: Exception) {
                // Ignore transient frame errors
            } finally {
                isProcessing = false
                imageProxy.close()
            }
        }
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val bitmap = try {
            image.toBitmap()
        } catch (_: Exception) {
            val buffer: ByteBuffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } ?: return null

        val argbBitmap = if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
        return Bitmap.createBitmap(argbBitmap, 0, 0, argbBitmap.width, argbBitmap.height, matrix, true)
    }

    fun shutdown() {
        scope.cancel()
        wasteDetector.close()
    }
}
