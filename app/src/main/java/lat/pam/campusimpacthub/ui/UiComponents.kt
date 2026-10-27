package lat.pam.campusimpacthub.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

object UiComponents {

    fun Context.dp(value: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()

    fun roundedBg(
        fillColorHex: String,
        cornerRadiusDp: Int = 12,
        strokeColorHex: String? = null,
        strokeWidthPx: Int = 2,
        context: Context
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dp(cornerRadiusDp).toFloat()
            setColor(Color.parseColor(fillColorHex))
            if (strokeColorHex != null) {
                setStroke(strokeWidthPx, Color.parseColor(strokeColorHex))
            }
        }
    }

    fun gradientBg(
        colorsHex: IntArray,
        cornerRadiusDp: Int = 16,
        context: Context
    ): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            colorsHex
        ).apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.dp(cornerRadiusDp).toFloat()
        }
    }

    data class CardHolder(
        val card: MaterialCardView,
        val body: LinearLayout
    )

    fun createCard(
        context: Context,
        bgColorHex: String = "#FFFFFF",
        strokeColorHex: String = "#E2E8F0",
        cornerRadiusDp: Int = 16,
        elevationDp: Int = 2,
        paddingDp: Int = 16,
        bottomMarginDp: Int = 12
    ): CardHolder {
        val card = MaterialCardView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = context.dp(bottomMarginDp)
            }
            radius = context.dp(cornerRadiusDp).toFloat()
            cardElevation = context.dp(elevationDp).toFloat()
            setCardBackgroundColor(Color.parseColor(bgColorHex))
            strokeColor = Color.parseColor(strokeColorHex)
            strokeWidth = context.dp(1)
            isClickable = false
        }
        val body = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            val p = context.dp(paddingDp)
            setPadding(p, p, p, p)
        }
        card.addView(body)
        return CardHolder(card, body)
    }

    fun createPillBadge(
        context: Context,
        text: String,
        textColorHex: String = "#1E3A8A",
        bgColorHex: String = "#DBEAFE",
        endMarginDp: Int = 6
    ): TextView {
        return TextView(context).apply {
            this.text = text
            setTextColor(Color.parseColor(textColorHex))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
            background = roundedBg(bgColorHex, cornerRadiusDp = 999, context = context)
            setPadding(context.dp(10), context.dp(4), context.dp(10), context.dp(4))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = context.dp(endMarginDp)
            }
        }
    }

    fun createSectionTitle(
        context: Context,
        title: String,
        subtitle: String? = null,
        actionLabel: String? = null,
        onAction: (() -> Unit)? = null
    ): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(6)
                bottomMargin = context.dp(10)
            }

            val topRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val titleTv = TextView(context).apply {
                text = title
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            topRow.addView(titleTv)

            if (actionLabel != null && onAction != null) {
                val actionBtn = TextView(context).apply {
                    text = actionLabel
                    setTextColor(Color.parseColor("#464FEB"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTypeface(typeface, Typeface.BOLD)
                    background = roundedBg("#EEF2FF", 999, "#C7D2FE", 2, context)
                    setPadding(context.dp(12), context.dp(6), context.dp(12), context.dp(6))
                    setOnClickListener { onAction() }
                }
                topRow.addView(actionBtn)
            }

            addView(topRow)

            if (!subtitle.isNullOrBlank()) {
                addView(TextView(context).apply {
                    text = subtitle
                    setTextColor(Color.parseColor("#475569"))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = context.dp(2)
                    }
                })
            }
        }
    }

    fun createButton(
        context: Context,
        label: String,
        bgColorHex: String = "#464FEB",
        textColorHex: String = "#FFFFFF",
        weight: Float = 0f,
        endMarginDp: Int = 0,
        onClick: () -> Unit
    ): MaterialButton {
        return MaterialButton(context).apply {
            text = label
            isAllCaps = false
            setTextColor(Color.parseColor(textColorHex))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            backgroundTintList = ColorStateList.valueOf(Color.parseColor(bgColorHex))
            cornerRadius = context.dp(10)
            insetTop = 0
            insetBottom = 0
            minHeight = context.dp(40)
            layoutParams = if (weight > 0f) {
                LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight).apply {
                    marginEnd = context.dp(endMarginDp)
                }
            } else {
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = context.dp(endMarginDp)
                }
            }
            setOnClickListener { onClick() }
        }
    }

    fun createStatCardTile(
        context: Context,
        iconEmoji: String,
        metricValue: String,
        metricLabel: String,
        subLabel: String,
        valueColorHex: String = "#464FEB",
        badgeBgHex: String = "#EEF2FF"
    ): MaterialCardView {
        val holder = createCard(
            context = context,
            bgColorHex = "#FFFFFF",
            strokeColorHex = "#E2E8F0",
            cornerRadiusDp = 14,
            elevationDp = 1,
            paddingDp = 12,
            bottomMarginDp = 0
        )
        holder.card.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        ).apply {
            marginEnd = context.dp(4)
            marginStart = context.dp(4)
        }

        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val iconBadge = TextView(context).apply {
            text = iconEmoji
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            background = roundedBg(badgeBgHex, 10, context = context)
            setPadding(context.dp(8), context.dp(4), context.dp(8), context.dp(4))
        }
        val valueTv = TextView(context).apply {
            text = metricValue
            setTextColor(Color.parseColor(valueColorHex))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = context.dp(8)
            }
        }
        topRow.addView(iconBadge)
        topRow.addView(valueTv)
        holder.body.addView(topRow)

        holder.body.addView(TextView(context).apply {
            text = metricLabel
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(6)
            }
        })

        holder.body.addView(TextView(context).apply {
            text = subLabel
            setTextColor(Color.parseColor("#64748B"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        })

        return holder.card
    }

    data class InputFieldHolder(
        val container: LinearLayout,
        val editText: EditText
    )

    fun createInputField(
        context: Context,
        label: String,
        hint: String,
        defaultValue: String = "",
        multiline: Boolean = false,
        isPassword: Boolean = false
    ): InputFieldHolder {
        val wrapper = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = context.dp(10)
            }
        }
        val labelTv = TextView(context).apply {
            text = label
            setTextColor(Color.parseColor("#1E293B"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = context.dp(4)
            }
        }
        val editText = EditText(context).apply {
            this.hint = hint
            setText(defaultValue)
            setTextColor(Color.parseColor("#0F172A"))
            setHintTextColor(Color.parseColor("#94A3B8"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            background = roundedBg("#F8FAFC", 10, "#CBD5E1", 2, context)
            val padH = context.dp(12)
            val padV = context.dp(10)
            setPadding(padH, padV, padH, padV)
            if (isPassword) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else if (multiline) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                minLines = 3
                gravity = Gravity.TOP or Gravity.START
            } else {
                inputType = InputType.TYPE_CLASS_TEXT
            }
        }
        wrapper.addView(labelTv)
        wrapper.addView(editText)
        return InputFieldHolder(wrapper, editText)
    }

    fun createDivider(context: Context, verticalMarginDp: Int = 10): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                context.dp(1)
            ).apply {
                topMargin = context.dp(verticalMarginDp)
                bottomMargin = context.dp(verticalMarginDp)
            }
            setBackgroundColor(Color.parseColor("#E2E8F0"))
        }
    }
}
