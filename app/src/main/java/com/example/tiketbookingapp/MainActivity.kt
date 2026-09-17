package com.example.tiketbookingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TicketBookingScreen()
            }
        }
    }
}


private val BgDark = Color(0xFF0B0E1A)
private val CardDark = Color(0xFF161B2C)
private val CardBorder = Color(0xFF2A3350)
private val NeonCyan = Color(0xFF22D3EE)
private val NeonPurple = Color(0xFF8B5CF6)
private val NeonPink = Color(0xFFEC4899)
private val NeonGreen = Color(0xFF34F5B0)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF8B93A7)

private val NeonGradient = Brush.horizontalGradient(listOf(NeonCyan, NeonPurple, NeonPink))

fun formatRupiah(amount: Int): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount)
}


private const val HARGA_VIP = 50_000
private const val HARGA_REGULER = 25_000
private const val MIN_TIKET = 1
private const val MAX_TIKET = 10
private const val MIN_QTY_PROMO = 3
private const val POTONGAN_PROMO = 5_000

@Composable
fun TicketBookingScreen() {
    var isVip by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(MIN_TIKET) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val pricePerTicket = if (isVip) HARGA_VIP else HARGA_REGULER
    val subtotal = pricePerTicket * quantity
    val isPromoActive = quantity >= MIN_QTY_PROMO
    val discount = if (isPromoActive) POTONGAN_PROMO else 0
    val total = (subtotal - discount).coerceAtLeast(0)

    fun resetAll() {
        isVip = false
        quantity = MIN_TIKET
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderSection()

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            CategoryAndPriceCard(
                isVip = isVip,
                pricePerTicket = pricePerTicket,
                onSelectVip = { isVip = true },
                onSelectReguler = { isVip = false }
            )

            QuantityCard(
                quantity = quantity,
                min = MIN_TIKET,
                max = MAX_TIKET,
                onDecrease = { if (quantity > MIN_TIKET) quantity-- },
                onIncrease = { if (quantity < MAX_TIKET) quantity++ }
            )

            PromoHint(isActive = isPromoActive, quantity = quantity)

            TotalCard(
                subtotal = subtotal,
                discount = discount,
                total = total,
                isPromoActive = isPromoActive
            )

            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                elevation = null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(NeonGradient, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Beli Tiket Sekarang  →", color = BgDark, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            TextButton(
                onClick = { resetAll() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("↺  Reset Pemesanan", color = TextSecondary, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Pesanan Dikonfirmasi") },
            text = {
                Column {
                    Text("Kategori: ${if (isVip) "VIP" else "Reguler"}")
                    Text("Jumlah: $quantity tiket")
                    if (isPromoActive) {
                        Text("Promo: -${formatRupiah(discount)}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Bayar: ${formatRupiah(total)}", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(colors = listOf(Color(0xFF1A1035), BgDark)),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(top = 56.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .align(Alignment.TopStart)
                .offset(x = 16.dp, y = 4.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.7f))
        )
        Box(
            modifier = Modifier
                .size(7.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-24).dp, y = 14.dp)
                .clip(CircleShape)
                .background(NeonPink.copy(alpha = 0.7f))
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .align(Alignment.BottomStart)
                .offset(x = 50.dp, y = (-4).dp)
                .clip(CircleShape)
                .background(NeonPurple.copy(alpha = 0.7f))
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pemesanan Tiket",
                style = TextStyle(
                    brush = Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple)),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.2.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Amankan tempatmu sebelum kehabisan!",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun CategoryAndPriceCard(
    isVip: Boolean,
    pricePerTicket: Int,
    onSelectVip: () -> Unit,
    onSelectReguler: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text("Kategori Tiket", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CategoryChip(
                label = "VIP",
                selected = isVip,
                accent = NeonPurple,
                onClick = onSelectVip,
                modifier = Modifier.weight(1f)
            )
            CategoryChip(
                label = "Reguler",
                selected = !isVip,
                accent = NeonCyan,
                onClick = onSelectReguler,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("Harga Tiket: ", color = TextPrimary, fontSize = 13.sp)
            Text(
                text = formatRupiah(pricePerTicket),
                color = NeonCyan,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.animateContentSize()
            )
            Text(" / tiket", color = TextSecondary, fontSize = 12.sp)
        }
        Text("Sudah termasuk pajak & biaya layanan", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) accent else CardBorder
    val bg = if (selected) accent.copy(alpha = 0.15f) else Color.Transparent
    val textColor = if (selected) TextPrimary else TextSecondary
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        if (selected) {
            Spacer(modifier = Modifier.width(4.dp))
            Text("✓", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QuantityCard(
    quantity: Int,
    min: Int,
    max: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Jumlah Tiket",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepperButton(symbol = "−", enabled = quantity > min, onClick = onDecrease)
            Text(
                text = quantity.toString(),
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .animateContentSize(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            StepperButton(symbol = "+", enabled = quantity < max, onClick = onIncrease)
        }
        if (quantity == max) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Maksimal $max tiket per pemesanan",
                color = NeonPink,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StepperButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) CardBorder else Color(0xFF1D2333)
    val fg = if (enabled) TextPrimary else Color(0xFF4A5268)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PromoHint(isActive: Boolean, quantity: Int) {
    val sisaTiket = (MIN_QTY_PROMO - quantity).coerceAtLeast(0)
    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isActive) NeonGradient
                    else Brush.horizontalGradient(listOf(CardBorder, CardBorder))
                )
                .padding(vertical = 10.dp, horizontal = 14.dp)
        ) {
            Text(
                text = if (isActive) {
                    "Promo aktif! Kamu hemat ${formatRupiah(POTONGAN_PROMO)}"
                } else {
                    "Beli $sisaTiket tiket lagi untuk dapat potongan ${formatRupiah(POTONGAN_PROMO)}"
                },
                color = if (isActive) BgDark else TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TotalCard(subtotal: Int, discount: Int, total: Int, isPromoActive: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = NeonGreen.copy(alpha = 0.25f),
                spotColor = NeonGreen.copy(alpha = 0.25f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(CardDark)
            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = TextSecondary, fontSize = 12.sp)
                Text(formatRupiah(subtotal), color = TextPrimary, fontSize = 12.sp)
            }
            if (isPromoActive) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Potongan Promo", color = NeonPink, fontSize = 12.sp)
                    Text("-${formatRupiah(discount)}", color = NeonPink, fontSize = 12.sp)
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().height(20.dp)) {
            DashedLine(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Bayar", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatRupiah(total),
                    color = NeonGreen,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.animateContentSize()
                )
                if (isPromoActive) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatRupiah(subtotal),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
    }
}

@Composable
fun DashedLine(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.height(1.dp)) {
        drawLine(
            color = CardBorder,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 4f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B0E1A)
@Composable
fun TicketBookingScreenPreview() {
    MaterialTheme {
        TicketBookingScreen()
    }
}
