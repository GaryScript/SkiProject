package be.alb.dao;

import be.alb.models.Skier;
import java.sql.SQLException;
import java.util.List;

public interface SkierDAOInterface {

    List<Skier> getAllSkiers() throws SQLException;

    Skier getSkierById(int skierId) throws SQLException;

    int createSkier(Skier skier) throws SQLException;

    boolean updateSkier(Skier skier) throws SQLException;

    boolean deleteSkier(Skier skier) throws SQLException;
}
