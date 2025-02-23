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

    private fun resetear() {
        if (binding.reset.isPressed) {
            binding.Contador1.setText("0")
        }else if (binding.resetenemy.isPressed){
            binding.Contador2.setText("0")
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.reset.id -> resetear()
            binding.resetenemy.id -> resetear()
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
    }
}


