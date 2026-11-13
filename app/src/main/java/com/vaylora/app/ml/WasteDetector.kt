package com.vaylora.app.ml

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
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

class WasteDetector(context: Context, private val useFrontCamera: Boolean = false) {

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

    /**
     * Pre-processes the bitmap to account for front camera mirroring.
     * Front camera frames are horizontally flipped relative to the scene;
     * we mirror them back so the classifier/detector sees a canonical image.
     */
    private fun preprocessBitmap(bitmap: Bitmap): Bitmap {
        if (!useFrontCamera) return bitmap
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun detect(bitmap: Bitmap): VisionInferenceResult? {
        val processedBitmap = preprocessBitmap(bitmap)
        val mpImage = BitmapImageBuilder(processedBitmap).build()

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

        // 3. Check for Bulk Furniture or Vehicles
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

        // 4. Find the best waste / packaging item using ONLY the ImageClassifier.
        //    The COCO ObjectDetector (80 classes) is intentionally excluded here —
        //    it confuses TV/laptop/oven/microwave (all within indices 72-82 of COCO).
        //    The EfficientNet-Lite0 ImageClassifier (1,000 ImageNet classes) is far
        //    more accurate for real-world object labeling.
        val allPredictions = mutableListOf<String>()

        // Collect classifier predictions only
        for (cat in classifierCategories) {
            allPredictions.add(cat.categoryName())
        }
        // Also record detector labels for debug, but do NOT use their scores for ranking
        for (det in detections) {
            det.categories().firstOrNull()?.categoryName()?.let { allPredictions.add("det:$it") }
        }

        // Pick the top classifier result that is NOT a living being
        val bestCat = classifierCategories.firstOrNull { cat ->
            !WasteTaxonomy.isLivingBeing(cat.categoryName())
        } ?: return null

        val bestLabel = bestCat.categoryName()
        val bestConfidence = bestCat.score()
        val normalized = WasteTaxonomy.normalizeMaterialName(bestLabel)

        // Clean up the ImageNet label: "laptop, laptop computer" → "Laptop"
        val cleanDisplayLabel = bestLabel.split(",").firstOrNull()
            ?.replace("_", " ")?.trim()?.replaceFirstChar { it.uppercase() } ?: bestLabel

        return VisionInferenceResult(
            primaryLabel = cleanDisplayLabel,
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
