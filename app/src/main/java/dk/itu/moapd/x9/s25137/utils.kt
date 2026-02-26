package dk.itu.moapd.x9.s25137

fun enumToString(enum: Enum<*>): String =
    enum.name.replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }