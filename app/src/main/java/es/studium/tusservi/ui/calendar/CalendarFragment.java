package es.studium.tusservi.ui.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.studium.tusservi.R;
import es.studium.tusservi.databinding.FragmentCalendarBinding;
import es.studium.tusservi.ui.calendar.TaskAdapter;

public class CalendarFragment extends Fragment {
Button btn;
    private FragmentCalendarBinding binding;
    private List<Task> tasks = new ArrayList<>();
    private TaskAdapter adapter;

    private long selectedDate;

    private RequestQueue requestQueue;

    private final String URL_CARGAR_TAREAS = "http://10.0.2.2/TUSSERVI/obtenerTareasPorFecha.php";
    private final String URL_ACTUALIZAR_TAREA = "http://10.0.2.2/TUSSERVI/actualizarTarea.php";
    private final String URL_INSERTAR_TAREA = "http://10.0.2.2/TUSSERVI/insertarTarea.php";
    private final String URL_MODIFICAR_TAREA = "http://10.0.2.2/TUSSERVI/modificarTarea.php";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCalendarBinding.inflate(inflater, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());

        adapter = new TaskAdapter(requireContext(), tasks,
                (task, isChecked) -> {
                    task.setDone(isChecked);
                    actualizarTarea(task);
                },
                (task, position) -> {
                    mostrarDialogoEditarTarea(task);
                },
                () -> {
                    loadTasksForDate(selectedDate);
                }
        );





