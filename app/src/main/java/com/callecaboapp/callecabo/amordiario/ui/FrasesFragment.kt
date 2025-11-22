package com.callecaboapp.callecabo.amordiario.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.callecaboapp.callecabo.amordiario.Alarma
import com.callecaboapp.callecabo.amordiario.BuildConfig
import com.callecaboapp.callecabo.amordiario.HandlerSQLite
import com.callecaboapp.callecabo.amordiario.R
import com.callecaboapp.callecabo.amordiario.databinding.FragmentFrasesBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.time.LocalDateTime

class FrasesFragment : Fragment() {

    private val lanzadorPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        val mensaje = when {
            isGranted -> ""
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> "Acepta el permiso para recordatorios de chistes nuevos"
            else -> ""
        }

        if (mensaje.isNotEmpty()) Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
    }

    private var _binding: FragmentFrasesBinding? = null
    private val binding get() = _binding!!

    private var mInterstitialAd: InterstitialAd? = null
    private var voy = 0
    private var disponibles = 10
    private var dia = 0
    private var mes = 0
    private var ano = 0
    private val frases: Array<String> = arrayOf(
        "Eres mi sol en días grises, mi luna en noches oscuras.",
        "Contigo todo tiene sentido, cada latido, cada sonrisa, cada mirada.",
        "Te quiero como se quiere a las cosas ciertas, con el alma llena y la vida en paz.",
        "Eres mi refugio, mi hogar, mi paz. Eres todo lo que siempre he buscado.",
        "Juntos somos invencibles, un equipo perfecto, una melodía armoniosa.",
        "Tu amor es mi brújula, me guía hacia lo mejor de mí mismo.",
        "Eres la luz que ilumina mi camino, la fuerza que me impulsa a seguir adelante.",
        "Me pierdo en tus ojos y encuentro un universo de amor infinito.",
        "Eres mi confidente, mi mejor amigo, la persona con la que puedo ser yo mismo.",
        "Te amo más que a las palabras, más que a los sueños, más que a la vida misma.",
        "Eres mi dulce melodía, la canción que llena mi corazón de alegría.",
        "Contigo el tiempo se detiene, el mundo se desvanece, solo existimos tú y yo.",
        "Eres mi inspiración, la musa que me hace crear, la artista que pinta mi alma.",
        "Feliz Día de San Valentín, mi amor. Eres mi regalo más preciado, mi tesoro invaluable.",
        "Me completas, me haces sentir completo, como si dos piezas de un rompecabezas se unieran.",
        "Eres mi refugio en la tormenta, mi calma en la tempestad, mi paz en el caos.",
        "Te amo con cada respiro, con cada latido, con cada gota de mi sangre.",
        "Eres la razón por la que sonrío, la causa de mi felicidad, el motivo de mi existir.",
        "A tu lado soy mejor persona, más fuerte, más feliz, más yo mismo.",
        "Te quiero hoy, mañana y siempre, hasta el infinito y más allá.",
        "Eres mi primavera, la flor que florece en mi corazón, la estación que me llena de vida.",
        "Contigo puedo ser yo mismo, sin máscaras ni pretensiones, solo mi esencia más pura.",
        "Eres mi fortaleza, mi apoyo incondicional, la mano que me sostiene en los momentos difíciles.",
        "Te amo más allá de las palabras, con un amor que solo mi corazón puede comprender.",
        "Eres mi hogar, mi lugar seguro, el espacio donde puedo descansar mi alma.",
        "Me haces sentir especial, único, como si fuera la única persona en el mundo para ti.",
        "Eres mi todo, mi presente, mi futuro, mi razón de ser.",
        "Te amo con la intensidad del fuego, con la pasión del mar, con la locura de la vida.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más.",
        "Eres mi sonrisa más sincera, la carcajada más espontánea, la alegría más pura.",
        "Contigo la vida es una aventura emocionante, un viaje lleno de sorpresas y felicidad.",
        "Eres mi maestro, mi guía, la persona que me enseña el verdadero significado del amor.",
        "Te amo con la locura del primer amor, con la intensidad de la pasión eterna.",
        "Eres mi compañero de aventuras, mi confidente, la persona con la que puedo compartir mis sueños.",
        "Me haces sentir vivo, me llenas de energía, me impulsas a ser la mejor versión de mí mismo.",
        "Eres mi ángel de la guarda, la persona que me protege del mal y me llena de luz.",
        "Te amo con cada fibra de mi ser, con cada célula de mi cuerpo, con cada latido de mi corazón.",
        "Eres mi inspiración, mi musa, la artista que pinta mi vida con colores vibrantes.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis sueños y mis miedos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor.",
        "Eres mi todo, mi presente, mi futuro, mi razón de ser.",
        "Me haces sentir amado, valorado, apreciado, como si fuera lo más importante para ti.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que me conoce mejor que nadie.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más.",
        "Eres mi rayo de sol en un día nublado, mi sonrisa después de la lluvia, mi alegría en la tristeza.",
        "Contigo la vida es una fiesta, una danza constante de felicidad y amor.",
        "Eres mi maestro, mi guía, la persona que me enseña el verdadero significado de la vida.",
        "Te amo con la locura del primer amor, con la intensidad de la pasión eterna.",
        "Eres mi compañero de vida, mi alma gemela, la persona con la que quiero compartir el resto de mis días.",
        "Me haces sentir vivo, me llenas de energía, me impulsas a ser la mejor versión de mí mismo.",
        "Eres mi ángel de la guarda, la persona que me protege del mal y me llena de luz.",
        "Te amo con cada fibra de mi ser, con cada célula de mi cuerpo, con cada latido de mi corazón.",
        "Eres mi inspiración, mi musa, la artista que pinta mi vida con colores vibrantes.",
        "Te quiero hoy, mañana y siempre, hasta que las estrellas se apaguen y el sol deje de brillar.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis sueños y mis miedos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor.",
        "Eres mi todo, mi presente, mi futuro, mi razón de ser.",
        "Me haces sentir amado, valorado, apreciado, como si fuera lo más importante para ti.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que me conoce mejor que nadie.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más.",
        "Eres mi oasis en el desierto, mi agua fresca en un día caluroso, mi sombra bajo el sol ardiente.",
        "Contigo puedo ser yo mismo sin miedo al juicio, sin máscaras ni pretensiones.",
        "Eres mi maestro, mi guía, la persona que me enseña el verdadero significado de la libertad.",
        "Te amo con la locura del primer amor, con la intensidad de la pasión eterna.",
        "Eres mi compañero de aventuras, mi confidente, la persona con la que puedo compartir mis sueños más locos.",
        "Me haces sentir vivo, me llenas de energía, me impulsas a perseguir mis sueños más salvajes.",
        "Eres mi ángel de la guarda, la persona que me protege del mal y me llena de esperanza.",
        "Te amo con cada fibra de mi ser, con cada célula de mi cuerpo, con cada latido de mi corazón.",
        "Eres mi inspiración, mi musa, la artista que pinta mi vida con colores vibrantes y audaces.",
        "Te quiero hoy, mañana y siempre, hasta que las estrellas se apaguen y el universo deje de existir.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis triunfos y mis fracasos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito para enfrentar cualquier desafío.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor verdadero.",
        "Eres mi todo, mi presente, mi futuro, la razón por la que me levanto cada mañana con una sonrisa.",
        "Me haces sentir amado, valorado, apreciado, como si fuera un tesoro invaluable para ti.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que completa mi alma.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más, hasta el infinito y más allá.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis sueños y mis miedos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito para volar alto.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor que nos une.",
        "Eres mi todo, mi presente, mi futuro, la razón por la la que mi corazón late con fuerza.",
        "Me haces sentir amado, valorado, apreciado, como si fuera la única persona en el mundo para ti.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que me conoce mejor que nadie.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más, hasta que las estrellas se apaguen y el universo deje de existir.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis sueños y mis miedos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito para enfrentar cualquier obstáculo.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor que nos transforma.",
        "Eres mi todo, mi presente, mi futuro, la razón por la que cada día es una aventura.",
        "Me haces sentir amado, valorado, apreciado, como si fuera un tesoro invaluable para ti.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que completa mi alma.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más, hasta que las hojas de los árboles se marchiten y el viento deje de soplar.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos.",
        "Contigo puedo compartir mis alegrías y mis tristezas, mis logros y mis fracasos.",
        "Eres mi roca, mi pilar de fuerza, la persona que me da la seguridad que necesito para alcanzar mis metas.",
        "Te amo con la ternura de un beso, con la pasión de un abrazo, con la locura del amor que nos une en cuerpo y alma.",
        "Eres mi todo, mi presente, mi futuro, la razón por la que sonrío cada mañana al despertar.",
        "Me haces sentir amado, valorado, apreciado, como si fuera la persona más importante en tu vida.",
        "Eres mi mejor amigo, mi amante, mi confidente, la persona que me conoce mejor que nadie.",
        "Te amo con la intensidad del sol, con la profundidad del mar, con la eternidad del tiempo.",
        "Eres mi sueño hecho realidad, el deseo más profundo de mi corazón hecho tangible.",
        "Te quiero hoy más que ayer, y mañana te amaré aún más, hasta que la luna deje de brillar y las estrellas se apaguen.",
        "Eres mi brisa fresca en un día caluroso, mi refugio en la tormenta, mi paz en el caos."
    )
    private var total: Int = 0
    private lateinit var baseDatosSQL: HandlerSQLite
    private var publicaAnuncio = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFrasesBinding.inflate(inflater, container, false)

        baseDatosSQL = HandlerSQLite(requireContext())
        val db = baseDatosSQL.writableDatabase
        baseDatosSQL.inicia(db)

        if (Build.VERSION.SDK_INT >= 33)
            lanzadorPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)

        alarma()

        publicidad()

        total = frases.size
        nuevoChiste()

        actualizaContador()
        compruebaLimites()

        eventos()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        alarma()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun eventos() {
        binding.favoritos.setOnClickListener{

            val cadena = baseDatosSQL.select("indice = $voy")
            if (cadena == null) {
                baseDatosSQL.insertFavoritos(voy, frases[voy])
                binding.favoritos.setImageResource(R.drawable.corazonlleno)
                Toast.makeText(requireContext(), "Frase agregada a favoritos", Toast.LENGTH_SHORT).show()
            } else {
                baseDatosSQL.delete("indice = $voy")
                binding.favoritos.setImageResource(R.drawable.corazon)
                Toast.makeText(requireContext(), "Frase eliminada de favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.compartir.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, frases[voy])
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)


            /*val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val myClip = ClipData.newPlainText("text", frases[voy])
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show()*/
        }

        binding.anterior.setOnClickListener {
            if (voy > 0) {
                voy--
                visualizaFrase()
                compruebaLimites()
            }
        }

        binding.siguiente.setOnClickListener {
            if (voy < disponibles) {
                voy++
                visualizaFrase()
                compruebaLimites()
            }
        }
    }

    private fun nuevoChiste() {
        val hoy = LocalDateTime.now()

        dia = hoy.dayOfMonth
        mes = hoy.monthValue
        ano = hoy.year

        val settings = requireContext().getSharedPreferences("Preferencias", MODE_PRIVATE)
        disponibles = settings.getInt("disponible", 10)
        voy = disponibles
        val ultimodia = settings.getInt("dia", dia)
        val ultimomes = settings.getInt("mes", mes)
        val ultimoano = settings.getInt("ano", ano)
        val ultimaFecha = (ultimoano * 365 + (ultimomes * 31) + ultimodia).toFloat()
        val fecha = (ano * 365 + (mes * 31) + dia).toFloat()

        //Nueva cita
        if (fecha > ultimaFecha && disponibles + 1 < total) {
            val dialogo1 = AlertDialog.Builder(requireContext())
            dialogo1.setTitle("Atención")
            dialogo1.setMessage("Hay un nuevo chiste disponible, pero deberá ver un anuncio ¿Deseea desbloquearlo?")
            dialogo1.setPositiveButton("SI") { _: DialogInterface?, _: Int -> muestraFrase()  }
            dialogo1.setNegativeButton("CANCELAR") { dialog: DialogInterface, _: Int -> dialog.cancel() }
            dialogo1.create()
            dialogo1.show()
        }
    }

    private fun publicidad() {
        binding.adView.adUnitId = BuildConfig.UNIT_ID

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        InterstitialAd.load(
            requireContext(), BuildConfig.ID_ADMOB, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    if (publicaAnuncio) {
                        publicaAnuncio = false
                        mInterstitialAd!!.show(requireActivity())
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.d("MainActivity", loadAdError.toString())
                    mInterstitialAd = null
                }
            })

    }

    private fun visualizaFrase() {
        binding.frase.text = frases[voy]
        val cadena = "${voy +1}/${disponibles + 1}"
        binding.numero.text = cadena

        val favoritos = baseDatosSQL.select("indice = $voy")
        if (favoritos != null) {
            binding.favoritos.setImageResource(R.drawable.corazonlleno)
        } else {
            binding.favoritos.setImageResource(R.drawable.corazon)
        }
    }

    private fun actualizaContador() {
        visualizaFrase()
        val settings = requireContext().getSharedPreferences("Preferencias", MODE_PRIVATE)
        val editor = settings.edit()
        editor.putInt("disponible", disponibles)
        editor.putInt("dia", dia)
        editor.putInt("mes", mes)
        editor.putInt("ano", ano)
        editor.apply()
    }

    private fun alarma() {
        val sharedPreferences = requireContext().getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE)
        val tareaProgramada = sharedPreferences.getBoolean("tarea_programada", false)

        if (!tareaProgramada) {
            val am = requireContext().getSystemService(ALARM_SERVICE) as AlarmManager

            val i = Intent(requireContext(), Alarma::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                i,
                PendingIntent.FLAG_IMMUTABLE
            )

            am.cancel(pendingIntent)

            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()

            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis + (24*60*60*1000),  24* 60 * 60 * 1000 , pendingIntent)
        }

        val editor = sharedPreferences.edit()
        editor.putBoolean("tarea_programada", true)
        editor.apply()
    }


    private fun compruebaLimites() {
        binding.siguiente.isEnabled = voy < disponibles
        binding.anterior.isEnabled = voy > 0
    }

    private fun muestraFrase() {
        disponibles++
        voy++;
        publicaAnuncio = true
        if (mInterstitialAd != null) {
            publicaAnuncio = false
            mInterstitialAd!!.show(requireActivity())
        }

        actualizaContador()
        compruebaLimites()
        visualizaFrase()
    }

}