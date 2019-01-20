package com.nikolayzakharevich;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Keyboard {

    public static class Builder {
        private JsonObject keyboard = new JsonObject();
        private JsonArray buttons = new JsonArray();
        private JsonArray row = new JsonArray();
        private int nButton = 0;
        private int currentRow = 0;
        private int currentColumn = 0;

        Builder() {
            keyboard.addProperty("one_time", false);
        }

        public Builder addButton(String label, Color color) {
            return addButton(label, color, "{\"button\": \"" + (++nButton) + "\"}");
        }

        public Builder addButton(String label, Color color, String payload) {
            if (++currentColumn == 5) {
                throw new KeyboardException("Too many buttons in a row");
            }
            JsonObject button = new JsonObject();
            JsonObject action = new JsonObject();
            action.addProperty("type", "text");
            action.addProperty("payload", payload);
            action.addProperty("label", label);
            button.add("action", action);
            button.addProperty("color", color.toString());
            row.add(button);
            return this;
        }

        public Builder newRow() {
            if (++currentRow == 11) {
                throw new KeyboardException("Too many rows");
            }
            currentColumn = 0;
            buttons.add(row);
            row = new JsonArray();
            return this;
        }

        public Builder setOneTime(boolean useOnce) {
            keyboard.remove("one_time");
            keyboard.addProperty("one_time", useOnce);
            return this;
        }

        public String build() {
            if (row.size() > 0) {
                buttons.add(row);
            }
            keyboard.add("buttons", buttons);
            Gson gson = new Gson();
            return gson.toJson(keyboard);
        }
    }

    private Keyboard() {}



    public static Builder builder() {
        return new Builder();
    }
}
