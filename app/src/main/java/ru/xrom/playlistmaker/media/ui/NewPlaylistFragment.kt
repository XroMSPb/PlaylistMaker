package ru.xrom.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.xrom.playlistmaker.R
import ru.xrom.playlistmaker.databinding.FragmentNewplaylistBinding
import ru.xrom.playlistmaker.utils.convertDpToPx


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri = Uri.EMPTY
    private val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, callback)
        binding.btnCreate.isEnabled = false
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.playlistName.doOnTextChanged { s, _, _, _ ->
            binding.btnCreate.isEnabled = s?.isEmpty() != true
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.image.scaleType = ImageView.ScaleType.CENTER_CROP
                    Glide.with(this)
                        .load(uri)
                        .apply(
                            RequestOptions().transform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCorners(
                                        convertDpToPx(
                                            8f,
                                            requireContext()
                                        )
                                    )
                                )
                            )
                        )
                        .into(binding.image)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.image.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnCreate.setOnClickListener {
            val playlistName = binding.playlistName.text.toString()
            viewModel.createPlaylist(
                playlistName, binding.playlistDescription.text.toString(), imageUri
            )
            Toast.makeText(
                context,
                context?.getString(R.string.playlist_created)?.format(playlistName),
                Toast.LENGTH_SHORT
            ).show()

            closeFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (binding.playlistName.text.toString().isNotEmpty()) {
                MaterialAlertDialogBuilder(context!!).setTitle(R.string.exit_title)
                    .setMessage(R.string.exit_message)
                    .setNeutralButton(android.R.string.cancel) { dialog, which ->

                    }.setPositiveButton(R.string.finish) { dialog, which ->
                        closeFragment()
                    }.show()
            } else closeFragment()
        }
    }

    private fun closeFragment() {
        parentFragmentManager.setFragmentResult(RESULT, Bundle())
        parentFragmentManager.popBackStack()
        //findNavController().navigateUp()
    }

    companion object {
        const val RESULT = "RESULT_KEY"
    }

}


