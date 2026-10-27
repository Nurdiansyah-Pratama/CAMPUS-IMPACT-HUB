package lat.pam.campusimpacthub.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

object QrCodeGenerator {

    /**
     * Generates a crisp, deterministic 25x25 QR-Code-style Bitmap for an Event Ticket ID.
     * Includes standard 7x7 finder patterns in three corners, timing patterns, 5x5 alignment pattern,
     * and deterministic bit matrix derived from [payload].
     */
    fun generateTicketQrBitmap(
        payload: String,
        sizePx: Int = 520,
        primaryColor: Int = Color.parseColor("#1E3A8A"),
        accentColor: Int = Color.parseColor("#464FEB")
    ): Bitmap {
        val gridSize = 25
        val matrix = Array(gridSize) { BooleanArray(gridSize) }
        val reserved = Array(gridSize) { BooleanArray(gridSize) }

        fun placeFinderPattern(startRow: Int, startCol: Int) {
            for (r in -1..7) {
                for (c in -1..7) {
                    val rr = startRow + r
                    val cc = startCol + c
                    if (rr in 0 until gridSize && cc in 0 until gridSize) {
                        reserved[rr][cc] = true
                        val inOuter = r in 0..6 && c in 0..6 && (r == 0 || r == 6 || c == 0 || c == 6)
                        val inInner = r in 2..4 && c in 2..4
                        matrix[rr][cc] = inOuter || inInner
                    }
                }
            }
        }

        // 3 standard QR Finder Patterns
        placeFinderPattern(0, 0)
        placeFinderPattern(0, gridSize - 7)
        placeFinderPattern(gridSize - 7, 0)

        // Timing patterns on row 6 and col 6
        for (i in 8 until gridSize - 8) {
            reserved[6][i] = true
            matrix[6][i] = (i % 2 == 0)
            reserved[i][6] = true
            matrix[i][6] = (i % 2 == 0)
        }

        // Alignment pattern at bottom-right (row 16..20, col 16..20)
        for (r in 16..20) {
            for (c in 16..20) {
                reserved[r][c] = true
                val border = (r == 16 || r == 20 || c == 16 || c == 20)
                val center = (r == 18 && c == 18)
                matrix[r][c] = border || center
            }
        }

        // Fill remaining modules deterministically from payload hash + character bytes
        var seed = payload.hashCode().toLong() and 0xFFFFFFFFL
        if (seed == 0L) seed = 0x5A3C9E17L
        var charIdx = 0
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (!reserved[r][c]) {
                    val ch = if (payload.isNotEmpty()) payload[charIdx % payload.length].code else 65
                    charIdx++
                    seed = (seed * 1664525L + 1013904223L + ch + r * 31 + c * 17) and 0xFFFFFFFFL
                    matrix[r][c] = ((seed shr 3) and 1L) == 1L
                }
            }
        }

        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val padding = sizePx * 0.08f
        val drawableSize = sizePx - (padding * 2f)
        val cellSize = drawableSize / gridSize

        val finderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = primaryColor
            style = Paint.Style.FILL
        }
        val modulePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = accentColor
            style = Paint.Style.FILL
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        // Outer subtle frame
        canvas.drawRoundRect(
            RectF(4f, 4f, sizePx - 4f, sizePx - 4f),
            28f,
            28f,
            borderPaint
        )

        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                if (matrix[r][c]) {
                    val left = padding + c * cellSize
                    val top = padding + r * cellSize
                    val right = left + cellSize
                    val bottom = top + cellSize
                    val isFinder = (r < 7 && c < 7) || (r < 7 && c >= gridSize - 7) || (r >= gridSize - 7 && c < 7)
                    val paint = if (isFinder) finderPaint else modulePaint
                    val inset = if (isFinder) 0f else cellSize * 0.06f
                    val radius = if (isFinder) cellSize * 0.15f else cellSize * 0.25f
                    canvas.drawRoundRect(
                        RectF(left + inset, top + inset, right - inset, bottom - inset),
                        radius,
                        radius,
                        paint
                    )
                }
            }
        }

        return bitmap
    }
}
