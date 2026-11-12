package com.example.data

data class RepairCase(
    val id: String,
    val title: String,
    val category: String, // 'Charging', 'Dead/Short', 'Display', 'Diode Values', 'Jumper', 'IC Compatibility'
    val symptom: String,
    val solutionEnglish: String,
    val solutionUrdu: String,
    val diodeModeRange: String,
    val keyICs: String,
    val isVerifiedHardwareSolution: Boolean = true
)

object MobileRepairingDatabase {
    val cases: List<RepairCase> = listOf(
        RepairCase(
            id = "rep_01",
            title = "Dead Phone - Secondary Power Rail Short Diagnosis",
            category = "Dead/Short",
            symptom = "Device draws 0.05A - 0.12A on DC Power Supply and resets, no boot logo, completely dead.",
            solutionEnglish = "1. Connect DC Power Supply at 4.0V / 3A.\n2. Check VPH_PWR / VBAT rail with thermal camera or rosin flux.\n3. Measure diode mode on coil capacitors around PMIC (PM6150 / PM660).\n4. If Buck coil shows < 0.050V in diode mode, isolate capacitor before replacing PMIC.",
            solutionUrdu = "1. DC Power Supply ko 4.0V aur 3A par set kar ke phone connect karein.\n2. Rosin flux ya thermal camera se shorted capacitor/coil check karein.\n3. Main PMIC ke aas paas coil par Diode Mode check karein (Normal value: 0.350V - 0.450V).\n4. Agar value 0.050V se kam aaye to pehle filter capacitor remove karein, phir IC replace karein.",
            diodeModeRange = "VBAT: 0.380V - 0.450V | VPH: 0.320V - 0.400V",
            keyICs = "PMI632, PM6150, PM8953, MT6357",
            isVerifiedHardwareSolution = true
        ),
        RepairCase(
            id = "rep_02",
            title = "Fake Charging / Slow Charging / No Current Flow",
            category = "Charging",
            symptom = "Battery charging icon displays on screen, but battery percentage does not increase (0.00A on USB tester).",
            solutionEnglish = "1. Measure USB D+ and D- lines in diode mode (Normal: 0.550V - 0.750V).\n2. If D+/D- OL or 0.00V, replace charging flex or SUB-board.\n3. Inspect OVP (Over-Voltage Protection) IC: input 5.0V, output must be 5.0V.\n4. If VBUS enters PMIC but no battery current, replace charging IC (BQ25890 / SMB1351).",
            solutionUrdu = "1. USB Tester par current check karein. Agar 0.00A ho to Charging Port aur D+/D- lines check karein.\n2. D+ aur D- par multimeter se Diode Mode value 0.600V aani chahiye.\n3. OVP IC check karein (In: 5V, Out: 5V). Agar output nahi hai to OVP bypass jumper lagayein.\n4. Charging IC (BQ series) replace karein agar VBUS normal ho aur battery charge na ho.",
            diodeModeRange = "D+: 0.620V | D-: 0.620V | VBUS: 0.520V",
            keyICs = "BQ25601, BQ25896, SMB1351, ETA6937",
            isVerifiedHardwareSolution = true
        ),
        RepairCase(
            id = "rep_03",
            title = "Display Light Missing / Black Screen (Graphics OK)",
            category = "Display",
            symptom = "Screen is completely black, but phone vibrates and faint image is visible under strong flashlight torch.",
            solutionEnglish = "1. Check Backlight Anode (+) and Cathode lines on FPC connector in diode mode.\n2. Normal Anode value: 0.500V - 0.600V (unpowered). When powered, must boost to 18V - 30V.\n3. Check Backlight Diode and 10uH Boost Coil for burning.\n4. Replace Backlight IC (LM36274 or 6-pin boost driver) and inspect display filter resistors.",
            solutionUrdu = "1. Torch ki roshni mein faint display nazar aaye to yeh Backlight (Light) ka masla hai.\n2. FPC Connector par LED+ (Anode) line check karein. Normal Diode value: 0.550V.\n3. Boost Coil aur Boost Diode ko multimeter se check karein (Coil open hone par 18V boost nahi banti).\n4. 6-pin Light IC ya Display PMIC (LM36274) replace karein.",
            diodeModeRange = "LED Anode (+): 0.520V | LED Cathode (-): 0.650V",
            keyICs = "LM36274, MP3302, KTD259, SM5109",
            isVerifiedHardwareSolution = true
        ),
        RepairCase(
            id = "rep_04",
            title = "Touch Screen Not Responding (Hardware Jumper Guidance)",
            category = "Display",
            symptom = "New folder/LCD screen installed but touch does not respond at all.",
            solutionEnglish = "1. Check Touch Power rail (VREG_L19_2P8 = 2.8V) and I2C lines (SDA/SCL = 1.8V).\n2. Diode value on Touch RST and INT line must be 0.400V - 0.550V.\n3. If SDA or SCL is OL (Open Loop), trace track to CPU via pull-up resistor and solder jumper.",
            solutionUrdu = "1. Touch Connector par 2.8V VDD aur 1.8V VIO voltage check karein.\n2. I2C Lines (SCL aur SDA) par diode mode mein 0.450V aana chahiye.\n3. Agar koi line Open (OL) ho to pull-up register se jumper lagayein.",
            diodeModeRange = "SDA: 0.480V | SCL: 0.480V | RST: 0.520V | INT: 0.510V",
            keyICs = "GT911, FT5436, Synaptics Touch Controller",
            isVerifiedHardwareSolution = true
        ),
        RepairCase(
            id = "rep_05",
            title = "Qualcomm / MediaTek IC Compatibility Cross-Reference",
            category = "IC Compatibility",
            symptom = "Need to find replacement power IC or charging IC from donor boards.",
            solutionEnglish = "PM6150 (Qualcomm): Compatible with Redmi Note 8 Pro, Poco X3, Samsung A51, Realme 6 Pro.\nPMI632: Compatible with Redmi 7, Redmi 8, Vivo Y12, Oppo A5s.\nMT6357CRV: Compatible with Vivo Y91, Redmi 6A, Realme C2.",
            solutionUrdu = "PM6150 Qualcomm IC aap Redmi Note 8, Poco X3, aur Samsung A51 donor board se nikal kar laga sakte hain.\nPMI632 IC Redmi 8, Vivo Y12 aur Oppo A5s mein same hoti hai.\nHar replacement se pehle stencil se reball lazmi karein.",
            diodeModeRange = "Universal BGA Reference",
            keyICs = "PM6150, PMI632, MT6357, MT6358, BQ25601",
            isVerifiedHardwareSolution = true
        )
    )
}
