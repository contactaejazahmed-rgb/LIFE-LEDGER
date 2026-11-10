package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.EduGoldContainer
import com.example.ui.theme.EduGoldTertiary
import com.example.ui.theme.EduNavyContainer
import com.example.ui.theme.EduNavyPrimary
import com.example.ui.theme.EduTealContainer
import com.example.ui.theme.EduTealSecondary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenContainer
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeContainer

@Composable
fun StatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    type: StatusBadgeType = StatusBadgeType.INFO
) {
    val (bgColor, textColor) = when (type) {
        StatusBadgeType.SUCCESS -> SuccessGreenContainer to SuccessGreen
        StatusBadgeType.WARNING -> WarningOrangeContainer to WarningOrange
        StatusBadgeType.ERROR -> AlertRedContainer to AlertRed
        StatusBadgeType.GOLD -> EduGoldContainer to EduGoldTertiary
        StatusBadgeType.INFO -> EduNavyContainer to EduNavyPrimary
        StatusBadgeType.TEAL -> EduTealContainer to EduTealSecondary
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("status_badge_${text.lowercase().replace(" ", "_")}")
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

enum class StatusBadgeType {
    SUCCESS,
    WARNING,
    ERROR,
    GOLD,
    INFO,
    TEAL
}
