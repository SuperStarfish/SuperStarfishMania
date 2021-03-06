package cg.group4.server.database.query;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Removes the player data of the given id from the server.
 */
public class DeletePlayerData extends Query {

    /**
     * The id of the player.
     */
    protected final String cId;

    /**
     * Removes the player data of the given id from the server.
     *
     * @param id The id of the player.
     */
    public DeletePlayerData(final String id) {
        cId = id;
    }

    @Override
    public Serializable query(final Connection databaseConnection) throws SQLException {

        String preparedQuery = "DELETE FROM User WHERE Id = ? ";

        try (PreparedStatement statement = databaseConnection.prepareStatement(preparedQuery)) {
            statement.setString(1, cId);
            statement.executeUpdate();
        }

        return null;
    }
}
