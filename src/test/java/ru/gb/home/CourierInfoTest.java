package ru.gb.home;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CourierInfoTest extends AbstractTest{

    @Order(1)
    @ParameterizedTest
    @CsvSource({"John", "Kate"})
    void getCourierByName_whenValid_shouldReturn(String name) throws SQLException {
        //given
        String sql = "SELECT * FROM courier_info WHERE first_name='" + name + "'";
        Statement stmt  = getConnection().createStatement();
        String nameString = "";
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            nameString = rs.getString(2);
        }
        //then
        Assertions.assertEquals(name, nameString);
    }
}
