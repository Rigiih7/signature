package com.example.signature.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SignatureService {

    private static final String SIGNATURE = "signature";

    @Value("${general-configs.merchant.secret-key}")
    private String secretKey;

    public JSONObject generateSignature(Map<String, Object> requestPayload, String charset) throws Exception {
        JSONObject jsonObject = new JSONObject(requestPayload);
        jsonObject.remove(SIGNATURE); // Remove signature if it already exists
        String signature = getSignature(jsonObject, charset, secretKey);
        jsonObject.put(SIGNATURE, signature);
        return jsonObject;
    }

    private String getSignature(JSONObject data, String charset, String secretKey) throws Exception {
        data.remove(SIGNATURE);
        List<String> keyValueList = new ArrayList<>();
        propertyFilter(null, data, keyValueList);
        Collections.sort(keyValueList);
        String formatText = StringUtils.join(keyValueList, "&");
        String finalText = secretKey + "&" + formatText + "&" + secretKey;
        return sha256(finalText, charset);
    }

    private void propertyFilter(String key, Object value, List<String> list) {
        if (value instanceof JSONObject) {
            jsonObjectPropertyFilter(key, (JSONObject) value, list);
        } else if (value instanceof JSONArray) {
            jsonArrayPropertyFilter(key, (JSONArray) value, list);
        } else {
            if (key != null && value != null && !value.toString().isEmpty()) {
                list.add(key.trim() + "=" + value);
            }
        }
    }

    private void jsonObjectPropertyFilter(String key, JSONObject value, List<String> list) {
        for (String name : value.keySet()) {
            propertyFilter(name, value.get(name), list);
        }
    }

    private void jsonArrayPropertyFilter(String key, JSONArray value, List<String> list) {
        for (Object json : value) {
            propertyFilter(key, json, list);
        }
    }

    private String sha256(String value, String charset) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(value.getBytes(charset));
        byte[] bytes = md.digest();
        return Base64.getEncoder().encodeToString(bytes);
    }
}
