/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package biograkn.semmed;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "biograkn-semmed", mixinStandardHelpOptions = true)
public class Options {

    @Option(descriptionKey = "source",
            names = {"-s", "--source"},
            required = true,
            description = "Directory in which source CSV data files located in")
    private String source;

    @Option(descriptionKey = "grakn",
            names = {"-g", "--grakn"},
            required = true,
            description = "Grakn server address {host:port}")
    private String grakn;

    @Option(descriptionKey = "database",
            names = {"-d", "--database"},
            defaultValue = Migrator.DATABASE_NAME,
            description = "The database name to create in the Grakn server")
    private String database;

    @Option(descriptionKey = "parallelisation",
            names = {"-p", "--parallelisation"},
            defaultValue = "" + Integer.MIN_VALUE,
            description = "The number of threads to use (greater than zero and less than CPU cores)")
    private int parallelisation;

    @Option(descriptionKey = "batch",
            names = {"-b", "--batch"},
            defaultValue = "" + Migrator.BATCH_SIZE,
            description = "The number of queries that a transaction should batch in one commit")
    private int batch;

    public static Options parseCommandLine(String[] args) {
        final Options options = new Options();
        boolean proceed;
        final CommandLine command = new CommandLine(options);

        try {
            command.parseArgs(args);
            if (command.isUsageHelpRequested()) {
                command.usage(command.getOut());
                proceed = false;
            } else if (command.isVersionHelpRequested()) {
                command.printVersionHelp(command.getOut());
                proceed = false;
            } else {
                proceed = true;
            }
        } catch (CommandLine.ParameterException ex) {
            command.getErr().println(ex.getMessage());
            if (!CommandLine.UnmatchedArgumentException.printSuggestions(ex, command.getErr())) {
                ex.getCommandLine().usage(command.getErr());
            }
            proceed = false;
        }

        if (proceed) return options;
        else return null;
    }

    public Path source() {
        return Paths.get(source);
    }

    public String grakn() {
        return grakn;
    }

    public String database() {
        return database;
    }

    public int parallelisation() {
        if (parallelisation == Integer.MIN_VALUE) return Migrator.PARALLELISATION_MAX;
        else return parallelisation;
    }

    public int batch() {
        return batch;
    }
}