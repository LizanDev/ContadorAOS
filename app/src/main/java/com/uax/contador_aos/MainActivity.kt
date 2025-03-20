package com.uax.contador_aos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.uax.contador_aos.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sumar1.setOnClickListener(this)
        binding.sumar2.setOnClickListener(this)
        binding.sumar5.setOnClickListener(this)
        binding.sumar1enemy.setOnClickListener(this)
        binding.sumar2enemy.setOnClickListener(this)
        binding.sumar5enemy.setOnClickListener(this)
        binding.commandplus1.setOnClickListener(this)
        binding.commandplus1enemy.setOnClickListener(this)
        binding.commandminus1.setOnClickListener(this)
        binding.commandminus1enemy.setOnClickListener(this)
        binding.reset.setOnClickListener(this)
        binding.resetenemy.setOnClickListener(this)

    }

    private fun resetear(view: View) {
        when (view.id) {
            binding.reset.id -> binding.Contador1.text = "0"
            binding.resetenemy.id -> binding.Contador2.text = "0"
        }

    }

        override fun onClick(v: View?) {
            when (v?.id) {
                binding.reset.id -> resetear(v)
                binding.resetenemy.id -> resetear(v)
                binding.sumar1.id -> binding.Contador1.text =
                    (binding.Contador1.text.toString().toInt() + 1).toString()
                binding.sumar2.id -> binding.Contador1.text =
                    (binding.Contador1.text.toString().toInt() + 2).toString()
                binding.sumar5.id -> binding.Contador1.text =
                    (binding.Contador1.text.toString().toInt() + 5).toString()
                binding.sumar1enemy.id -> binding.Contador2.text =
                    (binding.Contador2.text.toString().toInt() + 1).toString()
                binding.sumar2enemy.id -> binding.Contador2.text =
                    (binding.Contador2.text.toString().toInt() + 2).toString()
                binding.sumar5enemy.id -> binding.Contador2.text =
                    (binding.Contador2.text.toString().toInt() + 5).toString()
                binding.commandplus1.id -> binding.combatpoints1.text =
                    (binding.combatpoints1.text.toString().toInt() + 1).toString()
                binding.commandplus1enemy.id -> binding.combarpoints2.text =
                    (binding.combarpoints2.text.toString().toInt() + 1).toString()
                binding.commandminus1.id -> binding.combatpoints1.text =
                    (binding.combatpoints1.text.toString().toInt() - 1).toString()
                binding.commandminus1enemy.id -> binding.combarpoints2.text =
                    (binding.combarpoints2.text.toString().toInt() - 1).toString()

            }
            v?.let { animarBoton(it) }
        }
    }


    private fun animarBoton(view: View) {
        view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100)
        }
    }




