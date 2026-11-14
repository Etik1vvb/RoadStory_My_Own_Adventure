package com.example.app.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.app.R

/** Asta Sans variable font. Use the user's own licensed font file at res/font/asta_sans.ttf. */
val AstaSans = FontFamily(
    Font(R.font.asta_sans, weight = FontWeight.W300),
    Font(R.font.asta_sans, weight = FontWeight.W400),
    Font(R.font.asta_sans, weight = FontWeight.W500),
    Font(R.font.asta_sans, weight = FontWeight.W600),
    Font(R.font.asta_sans, weight = FontWeight.W700),
    Font(R.font.asta_sans, weight = FontWeight.W800)
)
