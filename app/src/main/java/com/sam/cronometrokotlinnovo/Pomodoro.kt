package com.sam.cronometrokotlinnovo
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import com.sam.cronometrokotlinnovo.databinding.ActivityPomodoroBinding
import java.text.SimpleDateFormat
//"Quem controla seu tempo, domina o futuro.
//fazer testes com tempo maior
class Pomodoro : AppCompatActivity() {

    private lateinit var binding: ActivityPomodoroBinding


    private var timeLeft: Long = 0 //tempo inicial em milissegundos
    private val oneSecond = 1000L
    private var x = 1
    private var repeticaoCiclo: Int = 4
    private var timeRemaining : Long = 0
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false //Verifica se está em execução o pomodoro
    private var faseIniciar = false
    private var DescansoRodou = false // ver se precisa apagar!!
    private var resumeDescansoIsRuning = false  // Verifica se está executando a fase descanso (há 2 fases, a iniciar e a descanso)





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)
        binding = ActivityPomodoroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        binding.btIniciar.setOnClickListener {
            x = 1
            binding.txtCiclo.setText("Ciclo: $x")
            if (isTimerRunning==false){
                binding.btIniciar.setText("Parar")
                iniciar()
            } else{
                binding.btIniciar.setText("Iniciar")
                timer?.cancel()
                isTimerRunning = false
            }
        }


        binding.btPausar.setOnClickListener {

            if (isTimerRunning == true){
                binding.btPausar.setText("Retomar")
                pauseTimer()

            } else{
                if (faseIniciar==true){
                    binding.btPausar.setText("Pausar")
                    resumeIniciar()
                }
                else{
                    binding.btPausar.setText("Pausar")
                    resumeDescanso()
                }
            }
        }

        binding.btResetar.setOnClickListener {
            if (faseIniciar==true){
                timer?.cancel()
                iniciar()
            }
            else{
                resumeDescansoIsRuning = true
                timer?.cancel()
                descanso()
                resumeDescansoIsRuning = false //depois que executar o descanso, ele fica false pra poder executar o som novamente qnd clicar no "reiniciar"
            }
        }

        binding.btPersonalizado.setOnClickListener {
            timer?.cancel()
            val intent = Intent(this, Pomodoro_Personalizado::class.java)
            startActivity(intent)
        }
    }



    private fun iniciar() {

        desabilitarBtIniciar()

        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Concentre")
        txtStatusAtividade.setTextColor(Color.RED)

        var exibicao = binding.txTempo

        timeLeft = 3000
        //timeLeft = (25 *60000) // usar qnd tudo tiver ok
        timer = object : CountDownTimer(timeLeft, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                faseIniciar = true
                timeLeft = millisUntilFinished
                //time's format
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeLeft)
                timeRemaining = timeLeft
                exibicao.setText(formated) // Atualizar a interface do usuário aqui, como mostrando o tempo restante
                println("infoa"+timeRemaining) //apagar depois

            }
            override fun onFinish() {
                faseIniciar = false
                descanso()
            }
        }.start()
        isTimerRunning = true
    }


    private fun descanso(){
        DescansoRodou = false

        Thread.sleep(100)
        faseIniciar = false
        if (resumeDescansoIsRuning == false){ //não vai tocar o sino quando executar o resumeDescanso
            val player = MediaPlayer.create(applicationContext,R.raw.cartoon_cowbell).start()
        }

        desabilitarBtIniciar()

        if (x==4){
            timeLeft = 5000 +10
        } else{
            timeLeft = (2000) +10
        }



        var exibicao = binding.txTempo

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

                exibicao.text = "Fim" // Executado quando o temporizador chega a zero
                habilitarBtIniciar()
                binding.btIniciar.setText("Reiniciar")
                DescansoRodou = true


                if (x <= repeticaoCiclo -1){
                    iniciar()
                    x++
                    binding.txtCiclo.setText("Ciclo: $x")
                }
            }
        }.start()
        isTimerRunning = true

    }

    private fun pauseTimer(){
        timer?.cancel()
        isTimerRunning = false
    }


    private fun resumeIniciar(){
        faseIniciar = true
        var exibicao = binding.txTempo

        timer = object : CountDownTimer(timeRemaining, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeRemaining)
                exibicao.setText(formated)
                println("infoaA"+timeRemaining)
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

        timer = object : CountDownTimer(timeRemaining, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeRemaining)
                exibicao.setText(formated)
            }
            override fun onFinish() {
                exibicao.text = "Fim" // Executado quando o temporizador chega a zero
                habilitarBtIniciar()
                binding.btIniciar.setText("Reiniciar")
                resumeDescansoIsRuning = false
            }
        }.start()
        isTimerRunning = true
    }




    private fun desabilitarBtIniciar(){
        binding.btIniciar.isEnabled = false //desativa o botão iniciar
        binding.btIniciar.setTextColor(Color.GRAY)
        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Descanse")
        txtStatusAtividade.setTextColor(Color.BLUE)
    }

    private fun habilitarBtIniciar(){
        binding.btIniciar.isEnabled = true //ativa o botão iniciar no final da execução
        binding.btIniciar.setTextColor(Color.WHITE)
        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.text = ""
    }
}