package com.openagent.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.openagent.core.model.AgentStatus
import com.openagent.core.model.AgentType
import com.openagent.core.ui.theme.*

// ═══════════════════════════════════════════
//  状态指示器
// ═══════════════════════════════════════════

@Composable
fun StatusIndicator(status: AgentStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        AgentStatus.ONLINE -> StatusGreen
        AgentStatus.OFFLINE -> StatusGray
        AgentStatus.BUSY -> StatusOrange
        AgentStatus.ERROR -> StatusRed
    }
    val animatedColor = animateColorAsState(targetValue = color, label = "status")
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(animatedColor.value)
    )
}

// ═══════════════════════════════════════════
//  Agent 类型标签
// ═══════════════════════════════════════════

@Composable
fun AgentTypeChip(type: AgentType, modifier: Modifier = Modifier) {
    val (label, color) = when (type) {
        AgentType.OPENCLAW -> "OpenClaw" to OpenClawBlue
        AgentType.OPENHERMES -> "Hermes" to HermesPurple
    }
    SuggestionChip(
        onClick = {},
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        modifier = modifier,
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.12f),
            labelColor = color
        )
    )
}

// ═══════════════════════════════════════════
//  统计卡片
// ═══════════════════════════════════════════

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ═══════════════════════════════════════════
//  区域标题
// ═══════════════════════════════════════════

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        action?.invoke()
    }
}

// ═══════════════════════════════════════════
//  日志条目
// ═══════════════════════════════════════════

@Composable
fun LogItem(
    level: String,
    tag: String,
    message: String,
    timestamp: String,
    modifier: Modifier = Modifier
) {
    val levelColor = when (level) {
        "ERROR" -> StatusRed
        "WARN" -> StatusOrange
        "INFO" -> StatusGreen
        else -> StatusGray
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = timestamp,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = level,
            style = MaterialTheme.typography.bodySmall,
            color = levelColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = "[$tag]",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// ═══════════════════════════════════════════
//  详情行 (设置页/详情页通用)
// ═══════════════════════════════════════════

@Composable
fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
