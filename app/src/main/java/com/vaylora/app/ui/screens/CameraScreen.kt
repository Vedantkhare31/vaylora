package com.vaylora.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.auth.FirebaseUser
import com.vaylora.app.data.repository.UserRepository
import com.vaylora.app.domain.model.ScanRecord
import com.vaylora.app.ml.CameraAnalyzer
import com.vaylora.app.ml.WasteDetector
import com.vaylora.app.rag.RAGEngine
import com.vaylora.app.rag.RuleEntity
import com.vaylora.app.ui.components.VayloraScanningReticle
import kotlinx.coroutines.launch

@Composable
fun CameraScreen(
    ragEngine: RAGEngine,
    currentUser: FirebaseUser? = null,
    isDarkTheme: Boolean = true,
    onNavigateBack: () -> Unit,
    onNavigateToUpcycling: (RuleEntity?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val userRepository = remember { UserRepository() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Camera Selector State: Front vs Back
    var useFrontCamera by remember { mutableStateOf(false) }
    var detectedMaterial by remember { mutableStateOf<String?>(null) }
    var detectedConfidence by remember { mutableStateOf(0f) }
    var currentRule by remember { mutableStateOf<RuleEntity?>(null) }
    var disposalInstruction by remember { mutableStateOf<String?>(null) }
    var isDetectedLivingBeing by remember { mutableStateOf(false) }
    var isScanningActive by remember { mutableStateOf(true) }

    // Recreate detector when switching front/back camera to mirror appropriately
    val wasteDetector = remember(useFrontCamera) { WasteDetector(context, useFrontCamera) }

    DisposableEffect(wasteDetector) {
        onDispose {
            wasteDetector.close()
        }
    }

    // Track last saved material to avoid duplicate cloud syncs in continuous frames
    var lastSavedMaterial by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            // Camera Preview View
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val analyzer = CameraAnalyzer(
                            wasteDetector = wasteDetector,
                            ragEngine = ragEngine
                        ) { material, confidence, rule, instruction, isLivingBeing ->
                            if (isScanningActive) {
                                detectedMaterial = material
                                detectedConfidence = confidence
                                currentRule = rule
                                disposalInstruction = instruction
                                isDetectedLivingBeing = isLivingBeing

                                // Persist real scan to Firestore for current user
                                if (currentUser != null && !isLivingBeing && material != lastSavedMaterial && confidence >= 0.40f) {
                                    lastSavedMaterial = material
                                    coroutineScope.launch {
                                        val carbon = rule?.carbonSavedKg ?: 0.25f
                                        userRepository.saveScanRecord(
                                            ScanRecord(
                                                uid = currentUser.uid,
                                                materialLabel = material,
                                                normalizedMaterial = rule?.material ?: material.lowercase(),
                                                binType = rule?.binType ?: "General Bin",
                                                binColorHex = rule?.binColorHex ?: "#2196F3",
                                                instruction = instruction,
                                                carbonSavedKg = carbon,
                                                confidence = confidence
                                            )
                                        )
                                        userRepository.incrementUserStats(currentUser.uid, carbon)
                                    }
                                }
                            }
                        }

                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { it.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer) }

                        val cameraSelector = if (useFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                analysis
                            )
                        } catch (_: Exception) {
                            // Hardware binding fallback
                        }
                    }, ContextCompat.getMainExecutor(context))
                },
                modifier = Modifier.fillMaxSize()
            )

            // Scanning Reticle (Brackets Animation)
            VayloraScanningReticle(isScanning = isScanningActive)

            // Top Header Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                // Vision Status Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isScanningActive) Color(0xFF8BC34A) else Color(0xFFFF9800))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isScanningActive) {
                                if (useFrontCamera) "Front Camera • Active" else "AI Vision: Active"
                            } else "Vision: Paused",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Controls: Camera Flip + Pause
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Switch Front / Back Camera
                    IconButton(
                        onClick = {
                            useFrontCamera = !useFrontCamera
                            detectedMaterial = null
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Flip Camera",
                            tint = Color(0xFF8BC34A)
                        )
                    }

                    // Pause / Resume Scanning
                    IconButton(
                        onClick = { isScanningActive = !isScanningActive },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = if (isScanningActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Scanning",
                            tint = Color(0xFF8BC34A)
                        )
                    }
                }
            }

            // Real-Time Detection Bottom Card Overlay
            AnimatedVisibility(
                visible = detectedMaterial != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 90.dp)
            ) {
                val rule = currentRule
                val isLiving = isDetectedLivingBeing || rule?.isDisposable == false &&
                        (rule.category == "Living Being" || rule.material == "person" ||
                                detectedMaterial?.contains("Living Entity", ignoreCase = true) == true ||
                                detectedMaterial?.contains("Person", ignoreCase = true) == true)

                val binColor = try {
                    if (isLiving) Color(0xFF2E7D32)
                    else if (rule != null) Color(android.graphics.Color.parseColor(rule.binColorHex))
                    else Color(0xFF2196F3)
                } catch (_: Exception) {
                    Color(0xFF2196F3)
                }

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isLiving) Color(0xFF0F2618).copy(alpha = 0.95f) else Color(0xFF0F1B14).copy(alpha = 0.95f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = if (isLiving) "LIVING ENTITY DETECTED" else (detectedMaterial?.uppercase() ?: "ITEM DETECTED"),
                                    fontSize = if (isLiving) 18.sp else 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isLiving) Color(0xFF81C784) else Color(0xFF8BC34A)
                                )
                                Text(
                                    text = if (isLiving) "Protected Life • Non-Disposable Entity" else "On-Device Neural Classification (${(detectedConfidence * 100).toInt()}% conf)",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            if (isLiving) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = "Life Protected",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "DO NOT DISPOSE",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else if (rule != null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = binColor
                                ) {
                                    Text(
                                        text = rule.binType,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isLiving) {
                                "Living beings are people, pets, and nature — NEVER waste! Vaylora protects all life with gratitude. Point your camera at household packaging, bottles, cans, or food waste."
                            } else {
                                disposalInstruction ?: (rule?.instruction ?: "Disposal instruction loading...")
                            },
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        if (isLiving) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1B3824), RoundedCornerShape(10.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Safe",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Safe & Ethical AI: Living beings are protected and never assigned a waste bin.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        } else if (rule != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF162B1E), RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Prep",
                                    tint = Color(0xFF8BC34A),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = rule.preparationSteps,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Eco,
                                        contentDescription = "Eco",
                                        tint = Color(0xFF8BC34A),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "-${rule.carbonSavedKg} kg CO2e saved",
                                        fontSize = 11.sp,
                                        color = Color(0xFF8BC34A)
                                    )
                                }

                                if (rule.isDisposable) {
                                    Button(
                                        onClick = { onNavigateToUpcycling(rule) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688)),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AutoAwesome,
                                            contentDescription = "Upcycle",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Upcycle This", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Idle Helper Prompt
            if (detectedMaterial == null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 110.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Point camera at any waste item • Tap 🔄 to flip camera",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Permission Grant Screen
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0C1410)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        tint = Color(0xFF8BC34A),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Camera Access Required",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vaylora uses your camera to automatically detect waste materials in real-time entirely on-device.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Grant Camera Permission",
                            color = Color(0xFF0C1410),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
