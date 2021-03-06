package frc.team1458.lib.util.flow

import edu.wpi.first.wpilibj.Timer

/**
 * Returns system time in milliseconds. Should only be used for relative measurements as the initial time may be unknown.
 */
val systemTimeMillis : Double
    get() {
        var time : Double = System.currentTimeMillis().toDouble()
        try {
            time = Timer.getFPGATimestamp() * 1000.0
        } catch (e : Exception) { }

        return time
    }

val systemTimeSeconds : Double
    get() = systemTimeMillis / 1000.0

/**
 * Delays execution for the specified number of milliseconds. Should be used very sparingly.
 */
fun delay(millis : Double) {
    try {
        Timer.delay(millis / 1000.0)
    } catch (e : Exception) {
        try {
            Thread.sleep(millis.toLong())
        } catch (e2 : InterruptedException) {

        }
    }
}

fun delay(millis : Long) {
    delay(millis.toDouble())
}