package it.thedarksword.skillmastery.mysql.manager;

import ch.jalu.configme.SettingsManager;
import it.thedarksword.basement.api.Basement;
import it.thedarksword.basement.api.persistence.generic.connection.Connector;
import it.thedarksword.basement.api.persistence.generic.connection.TypeConnector;
import it.thedarksword.basement.api.persistence.maria.queries.builders.WhereBuilder;
import it.thedarksword.basement.api.persistence.maria.queries.builders.data.QueryBuilderInsert;
import it.thedarksword.basement.api.persistence.maria.queries.builders.data.QueryBuilderSelect;
import it.thedarksword.basement.api.persistence.maria.queries.builders.data.QueryBuilderUpdate;
import it.thedarksword.basement.api.persistence.maria.queries.builders.table.QueryBuilderCreateTable;
import it.thedarksword.basement.api.persistence.maria.structure.AbstractMariaDatabase;
import it.thedarksword.basement.api.persistence.maria.structure.AbstractMariaHolder;
import it.thedarksword.basement.api.persistence.maria.structure.column.MariaType;
import it.thedarksword.skillmastery.SkillMastery;
import it.thedarksword.skillmastery.config.SkillConfig;
import it.thedarksword.skillmastery.mysql.DatabaseConstants;
import it.thedarksword.skillmastery.player.SkillPlayer;
import it.thedarksword.skillmastery.skill.Skill;
import it.thedarksword.skillmastery.skill.SkillType;
import it.thedarksword.skillmastery.skill.skills.FarmingSkill;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.UUID;

public class QueryManager {

    @Getter
    @Accessors(fluent = true)
    private final AbstractMariaDatabase database;

    private final QueryBuilderInsert skillPlayerAddPattern;
    private final QueryBuilderSelect playerIdPattern;
    private final QueryBuilderSelect skillPlayerQueryPattern;
    private final QueryBuilderUpdate skillPlayerSavePattern;

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
                .addColumn("farming_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("farming_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)

                .addColumn("mining_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("mining_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)

                .addColumn("combat_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("combat_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)

                .addColumn("foraging_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("foraging_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)

                .addColumn("enchanting_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("enchanting_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)

                .addColumn("alchemy_level", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .addColumn("alchemy_exp", MariaType.INT, "0", QueryBuilderCreateTable.ColumnData.NOT_NULL)
                .withPrimaryKeys("id").build().exec();

        skillPlayerAddPattern =  database.insert().ignore(true).into(DatabaseConstants.SKILL_TABLE).columnSchema("player_id");
        playerIdPattern =  database.select().columns("id").from(DatabaseConstants.PLAYER_TABLE);
        skillPlayerQueryPattern =  database.select().columns("*").from(DatabaseConstants.SKILL_TABLE, DatabaseConstants.PLAYER_TABLE);
        skillPlayerSavePattern = database.update().table(DatabaseConstants.SKILL_TABLE);
    }

    public QueryBuilderInsert skillPlayerAdd(UUID uuid) {
        return skillPlayerAddPattern.patternClone()
                .values(playerIdPattern.patternClone().where(WhereBuilder.builder().equals("uuid", uuid.toString()).close())).build();
    }

    public QueryBuilderSelect skillPlayerQuery(UUID uuid) {
        return skillPlayerQueryPattern.patternClone()
                .where(WhereBuilder.builder().equalsNQ(DatabaseConstants.PLAYER_TABLE + ".id", "player_id").and()
                        .equals("uuid", uuid.toString()).close()).build();
    }

    public QueryBuilderUpdate skillPlayerSave(SkillPlayer skillPlayer) {
        QueryBuilderUpdate query = skillPlayerSavePattern.patternClone();
        for (Map.Entry<SkillType, Skill<?>> entry : skillPlayer.skills().entrySet()) {
            String name = entry.getKey().name().toLowerCase();
            query.set(name + "_level", entry.getValue().level()).set(name + "_exp", entry.getValue().exp());
        }
        return query.where(WhereBuilder.builder().equals("player_id", playerIdPattern.patternClone().where(
                WhereBuilder.builder().equals("uuid", skillPlayer.player().getUniqueId().toString()).close())).close()).build();
    }
}
