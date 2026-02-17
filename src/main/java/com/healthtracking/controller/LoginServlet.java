package com.healthtracking.controller;

import com.healthtracking.dao.UserDAO;
import com.healthtracking.model.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginServlet extends HttpServlet {
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
            Map<String, String> loginData = gson.fromJson(reader, Map.class);

            String username = loginData.get("username");
            String password = loginData.get("password");

            if (username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                result.put("success", false);
                result.put("message", "Username and password are required");
                out.print(gson.toJson(result));
                return;
            }

            User user = userDAO.loginUser(username, password);

            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());

                result.put("success", true);
                result.put("message", "Login successful");
                result.put("username", user.getUsername());
            } else {
                result.put("success", false);
                result.put("message", "Invalid username or password");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "Login failed: " + e.getMessage());
        }

        out.print(gson.toJson(result));
        out.flush();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            result.put("loggedIn", true);
            result.put("username", session.getAttribute("username"));
        } else {
            result.put("loggedIn", false);
        }

        out.print(gson.toJson(result));
        out.flush();
    }
}