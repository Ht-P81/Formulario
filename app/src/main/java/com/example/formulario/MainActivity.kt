package com.example.formulario

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.formulario.databinding.ActivityMainBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //Declaramos una variable global
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // PARA QUE AL ESCRIBIR UN PAIS TE SALGA UNA LISTA CON LAS COINCIDENCIAS DE LO ESCRITO
        // creamos un array-arreglo con los posibles países
        val countries = arrayOf("Argentina", "Bolivia", "Chile", "Colombia", "Ecuador", "España",
            "Estados Unidos", "Mexico", "Panamá", "Perú", "Uruguay" )
        // ahora necesitamos un adaptador, clase especializada en mostrar datos (cualquier tipo)
        // creamos la variable instanciando ArrayAdapter con tres parámetros
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
        // se lo agregamos al actv, recueda en el activity_main se creo: androidx.appcompat.widget.AppCompatAutoCompleteTextView
        binding.actvPais.setAdapter(adaptador)

        //Vamos a detectar el evento de click, una vez se elija el país muestre el toast del pais correspondiente
        binding.actvPais.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, countries.get(i), Toast.LENGTH_SHORT).show()
            // Una vez seleccionado el país, para hacer que salte
            // al siguiente campo automáticamente debemos:
            binding.etLugarNacimiento.requestFocus()
        }

        //PARA MOSTRAR UN CALENDARIO EN LA FECHA DE NACIMIENTO
        // Alert dialog especializado en las fechas datepicker, se hace en el onCreate
        binding.etFechaNacimiento.setOnClickListener {
            // 1 Variable de materialDatepicker instanciada
            val builder = MaterialDatePicker.Builder.datePicker()
            // 2 Variable picket que carga la construcción del builder
            val picker = builder.build()
            // 4 Añadimos que los datos seleccionados en el calendario se agregen al et
            picker.addOnPositiveButtonClickListener { tiempoOrdenado ->
                val datStr = SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault()).apply {
                       //.apply {
                    //                        timeZone= TimeZone.getTimeZone("UTC")
                    //                }     esto sería necesario solo en el caso de que tuviera algún desfase la fecha
                    //en nuestro caso no sería necesario ponerlo, con lo que solo Locale.getDefault()).format(tiempoOrdenado) es suficiente
                        timeZone= TimeZone.getTimeZone("UTC")
                }.format(tiempoOrdenado)
                // El contexto es it y de tipo long, esto es pq data picker nos va a arrojar un numero en
                // formato long que representa la cantidad de miliseguendos desde la fecha seleccionada
                binding.etFechaNacimiento.setText(datStr)
                // como lo que muestra es un numero enorme, debemos dar formato a ese numero a formato fecha
                // para ello se crea al principio del metodo la variable datStr que se llama a la clase SimpleDateFormat
                // y se le pasa por parámetro el patrón de cómo queremos que muestre la fecha y a el local.get para la zona de ubicación
            }
            // 3 Metodo mostrar que nos pide dos parametros, un fragment y string,
            // Con esto conseguimos mostrar un calendario emergente en el et de fecha de nacimiento
            picker.show(supportFragmentManager,picker.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_send) {
            if (validar()) {
                //Mapeamos las variables de los edit text de las vistas
                val name: String = findViewById<TextInputEditText>(R.id.etNombre).text.toString()
                    .trim() //Metodo trim() para quitar espacios vacios innecesarios
                val surname = binding.etApellidos.text.toString().trim()
                val height = binding.etAltura.text.toString().trim()
                val fechaNacimiento = binding.etFechaNacimiento.text.toString().trim()
                val pais = binding.actvPais.text.toString().trim()
                val lugarNacimiento = binding.etLugarNacimiento.text.toString().trim()
                val notas = binding.etNotas.text.toString().trim()
                //Ventana emergente que muerta la información pasada por parámetros (Nombre y apellidos)
                Toast.makeText(this, "$name $surname", Toast.LENGTH_SHORT).show()

                //----------------------------------------------Alert Dialog--------------------------//
                var builder: AlertDialog.Builder = AlertDialog.Builder(this) //Creamos variable
                //Personalizamos el texto
                builder.setTitle(getString(R.string.dialog_title)) //Ponemos titulo
                builder.setMessage(joinData(name, surname, height, fechaNacimiento, pais, lugarNacimiento, notas)) //Pasamos lo que especificará la información

                builder.setPositiveButton(
                    getString(R.string.dialog_limpiar),
                    { dialogInterface, i ->
                        //Vamos a hacer que se reinicie y se limpie el formulario
                        //Para no repetir la misma línea con todos los componentes, aplicamos la función de alcance with
                        with(binding){
                            etNombre.text?.clear() //Podría estar vacío or null, por eso se le pone la ? para que no error de compilacion
                            etApellidos.text?.clear()
                            etAltura.text?.clear()
                            etFechaNacimiento.text?.clear()
                            actvPais.text?.clear()
                            etLugarNacimiento.text?.clear()
                            etNotas.text?.clear()
                        }
                    })

                builder.setNegativeButton(getString(R.string.dialog_cancelar), null)

                val dialog: AlertDialog = builder.create() //Lo creamos
                dialog.show() //Lo mostramos
                //------------------------------------------------------------------------------------//
            }
        }
        return super.onOptionsItemSelected(item)
    } //llave de cierre del onCreate

    //Función para mostrar el mensaje con todos los campos
    private fun joinData(vararg datos: String):String{
        var result = ""

        datos.forEach { dato->
            if(dato.isNotEmpty()){
                result += "$dato\n"
            }
        }
        return result
    }

    //Funcion de validar
    private fun validar(): Boolean {
        var isValid = true

        //VALIDACION DE ALTURA
        if (binding.etAltura.text.isNullOrEmpty()) {
            binding.tituloAltura.run {
                error = getString(R.string.help_required)
                //Independiente de donde este el cursor vuelva a colocarlo en el campo con el error
                requestFocus()
            }
            isValid = false
        } else {
            //Con esto sabes que el ERROR esta vacio del tituloALtura es null, por tanto la variabla tiene algun valor
            binding.tituloAltura.error = null
            //En esta variable guardamos que el texto, casteado a int sea inferior a 50 (da error) mayor a 50 altura validada
            val masde50=binding.etAltura.text.toString().toInt()
            if(masde50 < 50){
                binding.tituloAltura.run {
                    error = getString(R.string.help_alturaminima)
                    requestFocus()
                }
                Toast.makeText(this,"Altura insuficiente" , Toast.LENGTH_SHORT).show()
                isValid = false   //Validamos el tamaño minimo de 50
            }else{
                Toast.makeText(this,"Altura validada" , Toast.LENGTH_SHORT).show()
            }
        }

        //VALIDACIÓN DE APELLIDOS
        //Si el campo nombre está vacío o es nulo is valid es falso, por tanto no es valido y no muestra el alert dialog ni hace nada
        if (binding.etApellidos.text.isNullOrEmpty()) {
            binding.tituloApellidos.run {
                error = getString(R.string.help_required)
                //Independiente de donde este el cursor vuelva a colocarlo en el campo con el error
                requestFocus()
            }
            isValid = false
        } else {
            binding.tituloApellidos.error = null
        }

        //VALIDACION DE NOMBRE
        if (binding.etNombre.text.isNullOrEmpty()) {
            binding.tituloNombre.run {
                error = getString(R.string.help_required)
                requestFocus()
            }
            isValid = false
            //Sino, si no está vacío el campo, el error estará vacío y así no se queda en rojo el editText
        } else {
            binding.tituloNombre.error = null
        }
        return isValid
    }
}