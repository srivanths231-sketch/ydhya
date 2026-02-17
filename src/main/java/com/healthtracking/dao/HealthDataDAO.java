package com.healthtracking.dao;

import com.healthtracking.model.HealthData;
import com.healthtracking.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HealthDataDAO {

    // CREATE
    public boolean addHealthData(HealthData data) {
        String sql = "INSERT INTO health_data (user_id, date, weight, height, blood_pressure_systolic, " +
                "blood_pressure_diastolic, heart_rate, steps, sleep_hours, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, data.getUserId());
            pstmt.setDate(2, data.getDate());
            pstmt.setDouble(3, data.getWeight());
            pstmt.setDouble(4, data.getHeight());
            pstmt.setInt(5, data.getBloodPressureSystolic());
            pstmt.setInt(6, data.getBloodPressureDiastolic());
            pstmt.setInt(7, data.getHeartRate());
            pstmt.setInt(8, data.getSteps());
            pstmt.setDouble(9, data.getSleepHours());
            pstmt.setString(10, data.getNotes());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // READ ALL
    public List<HealthData> getUserHealthData(int userId) {
        List<HealthData> dataList = new ArrayList<>();
        String sql = "SELECT * FROM health_data WHERE user_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HealthData data = new HealthData();
                data.setId(rs.getInt("id"));
                data.setUserId(rs.getInt("user_id"));
                data.setDate(rs.getDate("date"));
                data.setWeight(rs.getDouble("weight"));
                data.setHeight(rs.getDouble("height"));
                data.setBloodPressureSystolic(rs.getInt("blood_pressure_systolic"));
                data.setBloodPressureDiastolic(rs.getInt("blood_pressure_diastolic"));
                data.setHeartRate(rs.getInt("heart_rate"));
                data.setSteps(rs.getInt("steps"));
                data.setSleepHours(rs.getDouble("sleep_hours"));
                data.setNotes(rs.getString("notes"));

                dataList.add(data);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    // READ ONE
    public HealthData getHealthDataById(int dataId) {
        String sql = "SELECT * FROM health_data WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dataId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                HealthData data = new HealthData();
                data.setId(rs.getInt("id"));
                data.setUserId(rs.getInt("user_id"));
                data.setDate(rs.getDate("date"));
                data.setWeight(rs.getDouble("weight"));
                data.setHeight(rs.getDouble("height"));
                data.setBloodPressureSystolic(rs.getInt("blood_pressure_systolic"));
                data.setBloodPressureDiastolic(rs.getInt("blood_pressure_diastolic"));
                data.setHeartRate(rs.getInt("heart_rate"));
                data.setSteps(rs.getInt("steps"));
                data.setSleepHours(rs.getDouble("sleep_hours"));
                data.setNotes(rs.getString("notes"));
                return data;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // UPDATE
    public boolean updateHealthData(HealthData data) {
        String sql = "UPDATE health_data SET date = ?, weight = ?, height = ?, " +
                "blood_pressure_systolic = ?, blood_pressure_diastolic = ?, " +
                "heart_rate = ?, steps = ?, sleep_hours = ?, notes = ? " +
                "WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, data.getDate());
            pstmt.setDouble(2, data.getWeight());
            pstmt.setDouble(3, data.getHeight());
            pstmt.setInt(4, data.getBloodPressureSystolic());
            pstmt.setInt(5, data.getBloodPressureDiastolic());
            pstmt.setInt(6, data.getHeartRate());
            pstmt.setInt(7, data.getSteps());
            pstmt.setDouble(8, data.getSleepHours());
            pstmt.setString(9, data.getNotes());
            pstmt.setInt(10, data.getId());
            pstmt.setInt(11, data.getUserId());

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE
    public boolean deleteHealthData(int dataId, int userId) {
        String sql = "DELETE FROM health_data WHERE id = ? AND user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dataId);
            pstmt.setInt(2, userId);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}