package services;

import entities.Avis;
import utils.JDBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AvisService implements ICrud<Avis>{
    private Connection cnx = JDBConnection.getInstance().getCnx();
    @Override
    public void insertOne(Avis avis) throws SQLException {
        String req = "INSERT INTO `avis`( `id_user`, `professional_id`, `note`,`commentaire`) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        //ps.setInt(1, avis.getRef());
        ps.setInt(1, avis.getUser().getId());
        ps.setInt(2, avis.getUser().getId());
        ps.setInt(3, 3);
        ps.setString(4, avis.getCommentaire());
        ps.executeUpdate();
    }

    @Override
    public void updateOne(Avis avis) throws SQLException {

    }

    @Override
    public void deleteOne(Avis avis) throws SQLException {

    }

    @Override
    public List selectAll() throws SQLException {
        return null;
    }
}
