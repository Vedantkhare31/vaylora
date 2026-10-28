package com.vaylora.app.ml

object WasteTaxonomy {

    // 1. Absolute Safety Filter: Living Beings & Humans
    private val LIVING_BEING_KEYWORDS = setOf(
        "person", "human", "man", "woman", "boy", "girl", "child", "baby", "face", "groom",
        "scuba_diver", "ballplayer", "swimmer", "diver", "scuba diver", "infant", "toddler",
        "dog", "cat", "bird", "kitten", "puppy", "horse", "sheep", "cow", "elephant",
        "bear", "zebra", "giraffe", "monkey", "rabbit", "deer", "duck", "pigeon", "parrot",
        "terrier", "retriever", "bulldog", "poodle", "hound", "chihuahua", "tabby", "siamese"
    )

    // 2. Non-Household Waste: Bulk Furniture, Vehicles, Architecture
    private val BULK_FURNITURE_KEYWORDS = setOf(
        "chair", "couch", "sofa", "bed", "dining table", "wardrobe", "cabinet", "desk",
        "toilet", "sink", "bathtub", "bookcase", "armchair", "studio couch"
    )

    private val VEHICLE_KEYWORDS = setOf(
        "car", "automobile", "truck", "bus", "motorcycle", "bicycle", "train", "airplane",
        "boat", "sports car", "convertible", "minivan", "jeep", "trailer"
    )

    // 3. Normalized Material Mappings
    private val MATERIAL_MAP = mapOf(
        // Plastic Bottles & Jugs
        "water_bottle" to "bottle",
        "water bottle" to "bottle",
        "pop_bottle" to "bottle",
        "pop bottle" to "bottle",
        "soda bottle" to "bottle",
        "pill_bottle" to "bottle",
        "lotion" to "bottle",
        "shampoo" to "bottle",
        "detergent" to "bottle",
        "plastic bottle" to "bottle",
        "bottle" to "bottle",
        "nipple" to "bottle",

        // Plastic Containers & Bags
        "tub" to "plastic container",
        "plastic container" to "plastic container",
        "tupperware" to "plastic container",
        "plastic_bag" to "plastic bag",
        "plastic bag" to "plastic bag",
        "shopping bag" to "plastic bag",
        "garbage bag" to "plastic bag",
        "packet" to "wrapper",
        "wrapper" to "wrapper",
        "candy wrapper" to "wrapper",
        "chip bag" to "wrapper",

        // Paper & Cardboard
        "carton" to "carton",
        "milk carton" to "carton",
        "juice box" to "carton",
        "tetra pak" to "carton",
        "box" to "box",
        "cardboard" to "box",
        "cardboard box" to "box",
        "shipping box" to "box",
        "pizza box" to "pizza box",
        "paper" to "paper",
        "newspaper" to "paper",
        "magazine" to "paper",
        "book" to "paper",
        "envelope" to "paper",
        "notebook" to "paper",
        "binder" to "paper",
        "menu" to "paper",
        "paper towel" to "napkin",
        "toilet tissue" to "napkin",
        "napkin" to "napkin",
        "tissue" to "napkin",

        // Cups
        "cup" to "cup",
        "coffee_mug" to "cup",
        "coffee mug" to "cup",
        "paper cup" to "cup",
        "disposable cup" to "cup",
        "tea cup" to "cup",

        // Metals & Cans
        "can" to "can",
        "tin_can" to "can",
        "tin can" to "can",
        "tin" to "can",
        "soda can" to "can",
        "beer_can" to "can",
        "beer can" to "can",
        "beverage can" to "can",
        "food can" to "can",
        "canned goods" to "can",
        "aerosol" to "aerosol",
        "spray can" to "aerosol",
        "aluminum foil" to "aluminum foil",
        "foil" to "aluminum foil",
        "foil tray" to "aluminum foil",

        // Glass
        "jar" to "jar",
        "glass jar" to "jar",
        "jam jar" to "jar",
        "mason jar" to "jar",
        "beer_glass" to "glass bottle",
        "wine_bottle" to "glass bottle",
        "wine bottle" to "glass bottle",
        "beer_bottle" to "glass bottle",
        "beer bottle" to "glass bottle",
        "wine glass" to "glass bottle",
        "goblet" to "glass bottle",
        "pitcher" to "jar",
        "flask" to "jar",
        "broken glass" to "broken glass",

        // Organics & Foods
        "banana" to "banana",
        "apple" to "apple",
        "orange" to "food waste",
        "lemon" to "food waste",
        "strawberry" to "food waste",
        "fruit" to "food waste",
        "vegetable" to "food waste",
        "broccoli" to "food waste",
        "carrot" to "food waste",
        "cucumber" to "food waste",
        "bell pepper" to "food waste",
        "mushroom" to "food waste",
        "pizza" to "pizza box",
        "sandwich" to "food waste",
        "hotdog" to "food waste",
        "donut" to "food waste",
        "cake" to "food waste",
        "bread" to "food waste",
        "bagel" to "food waste",
        "meat" to "food waste",
        "food waste" to "food waste",
        "food" to "food waste",

        // E-Waste & Electronics
        "cell phone" to "cell phone",
        "cellular_telephone" to "cell phone",
        "cellular telephone" to "cell phone",
        "cellphone" to "cell phone",
        "smartphone" to "cell phone",
        "mobile phone" to "cell phone",
        "laptop" to "electronics",
        "notebook computer" to "electronics",
        "mouse" to "electronics",
        "computer mouse" to "electronics",
        "keyboard" to "electronics",
        "computer keyboard" to "electronics",
        "remote" to "electronics",
        "remote_control" to "electronics",
        "remote control" to "electronics",
        "headphones" to "electronics",
        "earphones" to "electronics",
        "charger" to "electronics",
        "cable" to "electronics",
        "wire" to "electronics",
        "battery" to "battery",
        "power bank" to "battery",
        "television" to "electronics",
        "tv" to "electronics",
        "toaster" to "electronics",
        "microwave" to "electronics",
        "electric fan" to "electronics",
        "hair drier" to "electronics",
        "hair dryer" to "electronics",

        // Hazardous & Misc
        "light bulb" to "light bulb",
        "lightbulb" to "light bulb",
        "bulb" to "light bulb",
        "fluorescent" to "light bulb",
        "candle" to "clothing",
        "lighter" to "battery",
        "toothpaste" to "toothpaste tube",
        "toothpaste tube" to "toothpaste tube",
        "toothbrush" to "toothpaste tube",
        "clothing" to "clothing",
        "t-shirt" to "clothing",
        "shirt" to "clothing",
        "jean" to "clothing",
        "pants" to "clothing",
        "sweater" to "clothing",
        "sock" to "clothing",
        "shoe" to "shoes",
        "sneaker" to "shoes",
        "running shoe" to "shoes",
        "sandal" to "shoes",
        "styrofoam" to "styrofoam"
    )