        binding.recyclerViewTasks.setAdapter(adapter);
        binding.recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedDate = getDateWithoutTime(System.currentTimeMillis());
        loadTasksForDate(selectedDate);

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = getDateWithoutTime(year, month, dayOfMonth);
            loadTasksForDate(selectedDate);
        });

        binding.btnCrearTarea.setOnClickListener(v -> mostrarDialogoAgregarTarea());

        return binding.getRoot();
    }

    private void mostrarDialogoAgregarTarea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nueva tarea");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crear_tarea, null);

        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        TimePicker timePickerInicio = dialogView.findViewById(R.id.timePickerInicio);
        TimePicker timePickerFin = dialogView.findViewById(R.id.timePickerFin);

        // Configura TimePickers a 24h
        timePickerInicio.setIs24HourView(true);
        timePickerFin.setIs24HourView(true);

        builder.setView(dialogView);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String descripcion = etDescripcion.getText().toString().trim();

            int horaInicio = timePickerInicio.getHour();
            int minutoInicio = timePickerInicio.getMinute();

            int horaFin = timePickerFin.getHour();
            int minutoFin = timePickerFin.getMinute();

            if (descripcion.isEmpty()) {
                Toast.makeText(getContext(), "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                return;
            }

            if (horaFin < horaInicio || (horaFin == horaInicio && minutoFin <= minutoInicio)) {
                Toast.makeText(getContext(), "La hora de fin debe ser después de la hora de inicio", Toast.LENGTH_SHORT).show();
                return;
            }
            long idUsuario = 0;
            try {
                idUsuario = Long.parseLong(obtenerIdUsuarioDesdePreferencias());
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "ID de usuario inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            String fechaCreacion = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate);

            String horaInicioStr = String.format(Locale.getDefault(), "%02d:%02d", horaInicio, minutoInicio);
            String horaFinStr = String.format(Locale.getDefault(), "%02d:%02d", horaFin, minutoFin);

            insertarTarea(idUsuario, descripcion, fechaCreacion, horaInicioStr, horaFinStr, false);

        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }



    public void insertarTarea(long idUsuario, String descripcion, String fechaCreacion, String horaInicio, String horaFin, boolean realizada) {
        String url = URL_INSERTAR_TAREA;  // Usar la constante definida

        int realizadaInt = realizada ? 1 : 0;

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                boolean success = json.getBoolean("success");
                if (success) {
                    Toast.makeText(requireContext(), "Tarea creada!", Toast.LENGTH_SHORT).show();
                    loadTasksForDate(selectedDate);
                    // Crear objeto Task y programar notificación
                    Task nuevaTarea = new Task(
                            idUsuario, // Puedes usar un ID temporal o ajustar si lo devuelve el servidor
                            selectedDate,
                            descripcion,
                            false,
                            horaInicio,
                            horaFin
                    );
                    programarNotificacion(nuevaTarea);

                } else {
                    Toast.makeText(requireContext(), "Error al crear tarea", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
            Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idUsuario", String.valueOf(idUsuario));
                params.put("descripcion", descripcion);
                params.put("fechaCreacion", fechaCreacion);
                params.put("horaInicio", horaInicio);
                params.put("horaFin", horaFin);
                params.put("realizada", String.valueOf(realizadaInt));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }



    private String obtenerIdProfesionalDesdePreferencias() {
        return requireContext()
                .getSharedPreferences("session", getContext().MODE_PRIVATE)
                .getString("idProfesional", "");
        // Devuelve "" si no existe
    }
    private String obtenerIdUsuarioDesdePreferencias() {
        SharedPreferences prefs = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String tipo = prefs.getString("tipo_usuario", "");
        Log.d("DEBUG", "Tipo usuario: " + tipo);

        String id = "";
        if ("profesional".equals(tipo)) {
            id = prefs.getString("idProfesional", "");
        } else if ("cliente".equals(tipo)) {
            id = prefs.getString("idUsuario", "");
        }
        Log.d("DEBUG", "ID obtenido: " + id);
        return id;
    }




    private void loadTasksForDate(long dateMillis) {
        tasks.clear();
        adapter.notifyDataSetChanged();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaFormateada = sdf.format(dateMillis);

        JSONObject jsonBody = new JSONObject();
        try {
            String idUsuario = obtenerIdUsuarioDesdePreferencias();
            Log.d("CalendarFragment", "ID usuario enviado: " + idUsuario);
            jsonBody.put("idUsuario", obtenerIdUsuarioDesdePreferencias()); // Pon aquí tu idUsuario real
            jsonBody.put("fecha", fechaFormateada);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_CARGAR_TAREAS, jsonBody,
                response -> {
                    Log.d("CalendarFragment", "Respuesta JSON: " + response.toString());
                    try {
                        if (response.has("error")) {
                            Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray tareasArray = response.getJSONArray("tareas");

                        tasks.clear();
                        for (int i = 0; i < tareasArray.length(); i++) {
                            JSONObject tareaObj = tareasArray.getJSONObject(i);

                            int id = tareaObj.getInt("idTarea");
                            long fechaTareaMillis = dateMillis;
                            String descripcion = tareaObj.getString("descripcion");
                            boolean done = tareaObj.getInt("realizada") == 1;
                            String startTime = tareaObj.getString("horaInicio");
                            String endTime = tareaObj.getString("horaFin");


                            tasks.add(new Task(id, fechaTareaMillis, descripcion, done, startTime, endTime));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parseando tareas", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("Volley", "Error cargando tareas: " + error.toString());
                    Toast.makeText(getContext(), "Error cargando tareas", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    public void actualizarTarea(Task task) {
        actualizarTarea(task.getId(), task.getDescription(), task.isDone(), task.getStartTime(), task.getEndTime());
    }
    public void actualizarTarea(long idTarea, String descripcion, boolean realizada, String horaInicio, String horaFin) {
        String url = URL_ACTUALIZAR_TAREA;

        int realizadaInt = realizada ? 1 : 0;

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                boolean success = json.getBoolean("success");
                if (success) {
                    Toast.makeText(requireContext(), "Tarea actualizada!", Toast.LENGTH_SHORT).show();
                    loadTasksForDate(selectedDate);
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
            Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idTarea", String.valueOf(idTarea));
                params.put("descripcion", descripcion);
                params.put("realizada", String.valueOf(realizadaInt));
                params.put("horaInicio", horaInicio);
                params.put("horaFin", horaFin);
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(request);
    }



    private long getDateWithoutTime(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    private long getDateWithoutTime(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void programarNotificacion(Task tarea) {
        String idUsuarioStr = obtenerIdUsuarioDesdePreferencias();
        int idUsuario = 0;
        try {
            idUsuario = Integer.parseInt(idUsuarioStr);
        } catch (NumberFormatException e) {
            Log.e("Notificación", "ID de usuario inválido: " + idUsuarioStr);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tarea.getDate());
        String[] horaSplit = tarea.getStartTime().split(":");
        int hora = Integer.parseInt(horaSplit[0]);
        int minuto = Integer.parseInt(horaSplit[1]);

        calendar.set(Calendar.HOUR_OF_DAY, hora);
        calendar.set(Calendar.MINUTE, minuto);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

// Restar una hora
        calendar.add(Calendar.HOUR_OF_DAY, -1);

        Intent intent = new Intent(requireContext(), NotificacionReceiver.class);
        intent.putExtra("descripcion", tarea.getDescription());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                (int) tarea.getId(), // ID único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void mostrarDialogoEditarTarea(Task tarea) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Editar tarea");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_crear_tarea, null);

        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        TimePicker timePickerInicio = dialogView.findViewById(R.id.timePickerInicio);
        TimePicker timePickerFin = dialogView.findViewById(R.id.timePickerFin);

        timePickerInicio.setIs24HourView(true);
        timePickerFin.setIs24HourView(true);

        // Rellenar con datos actuales
        etDescripcion.setText(tarea.getDescription());

        String[] startParts = tarea.getStartTime().split(":");
        timePickerInicio.setHour(Integer.parseInt(startParts[0]));
        timePickerInicio.setMinute(Integer.parseInt(startParts[1]));

        String[] endParts = tarea.getEndTime().split(":");
        timePickerFin.setHour(Integer.parseInt(endParts[0]));
        timePickerFin.setMinute(Integer.parseInt(endParts[1]));

        builder.setView(dialogView);

        builder.setPositiveButton("Guardar", null); // lo manejamos manualmente más abajo
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button btnGuardar = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnGuardar.setOnClickListener(view -> {
                String descripcion = etDescripcion.getText().toString().trim();
                int horaInicio = timePickerInicio.getHour();
                int minutoInicio = timePickerInicio.getMinute();
                int horaFin = timePickerFin.getHour();
                int minutoFin = timePickerFin.getMinute();

                if (descripcion.isEmpty()) {
                    Toast.makeText(getContext(), "La descripción no puede estar vacía", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (horaFin < horaInicio || (horaFin == horaInicio && minutoFin <= minutoInicio)) {
                    Toast.makeText(getContext(), "La hora de fin debe ser después de la hora de inicio", Toast.LENGTH_SHORT).show();
                    return;
                }

                String horaInicioStr = String.format(Locale.getDefault(), "%02d:%02d:00", horaInicio, minutoInicio);
                String horaFinStr = String.format(Locale.getDefault(), "%02d:%02d:00", horaFin, minutoFin);

                // Llamada HTTP para actualizar la tarea
                RequestQueue queue = Volley.newRequestQueue(requireContext());
                StringRequest request = new StringRequest(Request.Method.POST, URL_MODIFICAR_TAREA,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.getBoolean("success")) {
                                    Toast.makeText(getContext(), "Tarea actualizada correctamente", Toast.LENGTH_SHORT).show();
                                    loadTasksForDate(selectedDate);
                                    dialog.dismiss();
                                    // Aquí puedes actualizar la lista si es necesario
                                } else {
                                    Toast.makeText(getContext(), "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Error JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            Toast.makeText(getContext(), "Error de red: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("idTarea", String.valueOf(tarea.getId()));
                        params.put("descripcion", descripcion);
                        params.put("realizada", String.valueOf(tarea.isDone())); // o usa una checkbox si quieres modificarla
                        params.put("horaInicio", horaInicioStr);
                        params.put("horaFin", horaFinStr);
                        return params;
                    }
                };

                queue.add(request);
            });
        });

        dialog.show();
    }



}