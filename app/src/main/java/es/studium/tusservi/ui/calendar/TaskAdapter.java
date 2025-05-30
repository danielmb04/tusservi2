package es.studium.tusservi.ui.calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.studium.tusservi.R;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private List<Task> taskList;
    private OnTaskCheckedChangeListener listener;
    private OnTaskClickListener onTaskClickListener;
    private OnTaskDeletedListener onTaskDeletedListener;


    public TaskAdapter(Context context, List<Task> taskList, OnTaskCheckedChangeListener listener,OnTaskClickListener clickListener, OnTaskDeletedListener onTaskDeletedListener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
        this.onTaskClickListener = clickListener;
        this.onTaskDeletedListener = onTaskDeletedListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(v);
    }
public interface OnTaskDeletedListener {
        void onTaskDeleted();
}
    public interface OnTaskClickListener {
        void onTaskClick(Task task, int adapterPosition);
    }

    public interface OnTaskCheckedChangeListener {
        void onTaskCheckedChanged(Task task, boolean isChecked);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position)
    {

        Task task = taskList.get(position);

        holder.checkBox.setChecked(task.isDone());
        holder.textDescription.setText(task.getDescription());
        holder.textHours.setText(task.getStartTime() + " - " + task.getEndTime());

        // Listener para el checkbox
        holder.checkBox.setOnCheckedChangeListener(null); // evitar triggers múltiples por reciclado

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCheckedChanged(task, isChecked);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            mostrarDialogoEliminar(task, holder.getAdapterPosition());
            return true;
        });
        holder.itemView.setOnClickListener(v -> {
            if (onTaskClickListener != null) {
                onTaskClickListener.onTaskClick(task, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textDescription;
        TextView textHours;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBoxDone);
            textDescription = itemView.findViewById(R.id.textViewDescription);
            textHours = itemView.findViewById(R.id.textViewHours);
        }
    }
    private void mostrarDialogoEliminar(Task task, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    eliminarTarea(task, position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void eliminarTarea(Task task, int position) {
        String url = "http://10.0.2.2/TUSSERVI/borrarTarea.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (position >= 0 && position < taskList.size()) {
                        taskList.remove(position);
                        notifyItemRemoved(position);
                    }

                    if (onTaskDeletedListener != null) {
                        onTaskDeletedListener.onTaskDeleted();
                    }
                },
                error -> {
                    Toast.makeText(context, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idTarea", String.valueOf(task.getId()));
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }



}