    fun isLivingBeing(rawLabel: String): Boolean {
        val lower = rawLabel.lowercase().replace("_", " ")
        val words = lower.split(Regex("[\\s,]+")).filter { it.isNotBlank() }
        return LIVING_BEING_KEYWORDS.any { keyword ->
            words.contains(keyword) || lower.contains(keyword)
        }
    }

    fun isBulkFurniture(rawLabel: String): Boolean {
        val lower = rawLabel.lowercase().replace("_", " ")
        return BULK_FURNITURE_KEYWORDS.any { lower.contains(it) }
    }

    fun isVehicle(rawLabel: String): Boolean {
        val lower = rawLabel.lowercase().replace("_", " ")
        return VEHICLE_KEYWORDS.any { lower.contains(it) }
    }

    fun normalizeMaterialName(rawLabel: String): String {
        val lower = rawLabel.lowercase()
            .split(",").firstOrNull()?.trim() ?: rawLabel.lowercase().trim()
        val cleaned = lower.replace("_", " ")

        // Check dictionary directly
        val direct = MATERIAL_MAP[cleaned] ?: MATERIAL_MAP[lower]
        if (direct != null) return direct

        // Substring matching
        for ((key, target) in MATERIAL_MAP) {
            if (cleaned.contains(key) || key.contains(cleaned)) {
                return target
            }
        }

        // Return formatted clean label
        return cleaned
    }
}
