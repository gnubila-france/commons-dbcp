/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.dbcp2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

/**
 * A dummy {@link Statement}, for testing purposes.
 *
 * @author Rodney Waldhoff
 * @author Dirk Verbeeck
 * @version $Id$
 */
public class TesterStatement implements Statement {
    public TesterStatement(final Connection conn) {
        _connection = conn;
    }

    public TesterStatement(final Connection conn, final int resultSetType, final int resultSetConcurrency) {
        _connection = conn;
        _resultSetType = resultSetType;
        _resultSetConcurrency = resultSetConcurrency;
    }

    protected Connection _connection = null;
    protected boolean _open = true;
    protected int _rowsUpdated = 1;
    protected boolean _executeResponse = true;
    protected int _maxFieldSize = 1024;
    protected int _maxRows = 1024;
    protected boolean _escapeProcessing = false;
    protected int _queryTimeout = 1000;
    protected String _cursorName = null;
    protected int _fetchDirection = 1;
    protected int _fetchSize = 1;
    protected int _resultSetConcurrency = 1;
    protected int _resultSetType = 1;
    protected ResultSet _resultSet = null;

    @Override
    public ResultSet executeQuery(final String sql) throws SQLException {
        checkOpen();
        if("null".equals(sql)) {
            return null;
        }
        if("invalid".equals(sql)) {
            throw new SQLException("invalid query");
        }
        if ("broken".equals(sql)) {
            throw new SQLException("broken connection");
        }
        if("select username".equals(sql)) {
            final String username = ((TesterConnection) _connection).getUsername();
            final Object[][] data = {{username}};
            return new TesterResultSet(this, data);
        } else {
            // Simulate timeout if queryTimout is set to less than 5 seconds
            if (_queryTimeout > 0 && _queryTimeout < 5) {
                throw new SQLException("query timeout");
            }
            return new TesterResultSet(this);
        }
    }

    @Override
    public int executeUpdate(final String sql) throws SQLException {
        checkOpen();
        return _rowsUpdated;
    }

    @Override
    public void close() throws SQLException {
        // calling close twice has no effect
        if (!_open) {
            return;
        }

        _open = false;
        if (_resultSet != null) {
            _resultSet.close();
            _resultSet = null;
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkOpen();
        return _maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(final int max) throws SQLException {
        checkOpen();
        _maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        checkOpen();
        return _maxRows;
    }

    @Override
    public void setMaxRows(final int max) throws SQLException {
        checkOpen();
        _maxRows = max;
    }

    @Override
    public void setEscapeProcessing(final boolean enable) throws SQLException {
        checkOpen();
        _escapeProcessing = enable;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        checkOpen();
        return _queryTimeout;
    }

    @Override
    public void setQueryTimeout(final int seconds) throws SQLException {
        checkOpen();
        _queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        checkOpen();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkOpen();
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkOpen();
    }

    @Override
    public void setCursorName(final String name) throws SQLException {
        checkOpen();
        _cursorName = name;
    }

    @Override
    public boolean execute(final String sql) throws SQLException {
        checkOpen();
        if("invalid".equals(sql)) {
            throw new SQLException("invalid query");
        }
        return _executeResponse;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkOpen();
        if (_resultSet == null) {
            _resultSet = new TesterResultSet(this);
        }
        return _resultSet;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkOpen();
        return _rowsUpdated;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        checkOpen();
        return false;
    }

    @Override
    public void setFetchDirection(final int direction) throws SQLException {
        checkOpen();
        _fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkOpen();
        return _fetchDirection;
    }

    @Override
    public void setFetchSize(final int rows) throws SQLException {
        checkOpen();
        _fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkOpen();
        return _fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return _resultSetConcurrency;
    }

    @Override
    public int getResultSetType() throws SQLException {
        checkOpen();
        return _resultSetType;
    }

    @Override
    public void addBatch(final String sql) throws SQLException {
        checkOpen();
    }

    @Override
    public void clearBatch() throws SQLException {
        checkOpen();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        checkOpen();
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkOpen();
        return _connection;
    }

    protected void checkOpen() throws SQLException {
        if(!_open) {
            throw new SQLException("Connection is closed.");
        }
    }

    @Override
    public boolean getMoreResults(final int current) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return new TesterResultSet(this);
    }

    @Override
    public int executeUpdate(final String sql, final int autoGeneratedKeys)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public int executeUpdate(final String sql, final int columnIndexes[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public int executeUpdate(final String sql, final String columnNames[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean execute(final String sql, final int autoGeneratedKeys)
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean execute(final String sql, final int columnIndexes[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean execute(final String sql, final String columnNames[])
        throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        checkOpen();
        throw new SQLException("Not implemented.");
    }


    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean isClosed() throws SQLException {
        return !_open;
    }

    @Override
    public void setPoolable(final boolean poolable) throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new SQLException("Not implemented.");
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLException("Not implemented.");
    }
}
