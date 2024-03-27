package server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import task.Epic;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.function.Function;

public class EpicAdapter extends TypeAdapter<Epic> {
    @Override
    public void write(JsonWriter jsonWriter, Epic epic) throws IOException {
        Function<LocalDateTime, String> toString = ldt -> ldt != null ?  ldt.toString() : "null";

        jsonWriter.beginObject()
                .name("id").value(epic.getId())
                .name("name").value(epic.getName())
                .name("description").value(epic.getDescription())
                .name("status").value(epic.getStatus().toString())
                .name("subTask").value(epic.getSubTaskIDs().toString())
                .name("duration").value(epic.getDuration().toString())
                .name("startTime").value(toString.apply(epic.getStartTime()))
                .endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        return new Epic(jsonReader.nextString(), jsonReader.nextString());
    }
}
