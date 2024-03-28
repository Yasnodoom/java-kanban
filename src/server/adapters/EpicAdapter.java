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
        Function<LocalDateTime, String> toStringOrNull = ldt -> ldt != null ? ldt.toString() : "null";

        jsonWriter.beginObject()
                .name("id").value(epic.getId())
                .name("name").value(epic.getName())
                .name("description").value(epic.getDescription())
                .name("status").value(epic.getStatus().toString())
                .name("subTask").value(epic.getSubTaskIDs().toString())
                .name("duration").value(epic.getDuration().toString())
                .name("startTime").value(toStringOrNull.apply(epic.getStartTime()))
                .endObject();
    }

    @Override
    public Epic read(JsonReader jsonReader) throws IOException {
        String name = String.valueOf(' ');
        String description = String.valueOf(' ');

        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String field = jsonReader.nextName();
            String value = jsonReader.nextString();
            if (field.equals("name")) {
                name = value;
            }
            if (field.equals("description")) {
                description = value;
            }
        }
        jsonReader.endObject();

        return new Epic(name, description);
    }
}
