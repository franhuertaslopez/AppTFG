package com.example.proyecto.MainMenuFragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.proyecto.Authentication.LoginActivity
import com.example.proyecto.R
import com.example.proyecto.databinding.CustomDialogChangeEmailBinding
import com.example.proyecto.databinding.CustomDialogChangePasswordBinding
import com.example.proyecto.databinding.CustomDialogDeleteAccBinding
import com.example.proyecto.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user != null) {
            binding.emailText.text = getString(R.string.profile_email, maskEmail(user.email ?: ""))

            val metadata = user.metadata
            metadata?.creationTimestamp?.let {
                val date = Date(it)
                val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val formattedDate = format.format(date)
                binding.createdAtText.text = getString(R.string.profile_date, formattedDate)
            } ?: run {
                binding.createdAtText.text = getString(R.string.unknow_profile_date)
            }
        } else {
            binding.emailText.text = getString(R.string.unknow_profile_email)
            binding.createdAtText.text = ""
        }

        binding.changeEmailButton.setOnClickListener {
            showChangeEmailDialog()
        }

        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        return binding.root
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf("@")
        if (atIndex <= 3) return email
        val visiblePart = email.substring(0, 3)
        val domainPart = email.substring(atIndex)
        return "$visiblePart****$domainPart"
    }

    private fun isEmailCorrect(currentEmail: String?, inputEmail: String): Boolean {
        return currentEmail == inputEmail
    }

    private fun showChangeEmailDialog() {
        val dialogBinding = CustomDialogChangeEmailBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.in_development), Toast.LENGTH_SHORT).show()
        }

        alertDialog.show()
    }

    private fun showChangePasswordDialog() {
        val dialogBinding = CustomDialogChangePasswordBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val currentPasswordInput = dialogBinding.currentPasswordChangePass
        val newPasswordInput = dialogBinding.newPassword
        val confirmNewPasswordInput = dialogBinding.confirmNewPassword
        val checkboxConfirm = dialogBinding.checkboxConfirmChangePass
        val btnConfirm = dialogBinding.btnConfirmChangePassword
        val btnCancel = dialogBinding.btnCancelChangePassword

        btnConfirm.isEnabled = false

        fun updateConfirmButtonState() {
            val currentPassFilled = currentPasswordInput.text.toString().trim().isNotEmpty()
            val newPassFilled = newPasswordInput.text.toString().trim().isNotEmpty()
            val confirmPassFilled = confirmNewPasswordInput.text.toString().trim().isNotEmpty()
            val isChecked = checkboxConfirm.isChecked

            btnConfirm.isEnabled = currentPassFilled && newPassFilled && confirmPassFilled && isChecked
        }

        // Listeners que llaman a updateConfirmButtonState
        currentPasswordInput.addTextChangedListener { updateConfirmButtonState() }
        newPasswordInput.addTextChangedListener { updateConfirmButtonState() }
        confirmNewPasswordInput.addTextChangedListener { updateConfirmButtonState() }
        checkboxConfirm.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val currentPassword = currentPasswordInput.text.toString().trim()
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = confirmNewPasswordInput.text.toString().trim()

            if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), getString(R.string.password_dissmatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser
            val email = user?.email

            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                user.reauthenticate(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Toast.makeText(requireContext(), getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show()
                                alertDialog.dismiss()
                                auth.signOut()
                                goToLogin()
                            } else {
                                Toast.makeText(requireContext(), getString(R.string.error_updating_password), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.unknown_user), Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun showDeleteAccountDialog() {
        val dialogBinding = CustomDialogDeleteAccBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val btnConfirm = dialogBinding.btnConfirmDeleteAccount
        val currentEmailInput = dialogBinding.currentEmailDeleteAccount
        val currentPasswordInput = dialogBinding.currentPasswordDeleteAccount
        val checkboxConfirmDelete = dialogBinding.checkboxConfirmDelete

        btnConfirm.isEnabled = false

        var animator: ObjectAnimator? = null

        fun updateConfirmButtonState() {
            val emailFilled = currentEmailInput.text.toString().trim().isNotEmpty()
            val passwordFilled = currentPasswordInput.text.toString().trim().isNotEmpty()
            val checked = checkboxConfirmDelete.isChecked

            val shouldEnable = emailFilled && passwordFilled && checked

            btnConfirm.isEnabled = shouldEnable

            if (shouldEnable) {
                if (animator == null) {
                    animator = ObjectAnimator.ofFloat(btnConfirm, "rotation", 0f, 0.7f, 0f, -0.7f).apply {
                        duration = 100L
                        repeatCount = ValueAnimator.INFINITE
                        repeatMode = ValueAnimator.RESTART
                        start()
                    }
                } else if (!animator!!.isRunning) {
                    animator!!.start()
                }
            } else {
                animator?.cancel()
                btnConfirm.rotation = 0f
                animator = null
            }
        }

        currentEmailInput.addTextChangedListener { updateConfirmButtonState() }
        currentPasswordInput.addTextChangedListener { updateConfirmButtonState() }
        checkboxConfirmDelete.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }

        dialogBinding.btnCancelDeleteAccount.setOnClickListener {
            btnConfirm.clearAnimation()
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            btnConfirm.clearAnimation()

            val currentEmail = currentEmailInput.text.toString().trim()
            val currentPassword = currentPasswordInput.text.toString().trim()

            val user = auth.currentUser
            if (!isEmailCorrect(user?.email, currentEmail)) {
                Toast.makeText(requireContext(), getString(R.string.incorrect_current_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
            user!!.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toast.makeText(requireContext(), getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT).show()
                            btnConfirm.clearAnimation()
                            auth.signOut()
                            alertDialog.dismiss()
                            goToLogin()
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.error_deleting_account), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    private fun goToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
