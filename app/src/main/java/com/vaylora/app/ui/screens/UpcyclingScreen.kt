package com.vaylora.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vaylora.app.rag.RuleEntity

data class UpcycleProject(
    val title: String,
    val material: String,
    val difficulty: String,
    val duration: String,
    val steps: List<String>,
    val ecoImpact: String
)

@Composable
fun UpcyclingScreen(
    rules: List<RuleEntity>,
    isDarkTheme: Boolean = true
) {
    val projects = listOf(
        UpcycleProject(
            title = "Self-Watering Herb Planter",
            material = "Plastic Bottle",
            difficulty = "Easy",
            duration = "15 mins",
            steps = listOf(
                "Cut a 2-liter plastic bottle in half horizontally.",
                "Invert the top half into the bottom half like a funnel.",
                "Thread a cotton string through the bottle neck down into the water reservoir.",
                "Fill the top with potting soil and plant mint, basil, or cilantro."
            ),
            ecoImpact = "Reuses PET plastic + zero-waste kitchen gardening"
        ),
        UpcycleProject(
            title = "Rustic Fairy Light Lantern",
            material = "Glass Jar",
            difficulty = "Easy",
            duration = "10 mins",
            steps = listOf(
                "Thoroughly clean and dry a glass jam or pickle jar.",
                "Wrap natural twine around the threaded neck to form a secure hanging loop.",
                "Insert warm-white LED battery/solar fairy lights inside.",
                "Hang on a balcony, patio, or place as an eco-friendly dining centerpiece."
            ),
            ecoImpact = "100% circular reuse of glass packaging"
        ),
        UpcycleProject(
            title = "Modular Desk Drawer Organizers",
            material = "Cardboard Box / Cereal Box",
            difficulty = "Medium",
            duration = "20 mins",
            steps = listOf(
                "Measure your desk drawer height.",
                "Cut empty cereal or snack boxes down to drawer depth.",
                "Cover with reclaimed craft paper or decorative newspaper.",
                "Arrange modular compartments for pens, paperclips, cables, and flash drives."
            ),
            ecoImpact = "Diverts corrugated board and replaces plastic organizers"
        ),
        UpcycleProject(
            title = "Acoustic Smartphone Sound Booster",
            material = "Paper Towel Roll",
            difficulty = "Easy",
            duration = "10 mins",
            steps = listOf(
                "Cut a horizontal slot in the center of a sturdy cardboard tube.",
                "Attach two push pins at the base to prevent the tube from rolling.",
                "Insert your smartphone speaker into the slot for natural acoustic amplification."
            ),
            ecoImpact = "Zero-electricity passive audio amplifier"
        ),
        UpcycleProject(
            title = "Succulent Tin Can Planter",
            material = "Aluminum / Tin Can",
            difficulty = "Medium",
            duration = "25 mins",
            steps = listOf(
                "Wash food can thoroughly and sand any sharp inner rim edges smooth.",
                "Punch 3 small drainage holes in the bottom using a hammer and small nail.",
                "Paint the outside with leftover non-toxic outdoor acrylic paint.",
                "Add a layer of small gravel pebbles followed by cactus soil and a succulent cutting."
            ),
            ecoImpact = "Saves energy required to melt and re-refine metals"
        )
    )

    val bgColor = if (isDarkTheme) Color(0xFF0C1410) else Color(0xFFF6FBF4)
    val cardBg = if (isDarkTheme) Color(0xFF14241B) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1B1B1B)
    val subtitleColor = if (isDarkTheme) Color(0xFF88A090) else Color(0xFF6B7280)
    val limeAccent = Color(0xFF8BC34A)
    val tealAccent = Color(0xFF009688)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Upcycling Workshop",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor
                )
                Text(
                    text = "Turn household waste into beautiful, functional everyday items",
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }
        }

        items(projects) { project ->
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = tealAccent.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = project.material.uppercase(),
                                color = tealAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = subtitleColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = project.duration,
                                fontSize = 11.sp,
                                color = subtitleColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = project.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "HOW TO MAKE IT:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = limeAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    project.steps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = limeAccent,
                                modifier = Modifier.width(20.dp)
                            )
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = subtitleColor,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(tealAccent.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = tealAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = project.ecoImpact,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = tealAccent
                        )
                    }
                }
            }
        }
    }
}
