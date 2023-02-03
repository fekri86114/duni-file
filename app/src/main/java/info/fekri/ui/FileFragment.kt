package info.fekri.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.fekri.R
import info.fekri.databinding.DialogAddFileBinding
import info.fekri.databinding.DialogAddFolderBinding
import info.fekri.databinding.DialogRemoveItemBinding
import info.fekri.databinding.FragmentFileBinding
import info.fekri.ux.FileAdapter
import java.io.File

class FileFragment(val path: String) : Fragment(), FileAdapter.FileEvent {
    private lateinit var binding: FragmentFileBinding
    private lateinit var mAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFileBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (MainActivity.ourViewType == 0) {
            binding.btnShowType.setImageResource(R.drawable.ic_grid)
        } else {
            binding.btnShowType.setImageResource(R.drawable.ic_list)
        }

        val ourFile = File(path)
        binding.txtPath.text = "${ourFile.name}>"

        // is file or folder -->
        if (ourFile.isDirectory) {

            val listOfFiles = arrayListOf<File>()
            listOfFiles.addAll(ourFile.listFiles()!!)
            listOfFiles.sort()

            mAdapter = FileAdapter(listOfFiles, this)
            binding.recyclerMain.adapter = mAdapter
            binding.recyclerMain.layoutManager =
                GridLayoutManager(
                    context,
                    MainActivity.ourSpanCount,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            mAdapter.changeViewType(MainActivity.ourViewType)

            if (listOfFiles.size > 0) {

                binding.recyclerMain.visibility = View.VISIBLE
                binding.imgNoData.visibility = View.GONE

            } else {
                binding.recyclerMain.visibility = View.GONE
                binding.imgNoData.visibility = View.VISIBLE
            }

        }

        binding.btnAddFolder.setOnClickListener {
            createNewFolder()
        }
        binding.btnAddFile.setOnClickListener {
            createNewFile()
        }
        binding.btnShowType.setOnClickListener {
            if (MainActivity.ourViewType == 0) {

                MainActivity.ourViewType = 1
                MainActivity.ourSpanCount = 3

                mAdapter.changeViewType(MainActivity.ourViewType)
                binding.recyclerMain.layoutManager =
                    GridLayoutManager(context, MainActivity.ourSpanCount)

                binding.btnShowType.setImageResource(R.drawable.ic_list)

            } else if (MainActivity.ourViewType == 1) {

                MainActivity.ourViewType = 0
                MainActivity.ourSpanCount = 1

                mAdapter.changeViewType(MainActivity.ourViewType)
                binding.recyclerMain.layoutManager =
                    GridLayoutManager(context, MainActivity.ourSpanCount)

                binding.btnShowType.setImageResource(R.drawable.ic_grid)

            }
        }

    }

    private fun createNewFile() {
        val dialogAddFileBinding = DialogAddFileBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).create()

        dialog.setView(dialogAddFileBinding.root)
        dialog.setCancelable(true)
        dialog.show()

        dialogAddFileBinding.btnCreate.setOnClickListener {

            val mNewFolderName = dialogAddFileBinding.edtAddFolder.text.toString()
            // file/pic
            val mNewFolder = File(path + File.separator + mNewFolderName)

            if (!mNewFolder.exists()) {
                // create file -->
                if (mNewFolder.createNewFile()) {
                    mAdapter.addNewFile(mNewFolder)
                    binding.recyclerMain.scrollToPosition(0)

                    binding.recyclerMain.visibility = View.VISIBLE
                    binding.imgNoData.visibility = View.INVISIBLE
                }
            }

            dialog.dismiss() // dismiss dialog
        }
        dialogAddFileBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }
    private fun createNewFolder() {
        val dialogAddFolderBinding = DialogAddFolderBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).create()

        dialog.setView(dialogAddFolderBinding.root)
        dialog.setCancelable(true)
        dialog.show()

        dialogAddFolderBinding.btnCreate.setOnClickListener {

            val mNewFolderName = dialogAddFolderBinding.edtAddFolder.text.toString()

            // file/pic
            val mNewFolder = File(path + File.separator + mNewFolderName)

            if (!mNewFolder.exists()) {

                // create folder -->
                if (mNewFolder.mkdir()) {
                    mAdapter.addNewFile(mNewFolder)
                    binding.recyclerMain.scrollToPosition(0)

                    binding.recyclerMain.visibility = View.VISIBLE
                    binding.imgNoData.visibility = View.INVISIBLE
                }

            }

            dialog.dismiss() // dismiss dialog
        }
        dialogAddFolderBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

    }

    override fun onFileClicked(file: File, type: String) {

        val intent = Intent(Intent.ACTION_VIEW)

        // check how to open the files -->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val fileProvider = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().packageName}.provider",
                file
            )

            intent.setDataAndType(fileProvider, type)

        } else intent.setDataAndType(Uri.fromFile(file), type)

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // give permission to read the uri
        startActivity(intent)

    }
    override fun onFolderClicked(path: String) {

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_main_container, FileFragment(path))
        transaction.addToBackStack(null)
        transaction.commit()

    }
    override fun onLongClicked(file: File, position: Int) {

        val dialog = AlertDialog.Builder(context).create()
        val dialogRemoveBinding = DialogRemoveItemBinding.inflate(layoutInflater)

        dialog.setView(dialogRemoveBinding.root)
        dialog.show()

        dialogRemoveBinding.btnCancel.setOnClickListener {
            dialog.dismiss() // dismiss dialog
        }

        dialogRemoveBinding.btnCreate.setOnClickListener {

            if (file.exists()) {
                if (file.deleteRecursively()) {
                    mAdapter.removeItem(file, position)
                }
            }

            dialog.dismiss()
        }

    }

}