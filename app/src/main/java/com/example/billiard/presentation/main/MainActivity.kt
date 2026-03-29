package com.example.billiard.presentation.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.billiard.R
import com.example.billiard.core.utils.SessionManager
import com.example.billiard.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()
    }
    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val role = sessionManager.getUserRole() ?: "EMPLOYEE"

            // 2. VẼ LẠI MENU BOTTOM NAV DỰA TRÊN ROLE MỚI NHẤT
            val menu = binding.bottomNavigation.menu
            val isManager = role == "MANAGER"
            menu.findItem(R.id.stasticsFragment)?.isVisible = isManager
            menu.findItem(R.id.administraitorFragment)?.isVisible = isManager

            // 3. BẢO VỆ CẤP ĐỘ 2 (Security Guard)
            if (!isManager &&
                (destination.id == R.id.stasticsFragment || destination.id == R.id.administraitorFragment)) {
                // Đá văng về Home nếu cố tình truy cập trái phép
                navController.popBackStack(R.id.homeFragment, false)
                return@addOnDestinationChangedListener
            }
            when (destination.id) {
                R.id.homeFragment,
                R.id.stasticsFragment,
                R.id.administraitorFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.GONE
                }
            }
        }
    }
}