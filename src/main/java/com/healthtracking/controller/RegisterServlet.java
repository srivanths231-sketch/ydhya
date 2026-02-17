package com.healthtracking.controller;

import com.healthtracking.dao.UserDAO;
import com.healthtracking.model.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        try {
            BufferedReader reader = request.getReader();
            Map<String, String> userData = gson.fromJson(reader, Map.class);

            // Validate required fields
            if (userData.get("username") == null || userData.get("username").trim().isEmpty() ||
                    userData.get("email") == null || userData.get("email").trim().isEmpty() ||
                    userData.get("password") == null || userData.get("password").trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "All fields are required");
                out.print(gson.toJson(result));
                return;
            }

            // Check if username exists
            if (userDAO.isUsernameExists(userData.get("username"))) {
                result.put("success", false);
                result.put("message", "Username already exists");
                out.print(gson.toJson(result));
                return;
            }

            // Check if email exists
            if (userDAO.isEmailExists(userData.get("email"))) {
                result.put("success", false);
                result.put("message", "Email already registered");
                out.print(gson.toJson(result));
                return;
            }

            // Create user
            User user = new User(
                    userData.get("username"),
                    userData.get("email"),
                    userData.get("password"),
                    userData.get("fullName"),
                    Integer.parseInt(userData.get("age")),
                    userData.get("gender")
            );

            boolean registered = userDAO.registerUser(user);

            if (registered) {
                result.put("success", true);
                result.put("message", "Registration successful!");
            } else {
                result.put("success", false);
                result.put("message", "Registration failed. Please try again.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Server error: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}