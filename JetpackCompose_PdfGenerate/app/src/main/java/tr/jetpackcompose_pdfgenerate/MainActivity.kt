package tr.jetpackcompose_pdfgenerate

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.*
import tr.jetpackcompose_pdfgenerate.ui.theme.JetpackCompose_PdfGenerateTheme
import tr.jetpackcompose_pdfgenerate.ui.theme.turquaseColor
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = turquaseColor,
                            title = {
                                Text(
                                    text = "MERHABA",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            }
                        )
                    }
                ) {
                    var characters = ArrayList<String>()
                    characters.add("Darth Vader")
                    characters.add("Luke Skywalker")
                    characters.add("Leia Organa")
                    characters.add("Obi-Wan Kenobi")
                    characters.add("Chewbacca")
                    characters.add("Han Solo")
                    characters.add("Yoda")


                    GeneratePdfFile(
                        LocalContext.current,
                        1120,
                        792,
                        "STARWARS CHARACTERS",
                        characters,
                        "starwars"
                    )
                }

            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun GeneratePdfFile(
        context: Context,
        pageWidth: Int,
        pageHeight: Int,
        documentTitle: String,
        characters: ArrayList<String>,
        documentName: String
    ) {

        val externalStoragePermissionsState = rememberMultiplePermissionsState(
            permissions = listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        var detectPermissions:Boolean = false
        externalStoragePermissionsState.permissions.forEach { permis ->
            when(permis.status)
            {
                is PermissionStatus.Denied -> {
                    detectPermissions=true
                    permis.launchPermissionRequest() //izin iste
                    Toast.makeText(context,permis.permission.toString()+" Permission Denied. Go To App settings for enabling",Toast.LENGTH_LONG).show()
                }
            }
        }

        if(detectPermissions){
            Toast.makeText(context,"Must be open permissions on app settings !",Toast.LENGTH_LONG).show()
        }else{
            var pdfDoc: PdfDocument = PdfDocument()
            //buradaki PdfDocument sınıfı Pdf dökümanı oluşturan sınıfımızdır.

            var paint: Paint = Paint()
            var title: Paint = Paint()

            var logoBitmap: Bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.starwars
            )
            var scaledBitmap: Bitmap = Bitmap.createScaledBitmap(logoBitmap, 300, 181, false)
            //scaledBitmap, logoBitmap'in ölçülendirilmiş halini yakalamak için üretildi.

            var pdfPageInfo: PdfDocument.PageInfo? =
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            //burada tek sayfadan oluşan bir pdf doküman oluşturduğumuzu belirtiyoruz.

            var ourPage: PdfDocument.Page = pdfDoc.startPage(pdfPageInfo)
            //başlangıç sayfasını temsil ediyoruz.

            var canvas: Canvas = ourPage.canvas
            //canvas oluşturduğumuz sayfanın çerçevesidir.
            // Bu çerçeve sayesinde içerisine resim, yazı gibi
            // görsel öğelerin sayfaya eklenebilmesini sağlıyoruz.

            canvas.drawBitmap(scaledBitmap, 56F, 40F, paint)
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            title.textSize = 45F
            title.setColor(ContextCompat.getColor(context, R.color.purple_700))
            var y = 130F
            characters.forEach{
                canvas.drawText(it, 450F, y, title)
                y += 50F
            }
            canvas.drawText(documentTitle, 450F, 80F, title)
            pdfDoc.finishPage(ourPage)

            var createPdfFile: File = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath, documentName+".pdf")
            try {
                pdfDoc.writeTo(FileOutputStream(createPdfFile))
                Toast.makeText(context, "File downloaded", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "An error expected", Toast.LENGTH_LONG).show()
            }
            pdfDoc.close()
        }
    }


}
