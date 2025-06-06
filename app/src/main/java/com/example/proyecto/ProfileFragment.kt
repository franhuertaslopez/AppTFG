package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.proyecto.databinding.CustomDialogChangeEmailBinding
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

    private fun showChangeEmailDialog() {
        val dialogBinding = CustomDialogChangeEmailBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val currentEmail = dialogBinding.currentEmail.text.toString().trim()
            val newEmail = dialogBinding.newEmail.text.toString().trim()
            val confirmEmail = dialogBinding.confirmNewEmail.text.toString().trim()
            val currentPassword = dialogBinding.currentPassword.text.toString().trim()

            if (currentEmail.isEmpty() || newEmail.isEmpty() || confirmEmail.isEmpty() || currentPassword.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.fields_cannot_be_empty), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = auth.currentUser

            if (user == null || user.email != currentEmail) {
                Toast.makeText(requireContext(), "El correo actual no es correcto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newEmail != confirmEmail) {
                Toast.makeText(requireContext(), "Los correos nuevos no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    user.updateEmail(newEmail).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Correo actualizado correctamente", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                            // Cerrar sesión y volver a login
                            auth.signOut()
                            goToLogin()
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar el correo", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    private fun showDeleteAccountDialog() {
        val dialogBinding = CustomDialogDeleteAccBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        val btnConfirm = dialogBinding.btnConfirm
        val currentEmailInput = dialogBinding.currentEmail
        val currentPasswordInput = dialogBinding.currentPassword
        val checkboxConfirmDelete = dialogBinding.checkboxConfirmDelete

        btnConfirm.isEnabled = false

        fun updateConfirmButtonState() {
            val emailFilled = currentEmailInput.text.toString().trim().isNotEmpty()
            val passwordFilled = currentPasswordInput.text.toString().trim().isNotEmpty()
            val checked = checkboxConfirmDelete.isChecked
            btnConfirm.isEnabled = emailFilled && passwordFilled && checked
        }

        currentEmailInput.addTextChangedListener { updateConfirmButtonState() }
        currentPasswordInput.addTextChangedListener { updateConfirmButtonState() }
        checkboxConfirmDelete.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }

        dialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val currentEmail = currentEmailInput.text.toString().trim()
            val currentPassword = currentPasswordInput.text.toString().trim()

            val user = auth.currentUser
            if (user == null || user.email != currentEmail) {
                Toast.makeText(requireContext(), "El correo actual no es correcto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
            user.reauthenticate(credential).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    user.delete().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Toast.makeText(requireContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show()
                            alertDialog.dismiss()
                            auth.signOut()
                            goToLogin()
                        } else {
                            Toast.makeText(requireContext(), "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }

        alertDialog.show()
    }

    private fun goToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
