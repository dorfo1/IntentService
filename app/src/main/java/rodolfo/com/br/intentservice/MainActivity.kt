package rodolfo.com.br.intentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe





class MainActivity : AppCompatActivity() {

    val receiver = ResponseReceiver()
    val receiver2 = PowerReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this,MinhaIntentService::class.java)
        intent.putExtra(MinhaIntentService.PARAM_ENTRADA,"Agora É:")
        startService(intent)
        containerLoading.visibility = View.VISIBLE
        registrarReciver()
    }

    private fun registrarReciver() {
        val filter = IntentFilter(MinhaIntentService.ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(receiver,filter)


        val filter2 = IntentFilter()
        filter2.addAction(Intent.ACTION_POWER_CONNECTED)
        filter2.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter2.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(receiver2,filter2)


    }

    private fun atualizarTexto(texto:String) {
        tvresultado.setText(texto)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Status) {
       when(event){
           Status.ERROR ->{
               containerLoading.visibility = View.GONE
           }
           Status.LOADING ->{
                containerLoading.visibility = View.VISIBLE
           }
           Status.SUCCESS ->{
               containerLoading.visibility = View.GONE

           }
       }
    }

    inner class PowerReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                Intent.ACTION_POWER_CONNECTED -> {
                    Toast.makeText(context,"CONNECTADO",Toast.LENGTH_LONG).show()
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Toast.makeText(context,"DESCONNECTADO",Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    inner class ResponseReceiver : BroadcastReceiver(){

        override fun onReceive(context: Context?, intent: Intent?) {
                atualizarTexto(intent?.getStringExtra(MinhaIntentService.PARAM_SAIDA)!!);
        }

    }

}
