package com.example.theguardian_news_app.View.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import com.bumptech.glide.Glide
import com.example.theguardian_news_app.R
import com.example.theguardian_news_app.View.SignInActivity
import com.example.theguardian_news_app.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class profileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebase: FirebaseAuth
    var isInitialSetup = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        /*
        val email = arguments?.getString("email").toString()
        binding.email.text = email
        Log.e("email",email)

         */
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        firebase = FirebaseAuth.getInstance()
        val user = firebase.currentUser?.email
        binding.email.text = user

        // Use the value of currentThemeMode to determine the theme mode
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                binding.switchTheme.isChecked = false
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                binding.switchTheme.isChecked = true
               }
        }


        Glide.with(this)
            .load("https://xsgames.co/randomusers/avatar.php?g=male")
            .placeholder(R.drawable.img)
            .into(binding.profilePic)

        binding.shareText.setOnClickListener{
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Our App Link When it is on playstore")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.logoutText.setOnClickListener{
            firebase.signOut()
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(requireContext(), isChecked.toString(), Toast.LENGTH_SHORT).show()
            if (isChecked) {
                binding.aboutusIcon.setImageResource(R.drawable.baseline_info_outline_24_light)
                binding.shareIcon.setImageResource(R.drawable.baseline_share_24_light)
                binding.themeIcon.setImageResource(R.drawable.baseline_dark_mode_24_light)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


            } else {
                binding.aboutusIcon.setImageResource(R.drawable.baseline_info_outline_24)
                binding.shareIcon.setImageResource(R.drawable.baseline_share_24)
                binding.themeIcon.setImageResource(R.drawable.baseline_dark_mode_24)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            }


            //requireActivity().recreate()
        }

    }
}
