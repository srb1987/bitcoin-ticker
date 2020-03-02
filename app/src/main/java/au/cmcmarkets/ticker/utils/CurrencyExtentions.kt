package au.cmcmarkets.ticker.utils


/**
 * Rounding of a double to the given decimals.
 *
 * @param decimals
 * @return
 */
fun Double?.format(decimals: Int? = 2): String {
    this?.let {
        return "%.${decimals}f".format(this)
    }

    return "-"
}