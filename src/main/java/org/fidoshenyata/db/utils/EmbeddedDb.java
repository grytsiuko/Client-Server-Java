package org.fidoshenyata.db.utils;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.embed.postgresql.Command;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.config.RuntimeConfigBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V11;

public class EmbeddedDb {
    static String url = "";

    static final EmbeddedPostgres postgres = new EmbeddedPostgres(V11);

    private static Logger logger = LoggerFactory.getLogger(EmbeddedDb.class);

    private static final URL dbStructureFile = ClassLoader.getSystemResource("db_structure.sql");

    public static String getUrl() {
        if (url.equals("")) {
            synchronized (url) {
                if (url.equals("")) {
                    try {
                        /* Creating own configuration, with own logger to disable ProgressListener flood while downloading PostgreSQL */
                        RuntimeConfigBuilder runtimeConfigBuilder = new RuntimeConfigBuilder();
                        IRuntimeConfig defaultConfigs = runtimeConfigBuilder.defaultsWithLogger(Command.Postgres, logger).build();

                        url = postgres.start(defaultConfigs);

                        try {
                            final Connection connection = DriverManager.getConnection(url);
                            final Statement statement = connection.createStatement();

                            Path dbStructure = Path.of(dbStructureFile.toURI());
                            String dbStructureQuery = Files.readString(dbStructure);
                            statement.execute(dbStructureQuery);

                            connection.close();
                        } catch (SQLException sqlException) {
                            // TODO: normal exception processing about creating demo DB structure failure
                            sqlException.printStackTrace();
                        } catch (URISyntaxException e) {
                            // TODO: normal exception processing about resource file processing
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return url;
    }

    public static void stop() {
        postgres.stop();
    }
}
