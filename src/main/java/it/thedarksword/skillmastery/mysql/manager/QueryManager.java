package it.thedarksword.skillmastery.mysql.manager;

import ch.jalu.configme.SettingsManager;
import it.thedarksword.basement.api.Basement;
import it.thedarksword.basement.api.persistence.generic.connection.Connector;
import it.thedarksword.basement.api.persistence.generic.connection.TypeConnector;
import it.thedarksword.basement.api.persistence.maria.queries.builders.WhereBuilder;
import it.thedarksword.basement.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.thedarksword.basement.api.persistence.maria.queries.builders.table.QueryBuilderCreateTable;
import it.thedarksword.basement.api.persistence.maria.structure.AbstractMariaDatabase;
import it.thedarksword.basement.api.persistence.maria.structure.AbstractMariaHolder;
import it.thedarksword.basement.api.persistence.maria.structure.column.MariaType;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.config.SkillConfig;
import it.thedarksword.skillmastery.mysql.DatabaseConstants;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.UUID;

public class QueryManager {

    @Getter
    @Accessors(fluent = true)
    private final AbstractMariaDatabase database;

    private final QueryBuilderSelect skillPlayerQueryPattern;

    public QueryManager(Basement basement, SettingsManager config) {
        Connector connector = basement.getConnector(TypeConnector.MARIADB);
        connector.connect(config.getProperty(SkillConfig.MARIA_HOST),
                config.getProperty(SkillConfig.MARIA_USERNAME),
                config.getProperty(SkillConfig.MARIA_PASSWORD));
        AbstractMariaHolder holder = basement.getHolder(SkillMastery.class, AbstractMariaHolder.class);
        holder.setConnector(connector);
        database = holder.createDatabase("minecraft").ifNotExists(true).build().execReturn();

        database.createTable(DatabaseConstants.SKILL_TABLE).ifNotExists(true)
                .addColumn("id", MariaType.INT, QueryBuilderCreateTable.ColumnData.AUTO_INCREMENT)
                .addColumn("player_id", MariaType.INT, QueryBuilderCreateTable.ColumnData.UNIQUE)
                .addColumn("combat_level", MariaType.INT, QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .withPrimaryKeys("id").build().exec();

        skillPlayerQueryPattern =  database.select().columns("combat_level").from(DatabaseConstants.SKILL_TABLE, DatabaseConstants.PLAYER_TABLE);
    }

    public QueryBuilderSelect skillPlayerQuery(UUID uuid) {
        return skillPlayerQueryPattern.patternClone()
                .where(WhereBuilder.builder().equalsNQ(DatabaseConstants.PLAYER_TABLE + ".id", "player_id").and()
                        .equals("uuid", uuid.toString()).close()).build();
    }
}
