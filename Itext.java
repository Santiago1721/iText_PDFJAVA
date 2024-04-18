
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.PdfMerger;
import java.io.File;
import java.io.IOException;

public class Itext {

public static void main(String[] args) throws IOException {
    String inputFilePath = "C:\\Users\\santo\\Downloads\\PDF_prueba.pdf"; // Ruta del archivo PDF de entrada
    String outputFolder = "C:\\Users\\santo\\Desktop\\pdf4"; // Carpeta de salida para los archivos divididos

    String startBookmark = "FAY2797626032024"; // Nombre del marcador de inicio
    String endBookmark = "MBI012403194319"; // Nombre del marcador de final

    try {
        splitByBookmarksRange(inputFilePath, outputFolder, startBookmark, endBookmark);
        System.out.println("Proceso completado exitosamente.");
    } catch (IOException e) {
        System.err.println("Error al dividir el archivo PDF: " + e.getMessage());
    }
}   

public static PdfOutline findBookmark(PdfDocument document, String bookmarkTitle) {
    var outlines = document.getOutlines(false);
    for (PdfOutline outline : outlines.getAllChildren()) {
        if (outline.getTitle().equals(bookmarkTitle)) {
            return outline;
        }
    }
    return null;
}

public static void splitByBookmarksRange(String inputFilePath, String outputFolder, String startBookmark, String endBookmark) throws IOException {
    
 
    PdfReader reader = new PdfReader(inputFilePath);
    try (PdfDocument document = new PdfDocument(reader)) {
        PdfOutline startOutline = findBookmark(document, startBookmark);
        PdfOutline endOutline = findBookmark(document, endBookmark);
        
        if (startOutline == null || endOutline == null) {
            throw new IllegalArgumentException("Los marcadores de inicio o final no fueron encontrados.");
        }
        
        int startPageNumber = getPageNumber(document, startOutline);
        int endPageNumber = getPageNumber(document, endOutline);
        
        if (startPageNumber > endPageNumber) {
            throw new IllegalArgumentException("El marcador de inicio no puede estar después del marcador de final.");
        }
        
        System.out.println("Dividiendo PDF desde el marcador \"" + startBookmark + "\" en la página " + startPageNumber +
                " hasta el marcador \"" + endBookmark + "\" en la página " + endPageNumber + ".");
        
        try (PdfDocument newDocument = new PdfDocument(new PdfWriter(outputFolder + "/output.pdf"))) {
            PdfMerger merger = new PdfMerger(newDocument);
            merger.merge(document, startPageNumber, endPageNumber);
        }
        
        System.out.println("PDF dividido exitosamente.");
    }
}

public static int getPageNumber(PdfDocument document, PdfOutline outline) {
    PdfObject destination = outline.getDestination().getPdfObject();
    if (destination.isDictionary()) {
        return document.getPageNumber((PdfDictionary) destination) + 1;
    } else if (destination.isArray()) {
        // Handle array destination (usually for remote destinations)
        // For simplicity, we'll just use the first element of the array
        PdfDictionary firstPage = ((PdfArray) destination).getAsDictionary(0);
        return document.getPageNumber(firstPage) + 1;
    } else {
        throw new IllegalArgumentException("Tipo de destino no compatible: " + destination.getClass().getName());
    }
}
}

