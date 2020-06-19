package com.g.base.extend

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.text.Spanned
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.g.base.appContent
import com.g.base.help.tryCatch
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by G on 2017/8/9 0009.
 */
//TOAST
fun Context.toast(message: String) {
    Handler(appContent.mainLooper).post {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.toast(message: String) {
    activity?.toast(message)
}

fun Context.getScreenPoint(): Point {
    val wm = appContent.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getRealSize(point)
    return point
}
//身份证格式检查 待验证
fun Context.isIdCard(idCard: String): Boolean {
    if (idCard.isEmpty()) return false
    val regex = "[1-9]\\d{13,16}[OrderList-zA-Z0-9]{1}"
    return Pattern.matches(regex, idCard)
}


//    日期格式判断 待验证
fun Context.isDate(date: String): Boolean {
    val rexp = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))"

    val pat = Pattern.compile(rexp)

    val mat = pat.matcher(date)

    return mat.matches()
}

//    获得屏幕宽度
fun Context.getScreenWidth(context: Context): Int {
    val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return outMetrics.widthPixels
}

//获取屏幕宽度默认减去spacing=16+16+6+6dp的三分之一
fun Context.getPicHeightOrWidth(context: Context, divisor: Int = 3, spacing: Int = 44): Int {
    val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    wm.defaultDisplay.getMetrics(outMetrics)
    return ((outMetrics.widthPixels - spacing.dp()) / divisor)
}

//保留两位小数,四舍五入
fun Context.format(double: Double) = BigDecimal(double).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()

@SuppressLint("SimpleDateFormat")
//时间转换为刚刚,几分钟前
fun String.transformTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val calendar = Calendar.getInstance()
    val nowDate = calendar.time.time //Date.getTime() 获得毫秒型日期
    var timeString = ""
    tryCatch {
        val specialDate = sdf.parse(this).time
        val betweenDate = (nowDate - specialDate) / 1000
        timeString = when (betweenDate) {
            in 0 until 60 -> {
                "刚刚"
            }
            in 60 until 3600 -> {
                "${betweenDate / 60}分钟前"
            }
            in 3600 until 3600 * 10 -> {
                "${betweenDate / 3600}小时前"
            }
            else -> {
                this.substring(0, 10)
            }
        }

    }
    return timeString
}

@SuppressLint("SimpleDateFormat")
fun Context.timeStampToTime(stamp: Long): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(Date(stamp))
}

//禁止输入emoji
fun Context.inhibitingInput(editText: EditText) {
    val emojiFilter = object : InputFilter {
        val emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)

        override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            val emojiMatcher = emoji.matcher(source)
            if (emojiMatcher.find()) {
                toast("不支持输入表情")
                return ""
            }
            return null
        }
    }
    editText.filters = arrayOf(emojiFilter)
}
