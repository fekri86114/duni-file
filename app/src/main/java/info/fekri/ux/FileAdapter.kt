package info.fekri.ux

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import info.fekri.R
import info.fekri.databinding.ItemFileVerticalBinding
import info.fekri.ui.MainActivity
import java.io.File
import java.net.URLConnection

class FileAdapter(private val data: ArrayList<File>, private val fileEvent: FileEvent) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {
    private var ourViewType: Int = 0

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView = itemView.findViewById<TextView>(R.id.textView)
        private val imageView = itemView.findViewById<ImageView>(R.id.imageView)

        fun bindViews(file: File) {
            // memTypes --> http://androidxref.com/4.4.4_r1/xref/frameworks/base/media/java/android/media/MediaFile.java#174
            var fileType = ""

            textView.text = file.name

            if (file.isFile)
                when {
                    isImage(file.path) -> {
                        imageView.setImageResource(R.drawable.ic_image)
                        fileType = "image/*"
                    }

                    isVideo(file.path) -> {
                        imageView.setImageResource(R.drawable.ic_video)
                        fileType = "video/*"
                    }

                    isZip(file.name) -> {
                        imageView.setImageResource(R.drawable.ic_zip)
                        fileType = "application/zip"
                    }

                    else -> {
                        imageView.setImageResource(R.drawable.ic_file)
                        fileType = "text/plain"
                    }
                }
            else
                imageView.setImageResource(R.drawable.ic_folder)

            // what to do when clicked on items -->
            itemView.setOnClickListener {
                if (file.isFile)
                    fileEvent.onFileClicked(file, fileType)
                else
                    fileEvent.onFolderClicked(file.path)
            }
            itemView.setOnLongClickListener {
                fileEvent.onLongClicked(file, adapterPosition)
                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view: View

        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_file_vertical, parent, false)
        } else {
            view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_file_grid, parent, false)
        }

        return FileViewHolder(view)
    }
    override fun getItemCount(): Int {
        return data.size
    }
    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bindViews(data[position])
    }
    override fun getItemViewType(position: Int): Int {
        return ourViewType
    }

    private fun isImage(path: String): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("image")
    }
    private fun isVideo(path: String): Boolean {
        val mimeType: String = URLConnection.guessContentTypeFromName(path)
        return mimeType.startsWith("video")
    }
    private fun isZip(name: String): Boolean {
        return name.contains(".zip") || name.contains(".rar")
    }

    fun addNewFile(mNewFile: File) {

        data.add(0, mNewFile)
        notifyItemInserted(0)

    }
    fun removeItem(oldFile: File, position: Int) {

        data.remove(oldFile)
        notifyItemRemoved(position)

    }
    fun changeViewType(newViewType: Int) {
        ourViewType = newViewType
        notifyDataSetChanged()
    }

    interface FileEvent {

        fun onFileClicked(file: File, type: String)

        fun onFolderClicked(path: String)

        fun onLongClicked(file: File, position: Int)

    }

} // 289 , set remove fun in adapter