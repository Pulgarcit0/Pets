package edu.itvo.pets.core.utils
import edu.itvo.pets.core.utils.LicenseUtils.getHardwareId
import java.net.NetworkInterface
import java.security.MessageDigest
import java.io.File

object LicenseUtils {
    fun getHardwareId(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            val sb = StringBuilder()
            interfaces.toList().forEach { networkInterface ->
                val hardwareAddress = networkInterface.hardwareAddress
                if (hardwareAddress != null) {
                    sb.append(hardwareAddress.joinToString("") { "%02X".format(it) })
                }
            }
            sb.toString()
        } catch (e: Exception) {
            ""
        }
    }
}
fun generateLicenseKey(hardwareId: String): String {
    return try {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(hardwareId.toByteArray(Charsets.UTF_8))
        hash.joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        ""
    }
}

fun isLicenseValid(): Boolean {
    val hardwareId = getHardwareId()
    val expectedKey = generateLicenseKey(hardwareId)

    val licenseFile = File("license.key")
    if (!licenseFile.exists()) return false

    val storedKey = licenseFile.readText().trim()
    return storedKey == expectedKey
}

