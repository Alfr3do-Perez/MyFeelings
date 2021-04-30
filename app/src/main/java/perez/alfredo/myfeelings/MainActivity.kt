package perez.alfredo.myfeelings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import perez.alfredo.myfeelings.utilities.CustomBarDrawable
import perez.alfredo.myfeelings.utilities.CustomCircleDrawable
import perez.alfredo.myfeelings.utilities.Emociones
import perez.alfredo.myfeelings.utilities.JSONFile
import java.io.IOException

class MainActivity : AppCompatActivity() {
    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data:Boolean = false;
    var lista = ArrayList<Emociones>();

    var graphVeryHappy:View? = null;
    var graphHappy:View? = null;
    var graphNeutral:View? = null;
    var graphSad:View? = null;
    var graphVerySad:View? = null;
    var graph:ConstraintLayout? = null;
    var icon:ImageView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        graphVeryHappy = this.findViewById(R.id.graphVeryHappy) as View;
        graphHappy = this.findViewById(R.id.graphHappy) as View;
        graphNeutral = this.findViewById(R.id.graphNeutral) as View;
        graphSad = this.findViewById(R.id.graphSad) as View;
        graphVerySad = this.findViewById(R.id.graphVerySad) as View;
        graph = this.findViewById(R.id.graph) as ConstraintLayout;
        icon = this.findViewById(R.id.iconoCentral) as ImageView;

        jsonFile = JSONFile();
        fetchingData();
        if(!data){
            var emociones = ArrayList<Emociones>();
            val fondo = CustomCircleDrawable(this, emociones);
            graph?.background = fondo;
            graphVeryHappy?.background = CustomBarDrawable(this, Emociones("Muy Feliz", 0.0F, R.color.mustard, veryHappy));
            graphHappy?.background = CustomBarDrawable(this, Emociones("Feliz", 0.0F, R.color.orange, happy));
            graphNeutral?.background = CustomBarDrawable(this, Emociones("Neutral", 0.0F, R.color.greenie, neutral));
            graphSad?.background = CustomBarDrawable(this, Emociones("Triste", 0.0F, R.color.blue, sad));
            graphVerySad?.background = CustomBarDrawable(this, Emociones("Muy Feliz", 0.0F, R.color.deepblue, verySad));
        }else{
            actualizarGrafica();
            iconoMayoria();
        }

        val guardarButton:Button = this.findViewById(R.id.guardarButton) as Button;
        val veryHappyButton:ImageButton = this.findViewById(R.id.veryHappyButton) as ImageButton;
        val happyButton:ImageButton = this.findViewById(R.id.happyButton) as ImageButton;
        val neutralButton:ImageButton = this.findViewById(R.id.neutralButton) as ImageButton;
        val sadButton:ImageButton = this.findViewById(R.id.sadButton) as ImageButton;
        val verySadButton:ImageButton = this.findViewById(R.id.verySadButton) as ImageButton;

        guardarButton.setOnClickListener {
            guardar();
        }

        veryHappyButton.setOnClickListener {
            veryHappy++;
            iconoMayoria();
            actualizarGrafica();
        }

        happyButton.setOnClickListener {
            happy++;
            iconoMayoria();
            actualizarGrafica();
        }

        neutralButton.setOnClickListener {
            neutral++;
            iconoMayoria();
            actualizarGrafica();
        }

        sadButton.setOnClickListener {
            sad++;
            iconoMayoria();
            actualizarGrafica();
        }

        verySadButton.setOnClickListener {
            verySad++;
            iconoMayoria();
            actualizarGrafica();
        }

    }

    fun fetchingData(){
        try {
            var json:String = jsonFile?.getData(this) ?: "";
            if (json != ""){
                this.data = true;
                var jsonArray:JSONArray = JSONArray(json);

                this.lista = parseJson(jsonArray);

                for(i in lista){
                    when(i.nombre){
                        "Muy Feliz" -> veryHappy = i.total;
                        "Feliz" -> happy = i.total;
                        "Neutral" -> neutral = i.total;
                        "Triste" -> sad = i.total;
                        "Muy Triste" -> verySad = i.total;
                    }
                }
            }else{this.data = false;}

        }catch (e: JSONException){e.printStackTrace()}
    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones>{
        var lista = ArrayList<Emociones>();

        for(i in 0..jsonArray.length()){
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre");
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat();
                val color = jsonArray.getJSONObject(i).getInt("color");
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat();
                var emocion = Emociones(nombre, porcentaje, color, total);
                lista.add(emocion);
            }catch (e: JSONException){e.printStackTrace();}
        }
        return lista;
    }

    fun actualizarGrafica(){
        val total = veryHappy+happy+neutral+sad+veryHappy;

        var pVH:Float = (veryHappy *100/total).toFloat();
        var pH:Float = (happy *100/total).toFloat();
        var pN:Float = (neutral *100/total).toFloat();
        var pS:Float = (sad *100/total).toFloat();
        var pVS:Float = (verySad *100/total).toFloat();

        Log.d("porcentajes", "very happy: "+pVH);
        Log.d("porcentajes", "happy: "+pH);
        Log.d("porcentajes", "neutral: "+pN);
        Log.d("porcentajes", "sad: "+pS);
        Log.d("porcentajes", "very sad: "+pVS);

        lista.clear();
        lista.add(Emociones("Muy Feliz", pVH, R.color.mustard, veryHappy));
        lista.add(Emociones("Feliz", pH, R.color.orange, happy));
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral));
        lista.add(Emociones("Triste", pS, R.color.blue, sad));
        lista.add(Emociones("Muy Triste", pVS, R.color.deepblue, verySad));

        val fondo = CustomCircleDrawable(this, lista);

        graphVeryHappy?.background = CustomBarDrawable(this, Emociones("Muy Feliz", pVH, R.color.mustard, veryHappy));
        graphHappy?.background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy));
        graphNeutral?.background = CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral));
        graphSad?.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad));
        graphVerySad?.background = CustomBarDrawable(this, Emociones("Muy Feliz", pVS, R.color.deepblue, verySad));

        graph?.background = fondo

    }

    fun iconoMayoria(){
        if(veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy > verySad){
            icon?.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy));
        }
        if(happy>veryHappy && happy>neutral && happy>sad && happy>verySad){
            icon?.setImageDrawable(resources.getDrawable(R.drawable.ic_happy));
        }
        if(neutral>happy && neutral>veryHappy && neutral>sad && neutral > verySad){
            icon?.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral));
        }
        if(sad>happy && sad>veryHappy && sad>neutral && sad>verySad){
            icon?.setImageDrawable(resources.getDrawable(R.drawable.ic_sad));
        }
        if(verySad>happy && verySad>veryHappy && verySad>neutral && verySad>sad){
            icon?.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad));
        }

    }

    fun guardar(){
        var jsonArray = JSONArray();
        var o:Int =0;
        for(i in lista){
            Log.d("objetos", i.toString());
            var j:JSONObject = JSONObject();
            j.put("nombre",i.nombre);
            j.put("porcentaje", i.porcentaje);
            j.put("color", i.color);
            j.put("total", i.total);

            jsonArray.put(o,j);
            o++;
        }
        jsonFile?.saveData(this,jsonArray.toString());
        Toast.makeText(this, "Datos Guardados!", Toast.LENGTH_SHORT).show();
    }
}