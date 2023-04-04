package com.sam.cronometrokotlinnovo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sam.cronometrokotlinnovo.databinding.ActivityPomodoroPersonalizadoBinding
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat



class Pomodoro_Personalizado : AppCompatActivity() {

    private lateinit var binding: ActivityPomodoroPersonalizadoBinding

    private var timeLeft: Long = 0 //tempo inicial em milissegundos
    private val oneSecond = 1000L
    private var timeRemaining : Long = 0
    private var x = 1 //variavel para fazer loop com if
    private var repeticaoCiclo: Int = 2
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false //Verifica se está em execução o pomodoro
    private var faseIniciar = false
    private var resumeDescansoIsRuning = false  // Verifica se está executando a fase descanso (há 2 fases, a iniciar e a descanso)




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPomodoroPersonalizadoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportActionBar?.hide()


        binding.btIniciar.setOnClickListener {

            x = 1
            binding.txtCiclo.setText("Ciclo: $x")


            if (isTimerRunning==false){
                iniciar()
                binding.btIniciar.setText("Parar")
                binding.btPausar.setText("Pausar")
                binding.btPausar.isEnabled = true //desativa o botão pausar

            } else{
                timer?.cancel()
                isTimerRunning = false
                binding.btIniciar.setText("Reiniciar")
                binding.btPausar.isEnabled = false //ativa o botão pausar
                binding.btPausar.setTextColor(Color.WHITE)
            }
        }

        binding.btPausar.setOnClickListener {

            if (isTimerRunning == true){
                pauseTimer()
                binding.btPausar.setText("Retomar")
                binding.btIniciar.isEnabled = false //ativa o botão pausar
                binding.btIniciar.setTextColor(Color.GRAY)


            } else{
                if (faseIniciar==true){
                    resumeIniciar()
                    binding.btPausar.setText("Pausar")
                    binding.btIniciar.isEnabled = true //ativa o botão pausar
                    binding.btIniciar.setTextColor(Color.WHITE)
                }
                else{
                    resumeDescanso()
                    binding.btPausar.setText("Pausar")
                    binding.btIniciar.isEnabled = true //ativa o botão pausar
                    binding.btIniciar.setTextColor(Color.WHITE)
                }
            }
        }

