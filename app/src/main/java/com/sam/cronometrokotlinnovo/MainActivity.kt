package com.sam.cronometrokotlinnovo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.sam.cronometrokotlinnovo.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*
// elvis operator?: 0
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var timer: CountDownTimer? = null
    private var timeLeft: Long = 600000 // tempo inicial em milissegundos
    private var isTimerRunning = false
    private val oneSecond = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var textDigitado = binding.editTempo  //captura o q foi digitado no editText
        val editText = textDigitado


        binding.btIniciar.setOnClickListener {
            startTimer()
        }
        binding.btPausar.setOnClickListener {
            pauseTimer()
        }
        binding.btResetar.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {


        var textDigitado = binding.editTempo.text  //captura o q foi digitado no editText

        //oculta o teclado do celular
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        binding.editTempo.visibility = View.INVISIBLE //deixa a variavel editText invisivel



        var exibicao = binding.txTempo


//A classe Number é uma classe abstrata que representa um número e é superclasse de todas as outras classes numéricas, como Int, Long, Float e Double.
// verifica o que foi digitado no EditText, se  é vazio/null e diferente de Numeros, se for o caso, a timeLeft assume valor zero.
        if (textDigitado.isNullOrEmpty() || textDigitado !is Number ) {
            timeLeft = 0
        } else{
            timeLeft = (textDigitado.toString()).toLong()*1000
        }


        timer = object : CountDownTimer(timeLeft, oneSecond) {

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished ?: 0

                //time's format
                val format = SimpleDateFormat("HH:mm:ss")
                val formated = format.format(timeLeft)
                exibicao.setText(formated)
                // Atualizar a interface do usuário aqui, como mostrando o tempo restante

            }

            override fun onFinish() {
                binding.editTempo.text.clear()
                binding.editTempo.visibility = View.VISIBLE
                exibicao.text = ""
                // Executado quando o temporizador chega a zero
            }
        }.start()

        isTimerRunning = true

    }

    private fun pauseTimer(){
        timer?.cancel()
        isTimerRunning = false
    }

    private  fun resetTimer(){
        timer?.start()
        isTimerRunning = false
    }

    fun formatEditTextTempo() {

        var textDigitado = binding.editTempo  //captura o q foi digitado no editText

        val timeEditText = textDigitado
        timeEditText.setOnClickListener {
            timeEditText.text
            val format = SimpleDateFormat("HH:mm:ss")
            val formatted = format.format(Calendar.getInstance().time)
            timeEditText.setText(formatted)
        }
    }


}
