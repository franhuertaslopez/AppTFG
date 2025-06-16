package com.example.proyecto.MainMenuFragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val goals = listOf("Pérdida de peso", "Ganancia muscular", "Mantenimiento")
    private val levels = listOf("Principiante", "Intermedio", "Avanzado")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setupDropdowns()
        loadUserData()
        setupUserInfo()
        setupButtonListeners()

        return binding.root
    }

    private fun setupDropdowns() {
        binding.autoGoal.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, goals)
        )
        binding.autoLevel.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, levels)
        )
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: run {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (doc?.exists() == true) {
                    val goalText = keyToGoal(doc.getString("goal") ?: "")
                    val levelText = keyToLevel(doc.getString("level") ?: "")

                    binding.autoGoal.setText(goalText, false)
                    binding.autoLevel.setText(levelText, false)
                } else {
                    Toast.makeText(requireContext(), "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupUserInfo() {
        val user = auth.currentUser
        if (user != null) {
            binding.emailText.text = getString(R.string.profile_email, maskEmail(user.email.orEmpty()))
            val creationTimestamp = user.metadata?.creationTimestamp
            binding.createdAtText.text = creationTimestamp?.let {
                val date = Date(it)
                val format = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                getString(R.string.profile_date, format.format(date))
            } ?: getString(R.string.unknow_profile_date)
        } else {
            binding.emailText.text = getString(R.string.unknow_profile_email)
            binding.createdAtText.text = ""
        }
    }

    private fun setupButtonListeners() {
        binding.changeEmailButton.setOnClickListener { showChangeEmailDialog() }
        binding.changePasswordButton.setOnClickListener { showChangePasswordDialog() }
        binding.btnDeleteAccount.setOnClickListener { showDeleteAccountDialog() }
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf("@")
        return if (atIndex > 3) {
            "${email.take(3)}****${email.substring(atIndex)}"
        } else email
    }

    private fun isEmailCorrect(currentEmail: String?, inputEmail: String): Boolean =
        currentEmail == inputEmail

    private fun showChangeEmailDialog() {
        val dialogBinding = CustomDialogChangeEmailBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener { alertDialog.dismiss() }
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

        with(dialogBinding) {
            btnConfirmChangePassword.isEnabled = false

            fun updateConfirmButtonState() {
                val enabled = currentPasswordChangePass.text.toString().trim().isNotEmpty() &&
                        newPassword.text.toString().trim().isNotEmpty() &&
                        confirmNewPassword.text.toString().trim().isNotEmpty() &&
                        checkboxConfirmChangePass.isChecked
                btnConfirmChangePassword.isEnabled = enabled
            }

            currentPasswordChangePass.addTextChangedListener { updateConfirmButtonState() }
            newPassword.addTextChangedListener { updateConfirmButtonState() }
            confirmNewPassword.addTextChangedListener { updateConfirmButtonState() }
            checkboxConfirmChangePass.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }

            btnCancelChangePassword.setOnClickListener { alertDialog.dismiss() }

            btnConfirmChangePassword.setOnClickListener {
                val currentPassword = currentPasswordChangePass.text.toString().trim()
                val newPasswordStr = newPassword.text.toString().trim()
                val confirmPassword = confirmNewPassword.text.toString().trim()

                if (newPasswordStr != confirmPassword) {
                    Toast.makeText(requireContext(), getString(R.string.password_dissmatch), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val user = auth.currentUser
                val email = user?.email

                if (email != null) {
                    val credential = EmailAuthProvider.getCredential(email, currentPassword)
                    user.reauthenticate(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            user.updatePassword(newPasswordStr).addOnCompleteListener { updateTask ->
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
        }

        alertDialog.show()
    }

    private fun showDeleteAccountDialog() {
        val dialogBinding = CustomDialogDeleteAccBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .create()

        with(dialogBinding) {
            btnConfirmDeleteAccount.isEnabled = false
            var animator: ObjectAnimator? = null

            fun updateConfirmButtonState() {
                val enabled = currentEmailDeleteAccount.text.toString().trim().isNotEmpty() &&
                        currentPasswordDeleteAccount.text.toString().trim().isNotEmpty() &&
                        checkboxConfirmDelete.isChecked

                btnConfirmDeleteAccount.isEnabled = enabled

                if (enabled) {
                    if (animator == null) {
                        animator = ObjectAnimator.ofFloat(btnConfirmDeleteAccount, "rotation", 0f, 0.7f, 0f, -0.7f).apply {
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
                    btnConfirmDeleteAccount.rotation = 0f
                    animator = null
                }
            }

            currentEmailDeleteAccount.addTextChangedListener { updateConfirmButtonState() }
            currentPasswordDeleteAccount.addTextChangedListener { updateConfirmButtonState() }
            checkboxConfirmDelete.setOnCheckedChangeListener { _, _ -> updateConfirmButtonState() }

            btnCancelDeleteAccount.setOnClickListener {
                animator?.cancel()
                alertDialog.dismiss()
            }

            btnConfirmDeleteAccount.setOnClickListener {
                animator?.cancel()

                val currentEmail = currentEmailDeleteAccount.text.toString().trim()
                val currentPassword = currentPasswordDeleteAccount.text.toString().trim()

                val user = auth.currentUser
                if (!isEmailCorrect(user?.email, currentEmail)) {
                    Toast.makeText(requireContext(), getString(R.string.incorrect_current_email), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val credential = EmailAuthProvider.getCredential(currentEmail, currentPassword)
                user?.reauthenticate(credential)?.addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        user.uid.let { uid ->
                            firestore.collection("users").document(uid).delete()
                                .addOnSuccessListener {
                                    user.delete().addOnCompleteListener { deleteTask ->
                                        if (deleteTask.isSuccessful) {
                                            Toast.makeText(requireContext(), getString(R.string.account_deleted_successfully), Toast.LENGTH_SHORT).show()
                                            auth.signOut()
                                            alertDialog.dismiss()
                                            goToLogin()
                                        } else {
                                            Toast.makeText(requireContext(), getString(R.string.error_deleting_account), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), getString(R.string.error_deleting_account), Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.incorrect_password), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        alertDialog.show()
    }

    private fun goToLogin() {
        Intent(requireContext(), LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun keyToGoal(key: String): String = when (key) {
        "weight_loss_goal" -> getString(R.string.goal_weight_loss)
        "muscle_gain_goal" -> getString(R.string.goal_muscle_gain)
        "maintenance_goal" -> getString(R.string.goal_maintenance)
        else -> ""
    }

    private fun keyToLevel(key: String): String = when (key) {
        "beginner" -> getString(R.string.level_begginer)
        "intermediate" -> getString(R.string.level_intermediate)
        "advanced" -> getString(R.string.level_advanced)
        else -> ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
