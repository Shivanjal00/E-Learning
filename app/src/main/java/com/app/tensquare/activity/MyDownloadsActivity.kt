package com.app.tensquare.activity

import android.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tensquare.adapter.MyDownloadsAdapter
import com.app.tensquare.databinding.ActivityMyDownloadsBinding
import com.app.tensquare.databinding.DesignDeleteFileDialogBinding
import com.app.tensquare.databinding.EnrolmentDialogBinding
import com.app.tensquare.ui.pdf.PdfViewerActivity
import com.app.tensquare.utils.AppUtills
import com.app.tensquare.utils.PDF_VIA_FILE
import java.io.*
import java.util.*


class MyDownloadsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyDownloadsBinding
    private lateinit var myDownloadsAdapter: MyDownloadsAdapter
    private var downloadsList = mutableListOf<String>()
    private lateinit var context: Context
    private lateinit var bundle: Bundle
    private lateinit var downloadType: String
    var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtills.setLan(this)
        binding = ActivityMyDownloadsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@MyDownloadsActivity
        //val file = File(context.filesDir, "TestPDFfile.pdf")
        bundle = intent.extras!!
        downloadType = bundle.getString("downloadType").toString()

        AppUtills.disableSsAndRecording(this@MyDownloadsActivity)

        binding.txtTitle.text = when (downloadType) {
            "NOTES_RESOURCES" -> getString(com.app.tensquare.R.string.notes_resources)
            "MODEL_PAPER" -> getString(com.app.tensquare.R.string.model_papers)
            "QUESTION_BANK_PAPER" -> getString(com.app.tensquare.R.string.question_bank_2023)
            else -> getString(com.app.tensquare.R.string.previous_year_paper)
        }

        val currentDir = filesDir


        /*val f = File("/data/user/0/com.app.elearning/app_model")
        val files: Array<File> = f.listFiles()!!
        for (inFile in files) {

            Log.e("currentDir***", inFile.absolutePath)

        }*/

        val filesList = ArrayList<String>()
        currentDir.list()?.let { filesList.addAll(it) }

        filesList.forEach {
            if (it.uppercase().endsWith("PDF"))
                if (it.uppercase().startsWith(downloadType))
                    downloadsList.add(it)
            Log.e("Files*****", it)
        }

        /*var files: Array<String> = context.fileList()
        if (files.isNotEmpty())
            binding.txtTitle.text = files[0]*/

        /*binding.txtTitle.text =
            context.openFileInput("TestPDFfile.pdf").bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some\n$text"
                }
            }*/

        /*val fos: FileOutputStream
        try {
            fos = openFileOutput(fileName, Context.MODE_PRIVATE)
            fos.write(content.getBytes())
            fos.close()
            Toast.makeText(
                this@AndroidInternalStorageActivity, fileName.toString() + " saved",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }*/

        binding.apply {
            rvDownloads.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                myDownloadsAdapter =
                    MyDownloadsAdapter("${downloadType}_")
                    { list: MutableList<String>, str: String, position: Int, flag: Int ->
                        if (flag == 1) {
                            val file = File(filesDir.absolutePath, str)
                            startActivity(
                                Intent(
                                    this@MyDownloadsActivity,
                                    PdfViewerActivity::class.java
                                ).also {
                                    it.putExtra("file", file.absolutePath)
                                    it.putExtra("VIA", PDF_VIA_FILE)
                                })
                            /*val sb = StringBuilder()
                            val file = File(filesDir.absolutePath, str)
                            try {
                                val br = BufferedReader(FileReader(file))
                                var line = ""
                                while (br.readLine() != null) {
                                    line = br.readLine()
                                    sb.append(line)
                                    sb.append("\n")
                                }
                                txtTitle.text = sb
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }*/

                            /* val intentShareFile = Intent(Intent.ACTION_VIEW)
                             if (mStmtFile.exists()) {
                                 intentShareFile.type = "application/pdf"
                                 val fileUri: Uri = FileProvider.getUriForFile(
                                     mFragmentActivity,
                                     "com.****.********.provider",
                                     mCardStmtFile
                                 )
                                 intentShareFile.putExtra(Intent.EXTRA_STREAM, fileUri)
                                 intentShareFile.putExtra(
                                     Intent.EXTRA_SUBJECT,
                                     getDescription()
                                 )
                                 //adding grant read permission to share internal file
                                 intentShareFile.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                 startActivity(Intent.createChooser(intentShareFile, "Share File"))
                             }*/
                        } else if (flag == 2) {
                            /*
                            list.removeAt(position)

                            (binding.rvDownloads.adapter as MyDownloadsAdapter).submitList(
                                list
                            )

                            val file = File(filesDir, str)
                            file.delete()

                            if (list.isEmpty())
                                imgNoFile.visibility = View.VISIBLE

                            */
                            //=================================================

                            try {

                                mDialog = Dialog(this@MyDownloadsActivity)

                                val dialogBinding: DesignDeleteFileDialogBinding =
                                    DesignDeleteFileDialogBinding.inflate(layoutInflater)
                                mDialog!!.setContentView(dialogBinding.root)
                                mDialog!!.setCanceledOnTouchOutside(true)
                                mDialog!!.setCancelable(true)
                                mDialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                                mDialog!!.window!!.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                                mDialog!!.window!!.setGravity(Gravity.CENTER)

                                dialogBinding.tvCancel.setOnClickListener {
                                    mDialog!!.dismiss()
                                }

                                dialogBinding.tvYes.setOnClickListener {

                                    list.removeAt(position)

                                    (binding.rvDownloads.adapter as MyDownloadsAdapter).submitList(
                                        list
                                    )

                                    val file = File(filesDir, str)
                                    file.delete()

                                    if (list.isEmpty())
                                        imgNoFile.visibility = View.VISIBLE

                                    mDialog!!.dismiss()
                                }

                                mDialog!!.show()

                            }catch (e:Exception){
                                e.printStackTrace()
                            }

                        }
                    }
                adapter = myDownloadsAdapter
            }
            if (downloadsList.isNotEmpty())
                myDownloadsAdapter.submitList(downloadsList)
            else
                imgNoFile.visibility = View.VISIBLE

        }

        //getDataFromServer()
        binding.imgBack.setOnClickListener { finish() }

    }

/*private fun getDataFromServer() {
    for (i in 0..5) {
        val subject = Subject()
        downloadsList.add(subject)
    }

    myDownloadsAdapter.submitList(downloadsList)
}*/


}