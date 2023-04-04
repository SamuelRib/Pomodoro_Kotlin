package com.sam.cronometrokotlinnovo
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.sam.cronometrokotlinnovo.databinding.ActivityPomodoroBinding
import java.text.SimpleDateFormat
//"Quem controla seu tempo, domina seu futuro.



class Pomodoro : AppCompatActivity() {

    private lateinit var binding: ActivityPomodoroBinding


    private var timeLeft: Long = 0 //tempo inicial em milissegundos
    private val oneSecond = 1000L
    private var x = 1 //variavel para fazer loop com if
    private var repeticaoCiclo: Int = 4
    private var timeRemaining : Long = 0
    private var timer: CountDownTimer? = null
    private var isTimerRunning = false //Verifica se está em execução o pomodoro
    private var faseIniciar = false
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

        Thread.sleep(600)

        //timeLeft = 2000+100
        timeLeft = (25 *60000) // usar qnd tudo tiver ok
        timer = object : CountDownTimer(timeLeft, oneSecond) {
            override fun onTick(millisUntilFinished: Long) {
                faseIniciar = true
                timeLeft = millisUntilFinished
                //time's format
                val format = SimpleDateFormat("mm:ss")
                val formated = format.format(timeLeft)
                timeRemaining = timeLeft
                exibicao.setText(formated) // Atualizar a interface do usuário aqui, como mostrando o tempo restante


            }
            override fun onFinish() {
                faseIniciar = false
                descanso()
            }
        }.start()
        isTimerRunning = true
    }


    private fun descanso(){

        desabilitarBtIniciar()

        Thread.sleep(1000)
        faseIniciar = false
        if (resumeDescansoIsRuning == false){ //não vai tocar o sino quando executar o resumeDescanso
            val player = MediaPlayer.create(applicationContext,R.raw.cartoon_cowbell).start()
        }


        //descanso longo
        if (x==4){
            timeLeft = (25 *60000) +100 // se for o ciclo 4, fara um descanso de 25 min
        } else{
            timeLeft = (5 *60000) +100 // se for o ciclo < 4, fara um descanso de 5 min
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
                isTimerRunning = false


                if (x < repeticaoCiclo ){
                    iniciar()
                    x++
                    binding.txtCiclo.setText("Ciclo: $x")
                }

                if (isTimerRunning == false) {
                    binding.btIniciar.setText("Reiniciar")
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
                resumeDescansoIsRuning = false

                if (x < repeticaoCiclo ){
                    iniciar()
                    x++
                    binding.txtCiclo.setText("Ciclo: $x")
                }

                if (isTimerRunning == false) {
                    binding.btIniciar.setText("Reiniciar")
                }

            }
        }.start()
        isTimerRunning = true
    }




    private fun desabilitarBtIniciar(){

        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.setText("Descanse")
        txtStatusAtividade.setTextColor(Color.BLUE)
    }

    private fun habilitarBtIniciar(){

        val txtStatusAtividade = binding.txtStatusAtividade
        txtStatusAtividade.text = ""
    }
}