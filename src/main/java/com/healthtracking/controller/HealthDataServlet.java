package com.healthtracking.controller;

import com.healthtracking.dao.HealthDataDAO;
import com.healthtracking.model.HealthData;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthDataServlet extends HttpServlet {
    private HealthDataDAO healthDataDAO = new HealthDataDAO();
    private Gson gson = new Gson();

    private Integer getUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Integer) session.getAttribute("userId");
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserId(request);
        if (userId == null) {
            result.put("success", false);
            result.put("message", "Not logged in");
            out.print(gson.toJson(result));
            return;
        }

        try {
            String pathInfo = request.getPathInfo();

            // If no path info, return all records
            if (pathInfo == null || pathInfo.equals("/")) {
                List<HealthData> dataList = healthDataDAO.getUserHealthData(userId);
                result.put("success", true);
                result.put("data", dataList);
            } else {
                // Get single record by ID
                String[] pathParts = pathInfo.split("/");
                if (pathParts.length == 2) {
                    int dataId = Integer.parseInt(pathParts[1]);
                    HealthData data = healthDataDAO.getHealthDataById(dataId);

                    if (data != null && data.getUserId() == userId) {
                        result.put("success", true);
                        result.put("data", data);
                    } else {
                        result.put("success", false);
                        result.put("message", "Record not found");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "Invalid request");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error fetching data");
        }

        out.print(gson.toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserId(request);
        if (userId == null) {
            result.put("success", false);
            result.put("message", "Not logged in");
            out.print(gson.toJson(result));
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            JsonObject jsonData = gson.fromJson(reader, JsonObject.class);

            HealthData healthData = new HealthData();
            healthData.setUserId(userId);
            healthData.setDate(Date.valueOf(jsonData.get("date").getAsString()));
            healthData.setWeight(jsonData.get("weight").getAsDouble());
            healthData.setHeight(jsonData.get("height").getAsDouble());
            healthData.setBloodPressureSystolic(jsonData.get("bpSystolic").getAsInt());
            healthData.setBloodPressureDiastolic(jsonData.get("bpDiastolic").getAsInt());
            healthData.setHeartRate(jsonData.get("heartRate").getAsInt());
            healthData.setSteps(jsonData.get("steps").getAsInt());
            healthData.setSleepHours(jsonData.get("sleepHours").getAsDouble());
            healthData.setNotes(jsonData.has("notes") ? jsonData.get("notes").getAsString() : "");

            boolean saved = healthDataDAO.addHealthData(healthData);

            if (saved) {
                result.put("success", true);
                result.put("message", "Health data saved");
            } else {
                result.put("success", false);
                result.put("message", "Failed to save");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserId(request);
        if (userId == null) {
            result.put("success", false);
            result.put("message", "Not logged in");
            out.print(gson.toJson(result));
            return;
        }

        try {
            // Get the ID from the URL
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.split("/").length != 2) {
                result.put("success", false);
                result.put("message", "Invalid request");
                out.print(gson.toJson(result));
                return;
            }

            int dataId = Integer.parseInt(pathInfo.split("/")[1]);

            // Read request body
            BufferedReader reader = request.getReader();
            JsonObject jsonData = gson.fromJson(reader, JsonObject.class);

            // First check if the record exists and belongs to this user
            HealthData existingData = healthDataDAO.getHealthDataById(dataId);
            if (existingData == null || existingData.getUserId() != userId) {
                result.put("success", false);
                result.put("message", "Record not found");
                out.print(gson.toJson(result));
                return;
            }

            // Update the data
            existingData.setDate(Date.valueOf(jsonData.get("date").getAsString()));
            existingData.setWeight(jsonData.get("weight").getAsDouble());
            existingData.setHeight(jsonData.get("height").getAsDouble());
            existingData.setBloodPressureSystolic(jsonData.get("bpSystolic").getAsInt());
            existingData.setBloodPressureDiastolic(jsonData.get("bpDiastolic").getAsInt());
            existingData.setHeartRate(jsonData.get("heartRate").getAsInt());
            existingData.setSteps(jsonData.get("steps").getAsInt());
            existingData.setSleepHours(jsonData.get("sleepHours").getAsDouble());
            existingData.setNotes(jsonData.has("notes") ? jsonData.get("notes").getAsString() : "");

            boolean updated = healthDataDAO.updateHealthData(existingData);

            if (updated) {
                result.put("success", true);
                result.put("message", "Health data updated");
            } else {
                result.put("success", false);
                result.put("message", "Failed to update");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        Integer userId = getUserId(request);
        if (userId == null) {
            result.put("success", false);
            result.put("message", "Not logged in");
            out.print(gson.toJson(result));
            return;
        }

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.split("/").length != 2) {
                result.put("success", false);
                result.put("message", "Invalid request");
                out.print(gson.toJson(result));
                return;
            }

            int dataId = Integer.parseInt(pathInfo.split("/")[1]);
            boolean deleted = healthDataDAO.deleteHealthData(dataId, userId);

            if (deleted) {
                result.put("success", true);
                result.put("message", "Deleted successfully");
            } else {
                result.put("success", false);
                result.put("message", "Failed to delete");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Error deleting");
        }

        out.print(gson.toJson(result));
    }
}