/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.commons.database.model;

import org.openjst.commons.database.model.sqlite.SQLiteModelFactory;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.openjst.commons.database.model.QueryWhere.*;

/**
 * @author Sergey Grachev
 */
public final class QuerySelectTest {

    @Test(groups = "manual")
    public void testInsert() {
        final Database db = SQLiteModelFactory.newInstance("test");
        final QueryInsert op = db.queryInsert().table("table")
                .value("field1", "val1").value("field2", "val2").value("field3", "val3");

        final List<Object> parameters = new LinkedList<Object>();
        final String sql = op.toSql(parameters);
        assertThat(parameters.size()).isEqualTo(3);
        System.out.println(sql + " " + parameters);
    }

    @Test(groups = "manual")
    public void testUpdate() {
        final Database db = SQLiteModelFactory.newInstance("test");
        final QueryUpdate op = db.queryUpdate().table("table")
                .set("field1", "val1").set("field2", "val2").set("field3", "val3")
                .where(and(
                        eq("fieldString", "string1"),
                        eq("fieldNumber1", 1L),
                        eq("fieldNumber2", 1),
                        eq("fieldNumber3", 1.1f),
                        or(
                                eq("fieldString11", "string11"),
                                eq("fieldString12", "string112")
                        )
                ));

        final List<Object> parameters = new LinkedList<Object>();
        final String sql = op.toSql(parameters);
        assertThat(parameters.size()).isEqualTo(9);
        System.out.println(sql + " " + parameters);
    }

    @Test(groups = "manual", enabled = false)
    public void testSelect() {
//    public static void main(String[] args) {

        final Database database = SQLiteModelFactory.newInstance("test");

        for (int i = 0; i < 1000; i++) {
            final QuerySelect op = database.querySelect().count("*").column("fieldString").column("fieldNumber1")
                    .from("table")
                    .where(and(
                            eq("fieldString", "string1"),
                            eq("fieldNumber1", 1L),
                            eq("fieldNumber2", 1),
                            eq("fieldNumber3", 1.1f),
                            eq("fieldNumber4", 1.1d),
                            and(
                                    eq("fieldString11", "string11"),
                                    eq("fieldString12", "string112")
                            )
                    )).orderBy(QueryOrderBy.desc("fieldString"), QueryOrderBy.asc("fieldNumber1"));

            op.toSql(new LinkedList<Object>());
        }

        final long time = System.currentTimeMillis();

        for (int i = 0; i < 1000000; i++) {
            final QuerySelect op = database.querySelect().count("*").column("fieldString").column("fieldNumber1")
                    .from("table")
                    .where(and(
                            eq("fieldString", "string1"),
                            eq("fieldNumber1", 1L),
                            eq("fieldNumber2", 1),
                            eq("fieldNumber3", 1.1f),
                            eq("fieldNumber4", 1.1d),
                            and(
                                    eq("fieldString11", "string11"),
                                    eq("fieldString12", "string112")
                            )
                    )).orderBy(QueryOrderBy.desc("fieldString"), QueryOrderBy.asc("fieldNumber1"));

            final List<Object> parameters = new LinkedList<Object>();
            final String sql = op.toSql(parameters);
            System.out.println(sql + " " + parameters);
        }

        System.out.println(System.currentTimeMillis() - time);
    }
}