        binding.btPersonalizado.setOnClickListener {
            timer?.cancel()
            val intent = Intent(this, Pomodoro::class.java)
            startActivity(intent)
        }

    }


    private fun iniciar() {


        faseIniciar = true

        ocultarTecladoCelular()
        desabilitarBtIniciar()


        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Concentre")
        txtStatusAtividade.setTextColor(Color.RED)


        var textoEstudo = binding.editTempoEstudo.text.toString() //dados do tempo de estudo
        if ( textoEstudo.isNullOrEmpty() || textoEstudo.toLong() !is Number ) {
            timeLeft  = 0
        } else{
            timeLeft = (textoEstudo.toLong() * 1000) + 100
        }


        var exibicao = binding.txTempo //exibe resultado
        Thread.sleep(400)

        timer = object : CountDownTimer(timeLeft, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                faseIniciar = true
                timeLeft = millisUntilFinished
                //time's format
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeLeft)
                timeRemaining = timeLeft
                exibicao.setText(formated) // Atualizar a interface do usuário aqui, mostrando o tempo restante


            }
            override fun onFinish() {
                faseIniciar = false

                exibicao.text = ""
                descanso() // Executado quando o temporizador chegar a zero
            }
        }.start()
        isTimerRunning = true
    }


    private fun descanso(){


        desabilitarBtIniciar()

        //Mostra o status descanse
        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Descanse")
        txtStatusAtividade.setTextColor(Color.BLUE)


        faseIniciar = false
        Thread.sleep(1500)


        var textoDescanso = binding.editTempoDescanso.text.toString()
        if ( textoDescanso.isNullOrEmpty() || textoDescanso.toLong() !is Number ) {
            timeLeft  = 0
        } else{
            timeLeft = (textoDescanso.toLong() * 1000) +100
        }

        var textoRepeticao = binding.editRepeticao.text.toString()
        if (textoRepeticao.isNullOrEmpty() || textoRepeticao.toLong() !is Number){
            repeticaoCiclo = 0
        } else {
            repeticaoCiclo = textoRepeticao.toInt()
        }



        if (resumeDescansoIsRuning == false){ //não vai tocar o sino quando executar o resumeDescanso
            val player = MediaPlayer.create(applicationContext,R.raw.cartoon_cowbell).start()
        }


        var exibicao = binding.txTempo //exibe resultado

        timer = object : CountDownTimer(timeLeft, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                //time's format
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeLeft)
                exibicao.setText(formated) // Atualizar a interface do usuário aqui, como mostrando o tempo restante
                timeRemaining = timeLeft
            }

            override fun onFinish() {

                exibicao.text = "00:00" // Executado quando o temporizador chega a zero
                txtStatusAtividade.text = ""
                binding.btIniciar.setText("Iniciar")
                habilitarBtIniciar()
                isTimerRunning = false

                if (x < repeticaoCiclo ){
                    iniciar()
                    x++
                    binding.txtCiclo.setText("Ciclo: $x")
                } else{
                    binding.txtCiclo.visibility = View.INVISIBLE
                }

                if (isTimerRunning == false) {
                    binding.btIniciar.setText("Reiniciar")
                }
            }
        }
        timer?.start()
        isTimerRunning = true
    }

    private fun pauseTimer(){
        timer?.cancel()
        isTimerRunning = false
    }


    private fun resumeIniciar(){
        faseIniciar = true
        var exibicao = binding.txTempo

        //oculta editText
        binding.editTempoEstudo.visibility = View.INVISIBLE
        binding.editTempoDescanso.visibility = View.INVISIBLE

        timer = object : CountDownTimer(timeRemaining, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeRemaining)
                exibicao.setText(formated)

            }
            override fun onFinish() {
                descanso()
            }
        }.start()
        isTimerRunning = true
    }


    private fun resumeDescanso(){

        faseIniciar = false
        var exibicao = binding.txTempo

        //oculta editText
        binding.editTempoEstudo.visibility = View.INVISIBLE
        binding.editTempoDescanso.visibility = View.INVISIBLE

        timer = object : CountDownTimer(timeRemaining, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeRemaining)
                exibicao.setText(formated)
            }
            override fun onFinish() {

                exibicao.text = "Fim" // Executado quando o temporizador chega a zero
                binding.btIniciar.setText("Iniciar")
                habilitarBtIniciar()
                resumeDescansoIsRuning = false


                if (x < repeticaoCiclo ){
                    iniciar()
                    x++
                }


            }
        }.start()
        isTimerRunning = true
    }



    private fun ocultarTecladoCelular(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }


    private fun desabilitarBtIniciar(){
        //binding.btIniciar.isEnabled = false //desativa o botão iniciar
        //binding.btIniciar.setTextColor(Color.GRAY)
        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Descanse")
        txtStatusAtividade.setTextColor(Color.BLUE)

        binding.editTempoEstudo.visibility = View.INVISIBLE
        binding.editTempoDescanso.visibility = View.INVISIBLE
        binding.editRepeticao.visibility = View.INVISIBLE
        binding.txtTempoEstudo.visibility = View.INVISIBLE
        binding.txtTempoDescanso.visibility = View.INVISIBLE
        binding.txtRepeticao.visibility = View.INVISIBLE

    }

    private fun habilitarBtIniciar(){
        //binding.btIniciar.isEnabled = true //ativa o botão iniciar no final da execução
        binding.btIniciar.setTextColor(Color.WHITE)
        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.text = ""

        binding.editTempoEstudo.visibility = View.VISIBLE
        binding.editTempoDescanso.visibility = View.VISIBLE
        binding.editRepeticao.visibility = View.VISIBLE
        binding.txtTempoEstudo.visibility = View.VISIBLE
        binding.txtTempoDescanso.visibility = View.VISIBLE
        binding.txtRepeticao.visibility = View.VISIBLE

    }

}
