package com.vaylora.app.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier.ImageClassifierOptions
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector.ObjectDetectorOptions

data class VisionInferenceResult(
    val primaryLabel: String,
    val normalizedMaterial: String,
    val confidence: Float,
    val isLivingBeing: Boolean,
    val isBulkOrVehicle: Boolean,
    val rawPredictions: List<String>
)

class WasteDetector(context: Context) {

    private val objectDetector: ObjectDetector
    private val imageClassifier: ImageClassifier

    init {
        val detectorBaseOptions = BaseOptions.builder()
            .setModelAssetPath("efficientdet_lite0.tflite")
            .build()
        val detectorOptions = ObjectDetectorOptions.builder()
            .setBaseOptions(detectorBaseOptions)
            .setScoreThreshold(0.35f)
            .setMaxResults(5)
            .setRunningMode(RunningMode.IMAGE)
            .build()
        objectDetector = ObjectDetector.createFromOptions(context, detectorOptions)

        val classifierBaseOptions = BaseOptions.builder()
            .setModelAssetPath("efficientnet_lite0_classifier.tflite")
            .build()
        val classifierOptions = ImageClassifierOptions.builder()
            .setBaseOptions(classifierBaseOptions)
            .setScoreThreshold(0.15f)
            .setMaxResults(8)
            .setRunningMode(RunningMode.IMAGE)
            .build()
        imageClassifier = ImageClassifier.createFromOptions(context, classifierOptions)
    }

    fun detect(bitmap: Bitmap): VisionInferenceResult? {
        val mpImage = BitmapImageBuilder(bitmap).build()

        // 1. Run Object Detector for spatial & safety checks
        val detections = objectDetector.detect(mpImage).detections()

        // Safety Filter: Living Entity in Object Detector
        for (detection in detections) {
            val cat = detection.categories().firstOrNull() ?: continue
            val label = cat.categoryName()
            if (cat.score() >= 0.35f && WasteTaxonomy.isLivingBeing(label)) {
                return VisionInferenceResult(
                    primaryLabel = "Person / Living Entity",
                    normalizedMaterial = "person",
                    confidence = cat.score(),
                    isLivingBeing = true,
                    isBulkOrVehicle = false,
                    rawPredictions = listOf(label)
                )
            }
        }

        // 2. Run Fine-Grained Image Classifier
        val classifications = imageClassifier.classify(mpImage)
        val classifierCategories = classifications.classificationResult()
            .classifications().firstOrNull()?.categories() ?: emptyList()

        // Safety Filter: Living Entity in Classifier
        for (cat in classifierCategories) {
            val label = cat.categoryName()
            if (cat.score() >= 0.25f && WasteTaxonomy.isLivingBeing(label)) {
                return VisionInferenceResult(
                    primaryLabel = "Person / Living Entity",
                    normalizedMaterial = "person",
                    confidence = cat.score(),
                    isLivingBeing = true,
                    isBulkOrVehicle = false,
                    rawPredictions = listOf(label)
                )
            }
        }

        // Check for Bulk Furniture or Vehicles
        val topBulk = detections.firstOrNull { d ->
            val l = d.categories().firstOrNull()?.categoryName() ?: ""
            WasteTaxonomy.isBulkFurniture(l) || WasteTaxonomy.isVehicle(l)
        }
        if (topBulk != null) {
            val cat = topBulk.categories().first()
            val label = cat.categoryName()
            val isVehicle = WasteTaxonomy.isVehicle(label)
            return VisionInferenceResult(
                primaryLabel = if (isVehicle) "Vehicle ($label)" else "Furniture ($label)",
                normalizedMaterial = if (isVehicle) "vehicle" else "furniture",
                confidence = cat.score(),
                isLivingBeing = false,
                isBulkOrVehicle = true,
                rawPredictions = listOf(label)
            )
        }

        // 3. Find the best waste / packaging item
        val allPredictions = mutableListOf<String>()
        var bestLabel = ""
        var bestConfidence = 0f

        // Check fine-grained classifier first
        for (cat in classifierCategories) {
            val raw = cat.categoryName()
            allPredictions.add(raw)
            if (cat.score() > bestConfidence && !WasteTaxonomy.isLivingBeing(raw)) {
                bestConfidence = cat.score()
                bestLabel = raw
            }
        }

        // Check object detector
        for (det in detections) {
            val cat = det.categories().firstOrNull() ?: continue
            val raw = cat.categoryName()
            allPredictions.add(raw)
            if (cat.score() > bestConfidence && !WasteTaxonomy.isLivingBeing(raw)) {
                bestConfidence = cat.score()
                bestLabel = raw
            }
        }

        if (bestLabel.isBlank()) {
            return null
        }

        val normalized = WasteTaxonomy.normalizeMaterialName(bestLabel)
        val cleanDisplayLabel = bestLabel.split(",").firstOrNull()?.replace("_", " ")?.trim() ?: bestLabel

        return VisionInferenceResult(
            primaryLabel = cleanDisplayLabel.replaceFirstChar { it.uppercase() },
            normalizedMaterial = normalized,
            confidence = bestConfidence,
            isLivingBeing = false,
            isBulkOrVehicle = false,
            rawPredictions = allPredictions
        )
    }

    fun close() {
        try {
            objectDetector.close()
            imageClassifier.close()
        } catch (_: Exception) {}
    }
}
