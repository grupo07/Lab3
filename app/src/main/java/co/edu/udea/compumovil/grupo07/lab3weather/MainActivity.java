package co.edu.udea.compumovil.grupo07.lab3weather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static co.edu.udea.compumovil.grupo07.lab3weather.R.id.ciudad;

public class MainActivity extends AppCompatActivity {

    private String message;
    private Ciudad currentCiudad;
    private AutoCompleteTextView search;
    private TextView city, temperature, humidity, description;
    private ImageView imClima;
    private boolean ciudadExistente;
    private List<String> ciudades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtener el estado de la variable guardada
        if(savedInstanceState!=null){
            ciudadExistente = savedInstanceState.getBoolean("State");
        }else{

        }

        //Capturar las vistas
        search = (AutoCompleteTextView) findViewById(R.id.buscar);
        city = (TextView) findViewById(ciudad);
        temperature = (TextView) findViewById(R.id.temperatura);
        humidity = (TextView) findViewById(R.id.humedad);
        description = (TextView) findViewById(R.id.descripcion);
        imClima = (ImageView) findViewById(R.id.clima_image);

        //Asignamos un adapter con la lista de las ciudades capitales al textview para la funcion de autocompletar
        ciudades = Arrays.asList(getResources().getStringArray(R.array.capital_cities));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, ciudades);
        search.setAdapter(adapter);
        search.setThreshold(2);

    }

    public void onClick(View v){
        String city = search.getText().toString();
        if(city.equals("")){
            //Validar si el texto entrado es vacio
            Toast.makeText(MainActivity.this,"Por favor ingrese una ciudad",Toast.LENGTH_LONG).show();
        }else {
            //Formatear el string de la ciudad en caso de que tenga espacios
            String cityFormated = formatCity(city);
            if (checkConnection()) {
                new HttpGetTask().execute(city, cityFormated);
            } else {
                Toast.makeText(this, "Verifique su conexión a internet", Toast.LENGTH_LONG).show();
            }
            search.setText("");
        }

        //Esconder el teclado virtual cuando se de click en el icono para buscar
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private String formatCity(String city) {
        //Formatear el string de la ciudad para cuando contenga mas de una palabra
        StringTokenizer tokenizer = new StringTokenizer(city);
        String responseCity=tokenizer.nextToken();
        while(tokenizer.hasMoreTokens()){
            responseCity+="%20"+tokenizer.nextToken();
        }
        return responseCity;
    }

    private class HttpGetTask extends AsyncTask<String, Void, Ciudad> {


        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        private String data;

        @Override
        protected void onPreExecute() {
            //Start Progress Dialog (Message)
            Dialog.setMessage("Cargando información del clima");
            Dialog.show();
        }

        @Override
        protected Ciudad doInBackground(String... params) {
            ClienteHttp client = new ClienteHttp();

            //Obtener los parametros
            String cityName=params[0];
            String cityURL = params[1];

            //Obtener el lenguaje para la consulta
            String language = getResources().getString(R.string.idioma);

            //Traer los datos del clima
            data = client.getJSONData(cityURL,language);

            //Convertir JSON a modelo de objetos Java
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Clima.class,new ClimaDeserialize());
            Gson gson = gsonBuilder.create();
            Clima currentWeather=null;
            try {
                currentWeather = gson.fromJson(data, Clima.class);
            }catch(com.google.gson.JsonSyntaxException ex){
                message = "Verifique su conexión a Internet";
            }
            if(currentWeather==null){
                message = "Ciudad invalida";
                return null;

            }else {
                publishProgress();

                //Descargar la imagen
                byte[] b = client.downloadImage(currentWeather.getIcono());
                currentWeather.setImagenClima(b);
                Ciudad ciudad = new Ciudad();
                ciudad.setName(cityName);
                ciudad.setClima(currentWeather);
                return ciudad;
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Dialog.setMessage("Descargando Imagen");
        }

        @Override
        protected void onPostExecute(Ciudad ciudad) {

            // Close progress dialog
            Dialog.dismiss();
            //Búsqueda exitosa: mostrar en pantalla el resultado
            if(ciudad!=null) {
                currentCiudad = ciudad;
                Clima clima = ciudad.getClima();
                city.setText(ciudad.getName());
                temperature.setText(getResources().getString(R.string.temperatura)+""+String.valueOf(clima.getTemperatura())+"°");
                humidity.setText(getResources().getString(R.string.humedad)+""+String.valueOf(clima.getHumedad()));
                description.setText(getResources().getString(R.string.descripcion)+""+clima.getDescripcion());

                byte[] imgWeather = clima.getImagenClima();
                Bitmap bitmapWeather = BitmapFactory.decodeByteArray(imgWeather, 0, imgWeather.length);
                imClima.setImageBitmap(bitmapWeather);
                ciudadExistente =true;
            }else{
                //Busqueda no exitosa: Mostrar una notificacion toast con el mensaje respectivo
                Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                ciudadExistente = false;
                clearScreen();
            }

        }
    }

    /*Chequear la conexión a Internet*/
    private boolean checkConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    /*Guardar el estado del clima para cuando se gire la pantalla*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(currentCiudad !=null) {
            outState.putString("Ciudad", new Gson().toJson(currentCiudad));
            outState.putBoolean("Estado", ciudadExistente);
        }else{

        }
    }

    /*Restaurar el estado del clima*/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String jsonMyObject;
        if (savedInstanceState != null) {
            boolean isAvailable = savedInstanceState.getBoolean("State");
            if(isAvailable) {
                jsonMyObject = savedInstanceState.getString("City");
                currentCiudad = new Gson().fromJson(jsonMyObject, Ciudad.class);
                Clima clima = currentCiudad.getClima();
                city.setText(currentCiudad.getName());
                temperature.setText(getResources().getString(R.string.temperatura) + "" + String.valueOf(clima.getTemperatura()) + "°");
                humidity.setText(getResources().getString(R.string.humedad) + "" + String.valueOf(clima.getHumedad()));
                description.setText(getResources().getString(R.string.descripcion) + "" + clima.getDescripcion());
                byte[] imgWeather = clima.getImagenClima();
                Bitmap bitmapWeather = BitmapFactory.decodeByteArray(imgWeather, 0, imgWeather.length);
                imClima.setImageBitmap(bitmapWeather);
            }
        }
    }

    /*Limpiar la pantalla*/
    private void clearScreen(){
        city.setText("");
        temperature.setText("");
        humidity.setText("");
        description.setText("");
        imClima.setImageBitmap(null);
    }
}