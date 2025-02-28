import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess

val MAPPINGS = mapOf(
    "textures/blocks" to "textures/block",
    "textures/entity/endercrystal" to "textures/entity/end_crystal",
    "textures/items" to "textures/item",
    "apple_golden.png" to "golden_apple.png",
    "totem.png" to "totem_of_undying.png"
)

val DIAMOND_TO_NETHERITE = "diamond_" to "netherite_"

fun main(
    args : Array<String>
) {
    val timestamp = System.currentTimeMillis()

    fun exit(
        message : String
    ) {
        println("$message\nUsage: <path to 1.12.2 zipped pack> <path to 1.20.1 zipped pack> <true/false: using diamond textures as netherite textures>")
        exitProcess(0)
    }

    if(args.size != 3) {
        exit("Not enough arguments!")
    }

    val inputName = args[0]
    val outputName = args[1]
    val diamondToNetherite = args[2].toBoolean()

    val inputFile = File(inputName)
    val outputFile = File(outputName)

    val inputZipFile = ZipFile(inputFile)

    if(outputFile.exists()) {
        println("Output file will be overwritten")

        outputFile.delete()
    }

    outputFile.createNewFile()

    var counter = 0

    fun mapName(
        name : String
    ) : String {
        var mappedName = name

        for((old, new) in MAPPINGS) {
            mappedName = mappedName.replace(old, new)
        }

        if(diamondToNetherite) {
            mappedName = mappedName.replace(DIAMOND_TO_NETHERITE.first, DIAMOND_TO_NETHERITE.second)
        }

        if(name != mappedName) {
            counter++
        }

        return mappedName
    }

    val zos = ZipOutputStream(FileOutputStream(outputFile))

    for(inputEntry in inputZipFile.entries()) {
        val name = inputEntry.name
        val mappedName = mapName(name)
        val `is` = inputZipFile.getInputStream(inputEntry)
        val bytes = `is`.readBytes()
        val outputEntry = ZipEntry(mappedName)

        zos.putNextEntry(outputEntry)
        zos.write(bytes)
        zos.closeEntry()
    }

    zos.close()

    println("Mapped $counter zip entries! Everything took ${System.currentTimeMillis() - timestamp} ms!")
}
