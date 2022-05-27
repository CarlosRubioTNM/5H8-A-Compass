package com.example.compass;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDAO {
    @Insert
    void registerUser(UserEntity entity);

    @Query("SELECT * FROM users WHERE username=(:name) AND password=(:pass)")
    UserEntity login(String name, String pass);
}
