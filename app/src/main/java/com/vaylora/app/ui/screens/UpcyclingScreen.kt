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
import androidx.compose.ui.graphics.Brush
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
fun UpcyclingScreen(rules: List<RuleEntity>) {
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
            ecoImpact = "Reuses PET plastic + zero waste kitchen gardening"
        ),
        UpcycleProject(
            title = "Rustic Fairy Light Lantern",
            material = "Glass Jar",
            difficulty = "Easy",
            duration = "10 mins",
            steps = listOf(
                "Thoroughly clean glass jar and remove adhesive label with warm soapy water.",
                "Drop in a string of battery-operated or solar micro LED copper wire lights.",
                "Wrap twine or jute rope around the rim for a natural hanging loop."
            ),
            ecoImpact = "Diverts heavy glass from landfills + replaces plastic decor"
        ),
        UpcycleProject(
            title = "Desk Organizer & Drawer Dividers",
            material = "Cardboard Box",
            difficulty = "Medium",
            duration = "20 mins",
            steps = listOf(
                "Measure the interior depth of your study desk drawer.",
                "Slice clean dry cardboard into slotted interlocking divider strips.",
                "Assemble grids for cables, pens, keys, and accessories."
            ),
            ecoImpact = "Replaces store-bought plastic trays"
        ),
        UpcycleProject(
            title = "Natural Plant Fertilizer Tea",
            material = "Banana & Fruit Scraps",
            difficulty = "Super Easy",
            duration = "5 mins",
            steps = listOf(
                "Place banana peels and fruit scraps into a reusable glass container.",
                "Fill with room-temperature water and cover loosely for 48 hours.",
                "Strain the liquid and water indoor plants with natural potassium nutrients."
            ),
            ecoImpact = "Zero chemical runoff + 100% circular nutrient cycle"
        ),
        UpcycleProject(
            title = "Pencil Holder & Succulent Pot",
            material = "Aluminum Can",
            difficulty = "Crafty",
            duration = "25 mins",
            steps = listOf(
                "Ensure top rim is smooth using pliers or fine sandpaper.",
                "Punch 3 small drainage holes in the bottom if using for succulents.",
                "Paint or wrap with twine to match your aesthetic."
            ),
            ecoImpact = "Infinite-cycle aluminum put to immediate daily use"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C1410))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Vaylora Reborn",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Transform waste into functional art & home essentials",
                    fontSize = 13.sp,
                    color = Color(0xFF8BC34A)
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF009688), Color(0xFF8BC34A))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color(0xFF081C10),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Upcycling Reasoning",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF081C10)
                        )
                        Text(
                            text = "Every item scanned with Vaylora Vision gets an on-device AI upcycling suggestion before disposal.",
                            fontSize = 11.sp,
                            color = Color(0xFF0F311C),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Featured DIY Upcycle Blueprints",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(projects) { project ->
            UpcycleProjectCard(project)
        }
    }
}

@Composable
fun UpcycleProjectCard(project: UpcycleProject) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14241B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = project.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF8BC34A).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = project.difficulty,
                        color = Color(0xFF8BC34A),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Time",
                    tint = Color.Gray,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = project.duration,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    Icons.Default.Inventory2,
                    contentDescription = "Material",
                    tint = Color(0xFF009688),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = project.material,
                    fontSize = 12.sp,
                    color = Color(0xFF009688),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Step-by-Step Blueprint:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.9f)
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
                        color = Color(0xFF8BC34A),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = step,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0D1B13), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Eco,
                    contentDescription = "Eco",
                    tint = Color(0xFF8BC34A),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = project.ecoImpact,
                    fontSize = 11.sp,
                    color = Color(0xFF8BC34A)
                )
            }
        }
    }
}
