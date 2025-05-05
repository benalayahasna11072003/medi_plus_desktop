package services;

import entities.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    void insertOne(User user) throws SQLException;
    void updateOne(User user) throws SQLException;
    void deleteOne(User user) throws SQLException;
    List<User> selectAll() throws SQLException;
    User findByEmail(String email) throws SQLException;
    User findById(int id) throws SQLException;
    User login(String email, String password) throws SQLException;
} 