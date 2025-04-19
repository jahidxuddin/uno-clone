package util

import androidx.compose.ui.input.pointer.PointerIcon
import java.awt.Point
import java.awt.Toolkit
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

fun loadCursorIcon(resourcePath: String): PointerIcon {
    val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(resourcePath)
        ?: error("Resource not found: $resourcePath")

    val bufferedImage: BufferedImage = ImageIO.read(resourceStream)
    val toolkit = Toolkit.getDefaultToolkit()
    val awtCursor = toolkit.createCustomCursor(bufferedImage, Point(0, 0), "customCursor")
    return PointerIcon(awtCursor)
}
