package com.nikolayzakharevich;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class Keyboard {

    static class Builder {
        private JsonObject keyboard = new JsonObject();
        private JsonArray buttons = new JsonArray();
        private JsonArray row = new JsonArray();
        private int nButton = 0;
        private int currentRow = 0;
        private int currentColumn = 0;

        Builder() {
            keyboard.addProperty("one_time", false);
        }

        Builder addLabel(Pair<String, Color> label) {
            if (++currentColumn == 11) {
                throw new KeyboardException("Too many buttons in a row");
            }
            JsonObject button = new JsonObject();
            JsonObject action = new JsonObject();
            action.addProperty("type", "text");
            action.addProperty("payload", "{\"button\": \"" + (++nButton) + "\"}");
            action.addProperty("label", label.getKey());
            button.add("action", action);
            button.addProperty("color", label.getValue().toString());
            row.add(button);
            return this;
        }

        Builder newRow() {
            if (++currentRow == 5) {
                throw new KeyboardException("Too many rows");
            }
            buttons.add(row);
            row = new JsonArray();
            return this;
        }

        Builder setOneTime(boolean useOnce) {
            keyboard.remove("one_time");
            keyboard.addProperty("one_time", useOnce);
            return this;
        }

        String build() {
            if (row.size() > 0) {
                buttons.add(row);
            }
            keyboard.add("buttons", buttons);
            Gson gson = new Gson();
            return gson.toJson(keyboard);
        }
    }

    private Keyboard() {}



    static Builder builder() {
        return new Builder();
    }
}
