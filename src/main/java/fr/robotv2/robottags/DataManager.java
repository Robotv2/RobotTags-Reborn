package fr.robotv2.robottags;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import fr.robotv2.robottags.player.TagPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.UUID;

public final class DataManager {

    private ConnectionSource source;
    private Dao<TagPlayer, UUID> gamePlayerDao;

    public enum StorageMode {
        SQLLITE,
        MYSQL;
    }

    protected void initialize(@NotNull ConnectionSource source) throws SQLException {
        this.source = source;
        this.gamePlayerDao = DaoManager.createDao(source, TagPlayer.class);
        TableUtils.createTableIfNotExists(source, TagPlayer.class);
    }

    protected void closeConnection() {
        this.source.closeQuietly();
    }

    @Nullable
    public TagPlayer getTagPlayer(@NotNull UUID playerUUID) {
        try {
            return this.gamePlayerDao.queryForId(playerUUID);
        } catch (SQLException e) {
            RobotTags.get().getLogger().warning("Couldn't query data for uuid: " + playerUUID);
            return null;
        }
    }

    public void saveTagPlayer(@NotNull TagPlayer data) {
        try {
            this.gamePlayerDao.createOrUpdate(data);
        } catch (SQLException e) {
            RobotTags.get().getLogger().warning("Couldn't save data for uuid:  " + data.getPlayerUniqueId());
        }
    }
}
