package com.floatpet.floatapp.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BaseExpandedFloat(close: () -> Unit) {
  val context = LocalContext.current
  val logs = remember { mutableStateListOf<WalletButtonLog>() }
  val isServiceEnabled = remember { mutableStateOf(false) }

  // Register broadcast receiver for wallet button detection
  DisposableEffect(context) {
    val receiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.floatpet.WALLET_BUTTON_DETECTED") {
          val message = intent.getStringExtra("message") ?: ""
          val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
          logs.add(0, WalletButtonLog(message, timestamp))

          // Keep only last 50 logs
          if (logs.size > 50) {
            logs.removeAt(logs.size - 1)
          }
        }
      }
    }

    val filter = IntentFilter("com.floatpet.WALLET_BUTTON_DETECTED")
    context.registerReceiver(receiver, filter)

    onDispose {
      context.unregisterReceiver(receiver)
    }
  }

  Card(
    modifier = Modifier
      .heightIn(300.dp, 600.dp)
      .widthIn(250.dp, 450.dp),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header with close button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "🔍 Wallet Button Detector",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )

        Row {
          IconButton(
            onClick = { logs.clear() },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Refresh,
              contentDescription = "Clear logs",
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = { close() },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.Close,
              contentDescription = "Close",
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        thickness = 1.dp
      )

      // Status indicator
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          "Service Status:",
          style = MaterialTheme.typography.bodyMedium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .fillMaxWidth()
          ) {
            // Status indicator color would be set based on service state
          }
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            if (isServiceEnabled.value) "Active" else "Inactive",
            style = MaterialTheme.typography.bodySmall,
            color = if (isServiceEnabled.value) Color.Green else Color.Red
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Logs count
      Text(
        "Detected Buttons: ${logs.size}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Logs List
      LazyColumn(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (logs.isEmpty()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
              )
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  "No wallet buttons detected yet",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  "Open a browser and navigate to a DeFi site",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        } else {
          items(logs) { log ->
            WalletButtonLogItem(log)
          }
        }
      }
    }
  }
}

data class WalletButtonLog(
  val message: String,
  val timestamp: String
)

@Composable
fun WalletButtonLogItem(log: WalletButtonLog) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer
    )
  ) {
    Column(
      modifier = Modifier.padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          "Button Detected",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          fontWeight = FontWeight.Bold
        )
        Text(
          log.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = log.message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryContainer
      )
    }
  }
}