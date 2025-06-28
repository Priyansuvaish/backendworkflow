package com.example.workflow.util;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;

public class validator {
    public static void validateJson(Map<String, Object> schemaMap, Map<String, Object> dataMap) throws ValidationException {
        JSONObject rawSchema = new JSONObject(schemaMap);
        JSONObject submission = new JSONObject(dataMap);
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(submission);
    }
}
