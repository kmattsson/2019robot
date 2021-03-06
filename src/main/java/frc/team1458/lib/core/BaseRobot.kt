package frc.team1458.lib.core

import edu.wpi.first.wpilibj.SampleRobot
import frc.team1458.lib.util.DataLogger
import frc.team1458.lib.util.TelemetryLogger
import frc.team1458.lib.util.flow.WaitGroup
import frc.team1458.lib.util.flow.delay
import frc.team1458.lib.util.flow.go
import frc.team1458.lib.util.flow.systemTimeMillis
import java.util.*

/*
 * All robot classes should extend from this class.
 *
 * @author asinghani
 */

// TODO Change SampleRobot to non-deprecated replacement
abstract class BaseRobot : SampleRobot, AutoModeHolder {

    override val autoModes = ArrayList<AutoMode>()
        get

    override var selectedAutoModeIndex = 0

    constructor() : super()

    /**
     * Initialize the robot's hardware and basic configuration.
     */
    abstract fun robotSetup()

    /**
     * This method should be overridden and add autonomous modes (which extend AutoMode) using the method addAutoMode()
     */
    abstract fun setupAutoModes()

    /**
     * Initialize any robot configuration which is time-consuming and must be run on a separate thread.
     * This is not guaranteed to finish before the robot is enabled
     */
    abstract fun threadedSetup()

    /**
     * Setup for teleop (if necessary)
     */
    abstract fun teleopInit()

    /**
     * Called constantly during teleop period
     */
    abstract fun teleopPeriodic()

    /**
     * Run the robot's test mode.
     */
    abstract fun runTest()

    /**
     * Called when the robot is disabled.
     */
    abstract fun robotDisabled()

    abstract fun disabledPeriodic()

    open fun selectAutoMode() { }

    fun addAutoMode(autoMode: AutoMode) {
        autoModes.add(autoMode)
    }

    override fun robotInit() {
        robotSetup()
        setupAutoModes()
        go { threadedSetup() }

        if (autoModes.isEmpty()) {
            autoModes.add(BlankAutoMode())
        }
    }

    override fun disabled() {
        robotDisabled()
        while(isDisabled) {
            disabledPeriodic()
        }
    }

    override fun autonomous() {
        selectAutoMode()
        val autoMode = autoModes[selectedAutoModeIndex]
        autoMode?.auto()
    }

    override fun operatorControl() {
        teleopInit()
        while (super.isOperatorControl() && super.isEnabled()) {
            var lastStartMillis = systemTimeMillis
            TelemetryLogger.startIteration()
            teleopPeriodic()
            TelemetryLogger.endIteration()
            var lastEndMillis = systemTimeMillis

            var nextStartMillis : Double = lastStartMillis

            while(nextStartMillis < lastEndMillis || (nextStartMillis.toLong() % 20) != 0L) {
                nextStartMillis += 1
            }

            while(systemTimeMillis < nextStartMillis) {
                delay(1)
            }
        }
    }

    override fun test() {
        runTest()
    }
}